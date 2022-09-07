package kz.ncanode.service;

import kz.ncanode.configuration.CaConfiguration;
import kz.ncanode.configuration.SystemConfiguration;
import kz.ncanode.dto.crl.CrlResult;
import kz.ncanode.exception.CaException;
import kz.ncanode.exception.CrlException;
import kz.ncanode.wrapper.CertificateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Сервис для работы с центром сертификации
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CaService {
    private final static int EXIT_CODE = 32;
    private final static String CA_CACHE_DIR_NAME = "ca";
    private final static String CA_FILE_EXTENSION = ".cer";

    private final ApplicationContext applicationContext;
    private final CaConfiguration caConfiguration;
    private final SystemConfiguration systemConfiguration;
    private final CloseableHttpClient client;

    @Qualifier("caCrlService")
    private final CrlService caCrlService;


    @Retryable(value = CaException.class)
    @Scheduled(fixedRateString = "${ncanode.ca.ttl}", initialDelay = 0, timeUnit = TimeUnit.MINUTES)
    public void updateCache() {
        updateCache(false);
    }

    public void updateCache(boolean force) {
        var urls = caConfiguration.getUrlList();

        if (urls.isEmpty()) {
            log.error("CA certificates urls is empty. Please set NCANODE_CA_URL environment variable.");
            shutdown();
        }

        log.info("Updating CA certificates cache...");

        for (var urlEntry : urls.entrySet()) {
            File caFile = getCacheFilePathFor(urlEntry.getKey() + CA_FILE_EXTENSION);
            CertificateWrapper cert;

            if (force || !caFile.exists() || !caFile.canRead()) {
                cert = downloadCert(urlEntry.getValue(), caFile);
            } else {
                cert = CertificateWrapper.fromFile(caFile).orElseThrow();
            }

            checkCertForNull(urlEntry, cert, caFile);

            if (!cert.isDateValid() || caCrlService.verify(cert).getResult() == CrlResult.REVOKED) {
                downloadCert(urlEntry.getValue(), caFile);
                cert = downloadCert(urlEntry.getValue(), caFile);
            }

            checkCertForNull(urlEntry, cert, caFile);
        }
    }

    public CertificateWrapper downloadCert(URL url, File file) {
        try {
            log.info("Downloading CA file: {}", url.toString());
            download(url, file);
            return CertificateWrapper.fromFile(file).orElse(null);
        } catch (CaException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public void download(URL url, File file) {
        try (CloseableHttpResponse response = client.execute(new HttpGet(url.toString()))) {
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new CaException(String.format("Cannot download file: %s", url));
            }

            HttpEntity entity = response.getEntity();

            if (entity == null) {
                throw new CaException(String.format("Got empty request from: %s", url));
            }

            try(FileOutputStream out = new FileOutputStream(file)) {
                entity.writeTo(out);
            }
        } catch (IOException e) {
            throw new CaException(String.format("Cannot download file: %s", url), e);
        }
    }

    private void shutdown() {
        SpringApplication.exit(applicationContext, () -> EXIT_CODE);
        System.exit(EXIT_CODE);
    }

    private File getCacheFilePathFor(String fileName) {
        return new File(getCacheDir(), fileName);
    }

    private File getCacheDir() {
        val cacheDir = Paths.get(systemConfiguration.getCacheDir(), CA_CACHE_DIR_NAME).toFile();

        if ((!cacheDir.exists() || !cacheDir.isDirectory()) && !cacheDir.mkdirs()) {
            var err = String.format("Cannot create CA cache directory for: %s", cacheDir.getAbsolutePath());
            log.error(err);
            throw new CrlException(err);
        }

        return cacheDir;
    }

    private void checkCertForNull(final Map.Entry<String, URL> urlEntry, final CertificateWrapper cert, final File caFile) {
        if (cert == null) {
            log.error("Cannot open CA certificate from: '{}'. File name: {}", urlEntry.getValue().toString(), caFile.getAbsolutePath());
            shutdown();
            return;
        }
    }
}
