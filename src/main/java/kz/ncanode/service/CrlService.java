package kz.ncanode.service;

import kz.ncanode.configuration.properties.CrlConfigurationProperties;
import kz.ncanode.configuration.properties.SystemConfigurationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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

    @Scheduled(fixedRateString = "${ncanode.crl.ttl}", initialDelay = 0, timeUnit = TimeUnit.MINUTES)
    public void updateCache() {
        if (!crlConfigurationProperties.isEnabled()) {
            return;
        }

        log.info("Updating CRL cache...");


    }

    public void download(String url, Path path) {

    }

    private Path getCrlCacheFilename(String fileName) {
        return Paths.get(systemConfigurationProperties.getCacheDir(), CRL_CACHE_DIR_NAME, fileName);
    }
}
