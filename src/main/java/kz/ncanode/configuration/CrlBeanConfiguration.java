package kz.ncanode.configuration;

import kz.ncanode.configuration.crl.CrlConfiguration;
import kz.ncanode.service.CrlService;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.TaskScheduler;

@Configuration
public class CrlBeanConfiguration {

    @Primary
    @Bean
    public CrlService crlService(CrlConfiguration crlConfiguration,
                                 SystemConfiguration systemConfiguration, CloseableHttpClient client, TaskScheduler taskScheduler) {
        return new CrlService(systemConfiguration, crlConfiguration, client, taskScheduler, CrlService.CRL_DEFAULT);
    }

    @Qualifier("caCrlService")
    @Bean
    public CrlService caCrlService(@Qualifier("caCrlConfiguration") CrlConfiguration crlConfiguration,
                                   SystemConfiguration systemConfiguration, CloseableHttpClient client, TaskScheduler taskScheduler) {
        return new CrlService(systemConfiguration, crlConfiguration, client, taskScheduler, CrlService.CRL_CA);
    }
}
