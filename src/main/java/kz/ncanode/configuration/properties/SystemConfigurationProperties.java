package kz.ncanode.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ncanode.system")
@Getter
@Setter
public class SystemConfigurationProperties {
    private boolean detailedErrors;
}
