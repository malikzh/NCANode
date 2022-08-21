package kz.ncanode.service;

import kz.ncanode.configuration.properties.CrlConfigurationProperties;
import kz.ncanode.configuration.properties.SystemConfigurationProperties;
import kz.ncanode.exception.CrlException;
import kz.ncanode.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Сервис для реализоции механизма проверки сертификатов в CRL
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CrlService {
    private final static String CRL_CACHE_DIR_NAME = "crl";
    private final static String CRL_FILE_EXTENSION = ".crl";

    private final SystemConfigurationProperties systemConfigurationProperties;
    private final CrlConfigurationProperties crlConfigurationProperties;
    private final CloseableHttpClient client;

    @Scheduled(fixedRateString = "${ncanode.crl.ttl}", initialDelay = 0, timeUnit = TimeUnit.MINUTES)
    public void updateCache() {
        updateCache(false);
    }

    public void updateCache(boolean force) {
        if (!crlConfigurationProperties.isEnabled()) {
            return;
        }

        log.info("Updating CRL cache...");
        long currentTime = System.currentTimeMillis();

        int updatedCount = 0;

        // Удаляем старые файлы CRL
        for (var crlFile : getCrlFiles()) {
            if (!force && crlFile.exists() && crlFile.isFile() && crlFile.canRead() && (currentTime - crlFile.lastModified()) <= (long)crlConfigurationProperties.getTtl() * 60000L) {
                log.debug("CRL file {} is actual. This file will not be removed", crlFile);
                continue;
            }

            if (!crlFile.delete()) {
                log.error("Cannot delete CRL cache file: {}", crlFile);
            }
        }

        // Скачиваем новые CRL файлы
        for (var crlEntry : crlConfigurationProperties.getUrlList().entrySet()) {
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

    private void downloadCrl(URL url) {
        try {
            String crlUrl = url.toString();
            String crlFileName = Util.sha1(crlUrl) + CRL_FILE_EXTENSION;

            log.info("Downloading CRL file from: {}", crlFileName);
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
        return Paths.get(systemConfigurationProperties.getCacheDir(), CRL_CACHE_DIR_NAME, fileName);
    }

    private File getCacheDirectory() {
        return Paths.get(systemConfigurationProperties.getCacheDir(), CRL_CACHE_DIR_NAME).toFile();
    }

    private List<File> getCrlFiles() {
        return Arrays.stream(Objects.requireNonNull(getCacheDirectory().listFiles()))
            .filter(file -> file.isFile() && file.canRead() && file.getName().endsWith(CRL_FILE_EXTENSION))
            .collect(Collectors.toList());
    }
}
