package kz.ncanode.configuration.crl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Стандартная конфигурация CRL для запросов
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "ncanode.ca.crl")
@Qualifier("caCrlConfiguration")
public class CaCrlConfiguration extends CrlBaseConfiguration implements CrlConfiguration {

}
