package kz.ncanode.security;

import kz.gov.pki.kalkan.jce.provider.KalkanProvider;
import kz.gov.pki.kalkan.xmldsig.KncaXS;
import kz.ncanode.ioc.ServiceProvider;
import kz.ncanode.log.OutLogServiceProvider;

import java.security.Provider;
import java.security.Security;

public class KalkanServiceProvider implements ServiceProvider {
    private KalkanProvider provider = null;
    private OutLogServiceProvider out = null;

    public final static String KALKAN_VERSION = "0.4";

    public KalkanServiceProvider(OutLogServiceProvider out) {
        this.out = out;

        this.out.write("Initializing Kalkan crypto...");
        provider = new KalkanProvider();
        Security.addProvider(provider);
        KncaXS.loadXMLSecurity();
        this.out.write("Kalkan crypto initialized. Version: " + KALKAN_VERSION);
    }

    public Provider get() {
        return provider;
    }
}
