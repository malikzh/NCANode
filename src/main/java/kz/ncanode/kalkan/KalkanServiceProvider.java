package kz.ncanode.kalkan;

import kz.gov.pki.kalkan.jce.provider.KalkanProvider;
import kz.gov.pki.kalkan.xmldsig.KncaXS;
import kz.ncanode.ioc.ServiceProvider;
import kz.ncanode.log.OutLogServiceProvider;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.varia.NullAppender;

import java.security.Provider;
import java.security.Security;

/**
 * Обёртка для загрузки провайдера Kalkan
 */
public class KalkanServiceProvider implements ServiceProvider {
    private KalkanProvider provider = null;
    private OutLogServiceProvider out = null;

    public KalkanServiceProvider(OutLogServiceProvider out) {
        this.out = out;

        this.out.write("Initializing Kalkan crypto...");
        provider = new KalkanProvider();
        Security.addProvider(provider);
        BasicConfigurator.configure(new NullAppender());
        KncaXS.loadXMLSecurity();
        this.out.write("Kalkan crypto initialized. Version: " + getVersion());
    }

    public String getVersion() {
        return KalkanProvider.class.getPackage().getImplementationVersion();
    }

    public Provider get() {
        return provider;
    }
}
