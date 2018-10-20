package kz.ncanode.pki;

import kz.ncanode.config.ConfigServiceProvider;
import kz.ncanode.ioc.ServiceProvider;
import kz.ncanode.kalkan.KalkanServiceProvider;
import kz.ncanode.log.ErrorLogServiceProvider;
import kz.ncanode.log.OutLogServiceProvider;
import org.json.simple.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class PkiServiceProvider implements ServiceProvider {
    private ConfigServiceProvider   config = null;
    private OutLogServiceProvider   out    = null;
    private ErrorLogServiceProvider err    = null;
    private KalkanServiceProvider   kalkan = null;

    enum OCSPResult {
        ACTIVE,
        REVOKED,
        UNKNOWN
    }

    enum CRLResult {
        ACTIVE,
        REVOKED,
        UNKNOWN
    }

    public PkiServiceProvider(ConfigServiceProvider config, OutLogServiceProvider out, ErrorLogServiceProvider err, KalkanServiceProvider kalkan) {
        this.config = config;
        this.out    = out;
        this.err    = err;
        this.kalkan = kalkan;
    }

    public KeyStore loadKey(String file, String password) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore store = KeyStore.getInstance("PKCS12", kalkan.get());
        store.load(new FileInputStream(file), password.toCharArray());
        return store;
    }

    public KeyStore loadKey(byte[] p12, String password) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        KeyStore store = KeyStore.getInstance("PKCS12", kalkan.get());
        store.load(new ByteArrayInputStream(p12), password.toCharArray());
        return store;
    }

    public OCSPResult verifyOcsp(X509Certificate cert) {

        //

        return null;
    }

    public CRLResult verifyCrl(X509Certificate cert) {
        return null; //todo
    }

    public CRLResult verifyCrlDelta(X509Certificate cert) {
        return null; //todo
    }

    public JSONObject certInfo(X509Certificate cert, boolean verifyChain, boolean verifyOcsp, boolean verifyCrl) {
        return null; // todo
    }

}
