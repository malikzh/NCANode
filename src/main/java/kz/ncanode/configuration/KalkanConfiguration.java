package kz.ncanode.configuration;

import kz.gov.pki.kalkan.jce.provider.KalkanProvider;
import kz.gov.pki.kalkan.xmldsig.KncaXS;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class KalkanConfiguration {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public KalkanProvider kalkanProvider() {
        var kalkanProvider = new KalkanProvider();
        KncaXS.loadXMLSecurity();
        return kalkanProvider;
    }

}
