package kz.ncanode.configuration;

import kz.ncanode.util.Util;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URL;
import java.util.Map;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "ncanode.ocsp")
@Getter
@Setter
public class OcspConfiguration {
    private String url;

    public Map<String, URL> getUrlList() {
        return Util.urlMap(getUrl(), log);
    }
}
