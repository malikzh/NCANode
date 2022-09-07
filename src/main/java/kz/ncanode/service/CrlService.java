package kz.ncanode.service;

import kz.ncanode.configuration.SystemConfiguration;
import kz.ncanode.configuration.crl.CrlConfiguration;
import kz.ncanode.dto.crl.CrlResult;
import kz.ncanode.dto.crl.CrlStatus;
import kz.ncanode.exception.CrlException;
import kz.ncanode.exception.ServerException;
import kz.ncanode.util.Util;
import kz.ncanode.wrapper.CertificateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Сервис для реализоции механизма проверки сертификатов в CRL
 */
@Slf4j
@RequiredArgsConstructor
public class CrlService {
    public final static String CRL_DEFAULT = "default";
    public final static String CRL_CA      = "ca-crl";
    private final static String CRL_CACHE_DIR_NAME = "crl";
    private final static String CRL_FILE_EXTENSION = ".crl";

    private final SystemConfiguration systemConfiguration;
    private final CrlConfiguration crlConfiguration;
    private final CloseableHttpClient client;
    private final TaskScheduler taskScheduler;
    private final String crlServiceType;

    @PostConstruct
    private void initializeScheduler() {
        log.info("Initializing '{}' CRL Service...", crlServiceType);
        val periodicTrigger = new PeriodicTrigger(crlConfiguration.getTtl(), TimeUnit.MINUTES);
        periodicTrigger.setInitialDelay(0);
        periodicTrigger.setFixedRate(true);
        taskScheduler.schedule(() -> updateCache(false), periodicTrigger);
    }

    /**
     * Проверка сертификата в CRL
     *
     * @param cert Сертификат
     * @return Статус проверки
     */
    public CrlStatus verify(CertificateWrapper cert) {
        // Догружаем CRL из сертификата
        for (URL crlUrl : cert.getCrlList()) {
            File crlFile = getCrlCacheFilePathFor(crlUrl).toFile();

            if (crlFile.exists()) {
                continue;
            }

            downloadCrl(crlUrl);
        }

        // Проверяем в CRL
        for (var crlEntry : getLoadedCrlEntries().entrySet()) {
            if (crlEntry.getValue().isRevoked(cert.getX509Certificate())) {

                return Optional.ofNullable(crlEntry.getValue().getRevokedCertificate(cert.getX509Certificate()))
                    .map( entry -> CrlStatus.builder()
                        .result(CrlResult.REVOKED)
                        .file(crlEntry.getKey())
                        .revocationDate(entry.getRevocationDate())
                        .reason(Optional.ofNullable(entry.getRevocationReason()).map(CRLReason::toString).orElse(""))
                        .build()
                    ).orElse(CrlStatus.builder()
                        .result(CrlResult.REVOKED)
                        .build()
                    );
            }
        }

        return CrlStatus.builder()
            .result(CrlResult.ACTIVE)
            .build();
    }

    public Map<String, X509CRL> getLoadedCrlEntries() {
        return getCrlFiles().stream().collect(Collectors.toMap(File::getName, this::loadCrl));
    }

    /**
     * Обновляет кэш CRL
     *
     * @param force Если true, то кэш будет обновлен в любом случае
     */
    @CacheEvict("crls")
    public void updateCache(boolean force) {
        if (!crlConfiguration.isEnabled() || crlConfiguration.getTtl() <= 0) {
            return;
        }

        log.info("Updating CRL cache...");
        long currentTime = System.currentTimeMillis();

        // Удаляем старые файлы CRL
        for (var crlFile : getCrlFiles()) {
            if (!force && crlFile.exists() && crlFile.isFile() && crlFile.canRead() && (currentTime - crlFile.lastModified()) <= (long) crlConfiguration.getTtl() * 60000L) {
                log.debug("CRL file {} is actual. This file will not be removed", crlFile);
                continue;
            }

            if (!crlFile.delete()) {
                log.error("Cannot delete CRL cache file: {}", crlFile);
            }
        }

        int updatedCount = 0;

        // Скачиваем новые CRL файлы
        for (var crlEntry : crlConfiguration.getUrlList().entrySet()) {
            var crlFile = new File(getCacheDirectory(), crlEntry.getKey() + CRL_FILE_EXTENSION);

            if (crlFile.exists()) {
                continue;
            }

            downloadCrl(crlEntry.getValue());
            updatedCount++;
        }

        if (updatedCount == 0) {
            log.info("Nothing to update in CRL cache.");
        } else {
            log.info("{} files updated in CRL cache", updatedCount);
        }
    }


    /**
     * Загружает CRL файл
     *
     * @param file
     * @return
     */
    @Cacheable(value = "crls", key = "#file.absolutePath")
    public X509CRL loadCrl(File file) {
        try (FileInputStream in = new FileInputStream(file)) {
            return (X509CRL) CertificateFactory.getInstance("X.509").generateCRL(in);
        } catch (IOException | CRLException | CertificateException e) {
            log.error("Cannot load CRL file \"{}\"", file, e);
            throw new ServerException(String.format("Cannot load CRL file \"%s\"", file.getName()), e);
        }
    }

    /**
     * Скачивает CRL файл в директорию
     *
     * @param url
     */
    public void downloadCrl(URL url) {
        try {
            String crlUrl = url.toString();
            String crlFileName = Util.sha1(crlUrl) + CRL_FILE_EXTENSION;

            log.info("Downloading CRL file from: {}", crlUrl);
            final File downloadedFile = download(crlUrl, getCrlCacheFilePathFor(crlFileName));
            log.info("CRL file \"{}\" successfully downloaded. Size: {} bytes", crlFileName, downloadedFile.length());
        } catch (CrlException e) {
            log.error("CRL File download failure", e.getCause());
        }
    }

    private File download(String url, Path path) throws CrlException {
        try(CloseableHttpResponse response = client.execute(new HttpGet(url))) {
            int status = response.getStatusLine().getStatusCode();

            if (status != HttpStatus.OK.value()) {
                throw new CrlException(String.format("Cannot download file from: %s. Got HTTP status: %d", url, status));
            }

            HttpEntity entity = response.getEntity();

            if (entity == null) {
                throw new CrlException(String.format("Got empty request from: %s", url));
            }

            var file = path.toFile();

            try(FileOutputStream out = new FileOutputStream(file)) {
                entity.writeTo(out);
            }

            return file;
        } catch (IOException e) {
            throw new CrlException(e.getMessage(), e);
        }
    }

    private Path getCrlCacheFilePathFor(String fileName) {
        return Paths.get(systemConfiguration.getCacheDir(), CRL_CACHE_DIR_NAME, fileName);
    }

    private Path getCrlCacheFilePathFor(URL url) {
        return getCrlCacheFilePathFor(Util.sha1(url.toString()) + CRL_FILE_EXTENSION);
    }

    private File getCacheDirectory() {
        return Paths.get(systemConfiguration.getCacheDir(), CRL_CACHE_DIR_NAME).toFile();
    }

    private List<File> getCrlFiles() {
        return Arrays.stream(Objects.requireNonNull(getCacheDirectory().listFiles()))
            .filter(file -> file.isFile() && file.canRead() && file.getName().endsWith(CRL_FILE_EXTENSION))
            .collect(Collectors.toList());
    }
}
