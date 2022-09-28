package kz.ncanode.service;

import kz.ncanode.configuration.SystemConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class DirectoryService {
    private final SystemConfiguration systemConfiguration;

    /**
     * Возвращает путь к кэшу
     * @param dirName
     * @return
     */
    public Optional<File> getCachePathFor(final String dirName) {
        val file = Paths.get(systemConfiguration.getCacheDir(), dirName).normalize().toAbsolutePath().toFile();

        if ((!file.exists() || !file.isDirectory()) && !file.mkdirs()) {
            log.error("Cannot get cache path for: {}", file);
            return Optional.empty();
        }

        return Optional.of(file);
    }
}
