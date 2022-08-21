package kz.ncanode.configuration.properties;

import kz.ncanode.util.Util;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "ncanode.crl")
@Getter
@Setter
public class CrlConfigurationProperties {
    private boolean enabled;
    private Integer ttl;
    private String url;

    public Map<String, URL> getUrlList() {
        return Arrays.stream(url.split("\\s+"))
            .map(CrlConfigurationProperties::createNewUrl)
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(
                url -> Util.sha1(url.toString()),
                Function.identity()
            ));
    }

    private static URL createNewUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            log.error("Cannot parse CRL url: {}", url, e);
            return null;
        }
    }
}
