package kz.ncanode.service;

import kz.ncanode.configuration.properties.CrlConfigurationProperties;
import kz.ncanode.configuration.properties.SystemConfigurationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * Сервис для реализоции механизма проверки сертификатов в CRL
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CrlService {
    private final static String CRL_CACHE_DIR_NAME = "crl";

    private final SystemConfigurationProperties systemConfigurationProperties;
    private final CrlConfigurationProperties crlConfigurationProperties;
    private final CloseableHttpClient client;

    @Scheduled(fixedRateString = "${ncanode.crl.ttl}", initialDelay = 0, timeUnit = TimeUnit.MINUTES)
    public void updateCache() {
        if (!crlConfigurationProperties.isEnabled()) {
            return;
        }

        log.info("Updating CRL cache...");

    }

    /**
     * Скачивает CRL файл
     *
     * @param url
     * @param path
     */
    private void download(String url, Path path) {
        try(CloseableHttpResponse response = client.execute(new HttpGet(url))) {
            int status = response.getStatusLine().getStatusCode();

            if (status != HttpStatus.OK.value()) {
                log.error("Cannot download file: {}", url);
                return;
            }

            HttpEntity entity = response.getEntity();

            if (entity == null) {
                log.error("Got empty request: {}", url);
                return;
            }

            try(FileOutputStream out = new FileOutputStream(path.toFile())) {
                entity.writeTo(out);
            }
        } catch (IOException e) {
            log.error("Cannot download file from url: {}", url, e);
        }
    }

    private Path getCrlCacheFilename(String fileName) {
        return Paths.get(systemConfigurationProperties.getCacheDir(), CRL_CACHE_DIR_NAME, fileName);
    }
}
