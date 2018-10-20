package kz.ncanode.pki;

import kz.gov.pki.kalkan.jce.provider.KalkanProvider;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class X509Manager {
    private static final String OCSP_URL = "http://ocsp.pki.gov.kz";

    public static X509Certificate load(String file) throws CertificateException, NoSuchProviderException, FileNotFoundException {
        return (X509Certificate)java.security.cert.CertificateFactory.getInstance("X.509", KalkanProvider.PROVIDER_NAME).generateCertificate(new FileInputStream(file));
    }

    public static X509Certificate load(byte[] cert) throws CertificateException, NoSuchProviderException {
        return (X509Certificate)java.security.cert.CertificateFactory.getInstance("X.509", KalkanProvider.PROVIDER_NAME).generateCertificate(new ByteArrayInputStream(cert));
    }
}
