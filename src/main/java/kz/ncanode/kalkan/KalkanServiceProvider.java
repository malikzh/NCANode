package kz.ncanode.kalkan;

import kz.gov.pki.kalkan.jce.provider.KalkanProvider;
import kz.gov.pki.kalkan.jce.provider.cms.CMSException;
import kz.gov.pki.kalkan.jce.provider.cms.CMSSignedData;
import kz.gov.pki.kalkan.jce.provider.cms.SignerInformation;
import kz.gov.pki.kalkan.jce.provider.cms.SignerInformationStore;
import kz.gov.pki.kalkan.xmldsig.KncaXS;
import kz.ncanode.ioc.ServiceProvider;
import kz.ncanode.log.OutLogServiceProvider;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.varia.NullAppender;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    /**
     * Возвращает сертификаты, которым был подписан переданный документ
     */
    public List<X509Certificate> getCertificatesFromCmsSignedData(CMSSignedData cms) throws
            NoSuchAlgorithmException,
            NoSuchProviderException,
            CMSException,
            CertStoreException
    {
        List<X509Certificate> certs = new ArrayList<>();
        SignerInformationStore signers = cms.getSignerInfos();
        String providerName = this.provider.getName();
        CertStore clientCerts = cms.getCertificatesAndCRLs("Collection", providerName);

        for (Object signerObj : signers.getSigners()) {
            SignerInformation signer = (SignerInformation) signerObj;
            X509CertSelector signerConstraints = signer.getSID();
            Collection<? extends Certificate> certCollection = clientCerts.getCertificates(signerConstraints);

            for (Certificate certificate : certCollection) {
                X509Certificate cert = (X509Certificate) certificate;
                certs.add(cert);
            }
        }

        return certs;
    }
}
