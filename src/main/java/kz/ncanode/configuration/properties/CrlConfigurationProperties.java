package kz.ncanode.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ncanode.crl")
@Getter
@Setter
public class CrlConfigurationProperties {
    private boolean enabled;
    private Integer ttl;
}
