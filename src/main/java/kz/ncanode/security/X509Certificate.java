package kz.ncanode.security;

import kz.gov.pki.kalkan.jce.provider.KalkanProvider;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

public abstract class X509Certificate extends java.security.cert.X509Certificate {

    public static java.security.cert.X509Certificate createFrom(String file) throws CertificateException, NoSuchProviderException, FileNotFoundException {
        return (java.security.cert.X509Certificate)java.security.cert.CertificateFactory.getInstance("X.509", KalkanProvider.PROVIDER_NAME).generateCertificate(new FileInputStream(file));
    }

    public static java.security.cert.X509Certificate createFrom(byte[] cert) throws CertificateException, NoSuchProviderException {
        return (java.security.cert.X509Certificate)java.security.cert.CertificateFactory.getInstance("X.509", KalkanProvider.PROVIDER_NAME).generateCertificate(new ByteArrayInputStream(cert));
    }

    /**
     * Возвращает подробную инфомрацию о сертификате
     *
     * @return
     */
    public JSONObject info() {
        // todo
        return null;
    }

    /**
     * Строит цепочку сертификатов, до корневого НУЦ
     * @return
     */
    public JSONArray chain() {
        // todo
        return null;
    }

    public void verifyOcsp() {
        //todo
    }

    public void verifyCrl() {
        //todo
    }
}
