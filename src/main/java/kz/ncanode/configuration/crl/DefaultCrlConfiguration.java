package kz.ncanode.configuration.crl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Стандартная конфигурация CRL для запросов
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "ncanode.crl")
@Primary
public class DefaultCrlConfiguration extends CrlBaseConfiguration implements CrlConfiguration {

}
