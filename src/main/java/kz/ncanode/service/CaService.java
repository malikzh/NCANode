package kz.ncanode.service;

import kz.ncanode.configuration.properties.CaConfigurationProperties;
import kz.ncanode.configuration.properties.SystemConfigurationProperties;
import kz.ncanode.exception.CaException;
import kz.ncanode.wrapper.CertificateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private final CaConfigurationProperties caConfigurationProperties;
    private final SystemConfigurationProperties systemConfigurationProperties;
    private final CloseableHttpClient client;


    @Scheduled(fixedRateString = "${ncanode.ca.ttl}", initialDelay = 0, timeUnit = TimeUnit.MINUTES)
    public void updateCache() {
        updateCache(false);
    }

    public void updateCache(boolean force) {
        var urls = caConfigurationProperties.getUrlList();

        if (urls.isEmpty()) {
            log.error("CA certificates urls is empty. Please set NCANODE_CA_URL environment variable.");
            shutdown();
        }

        log.info("Updating CA certificates cache...");

        for (var urlEntry : urls.entrySet()) {
            File caFile = getCacheFilePathFor(urlEntry.getKey() + CA_FILE_EXTENSION).toFile();
            CertificateWrapper cert;


            if (!caFile.exists() || !caFile.canRead()) {
                cert = downloadCert(urlEntry.getValue(), caFile);
            } else {
                cert = CertificateWrapper.fromFile(caFile).orElseThrow();
            }

            if (cert == null) {
                log.error("Cannot open CA certificate from: '{}'. FIle name: {}", urlEntry.getValue().toString(), caFile.getAbsolutePath());
                shutdown();
                return;
            }

            if (!cert.isDateValid()) {
                downloadCert(urlEntry.getValue(), caFile);
                cert = downloadCert(urlEntry.getValue(), caFile);
            }
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

    public void download(URL url, File file) throws CaException {
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

    private Path getCacheFilePathFor(String fileName) {
        return Paths.get(systemConfigurationProperties.getCacheDir(), CA_CACHE_DIR_NAME, fileName);
    }
}
