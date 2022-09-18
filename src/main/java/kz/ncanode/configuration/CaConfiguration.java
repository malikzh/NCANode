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
@ConfigurationProperties(prefix = "ncanode.ca")
@Getter
@Setter
public class CaConfiguration {
    private boolean enabled = true;
    private String url;
    private Integer ttl;

    public Map<String, URL> getUrlList() {
        return Util.urlMap(getUrl(), log);
    }
}
