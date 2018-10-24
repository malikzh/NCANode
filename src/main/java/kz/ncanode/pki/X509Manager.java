package kz.ncanode.pki;

import kz.gov.pki.kalkan.jce.provider.KalkanProvider;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Хелпер для загрузки сертификатов
 */
public class X509Manager {
    public static X509Certificate load(String file) throws CertificateException, NoSuchProviderException, IOException {
        FileInputStream stream = new FileInputStream(file);
        X509Certificate cert = (X509Certificate)java.security.cert.CertificateFactory.getInstance("X.509", KalkanProvider.PROVIDER_NAME).generateCertificate(stream);
        stream.close();
        return cert;
    }

    public static X509Certificate load(byte[] cert) throws CertificateException, NoSuchProviderException, IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream(cert);
        X509Certificate ret = (X509Certificate)java.security.cert.CertificateFactory.getInstance("X.509", KalkanProvider.PROVIDER_NAME).generateCertificate(stream);
        stream.close();
        return ret;
    }
}
