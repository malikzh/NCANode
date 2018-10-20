package kz.ncanode.pki;

import kz.gov.pki.kalkan.jce.provider.KalkanProvider;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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

    /**
     * Возвращает подробную инфомрацию о сертификате
     *
     * @return
     */
    public static JSONObject info(X509Certificate cert) {
        // todo
        return null;
    }

    public static void verifyOcsp(X509Certificate cert) {
        //todo
    }

    public static void verifyCrl() {
        //todo
    }

    public static void verifyCrlDelta() {
        //todo
    }
}
