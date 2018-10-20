package kz.ncanode.pki;

import kz.gov.pki.kalkan.asn1.ASN1InputStream;
import kz.gov.pki.kalkan.asn1.DERObject;
import kz.gov.pki.kalkan.asn1.DEROctetString;
import kz.gov.pki.kalkan.asn1.ocsp.OCSPObjectIdentifiers;
import kz.gov.pki.kalkan.asn1.x509.X509Extension;
import kz.gov.pki.kalkan.asn1.x509.X509Extensions;
import kz.gov.pki.kalkan.ocsp.*;
import kz.ncanode.config.ConfigServiceProvider;
import kz.ncanode.ioc.ServiceProvider;
import kz.ncanode.kalkan.KalkanServiceProvider;
import kz.ncanode.log.ErrorLogServiceProvider;
import kz.ncanode.log.OutLogServiceProvider;
import org.json.simple.JSONObject;

import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Hashtable;

public class PkiServiceProvider implements ServiceProvider {
    private ConfigServiceProvider   config = null;
    private OutLogServiceProvider   out    = null;
    private ErrorLogServiceProvider err    = null;
    private KalkanServiceProvider   kalkan = null;

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

    public OCSPStatus verifyOcsp(X509Certificate cert, X509Certificate issuerCert) {
        String ocspUrl = config.get("pki", "ocsp_url");

        byte[] nonce = generateOcspNonce();

        OCSPReq ocspRequest = null;

        try {
            ocspRequest = buildOcspRequest(cert.getSerialNumber(), issuerCert, CertificateID.HASH_SHA256, nonce);
        } catch (OCSPException e) {
            e.printStackTrace();
            return new OCSPStatus(OCSPStatus.OCSPResult.UNKNOWN, null, 0);
        }

        // make request
        try {
            URL url = new URL(ocspUrl);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/ocsp-request");
            OutputStream os = connection.getOutputStream();
            os.write(ocspRequest.getEncoded());
            os.close();

            InputStream response = connection.getInputStream();
            OCSPStatus res = processOcspResponse(response, nonce);
            response.close();
            return res;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (OCSPException e) {
            e.printStackTrace();
        }

        // read response

        return null;
    }

    public JSONObject certInfo(X509Certificate cert) {
        return null; // todo
    }


    // private
    private OCSPReq buildOcspRequest(BigInteger serialNumber, X509Certificate issuerCert, String hashAlg, byte[] nonce) throws OCSPException {
        OCSPReqGenerator ocspReqGenerator = new OCSPReqGenerator();

        CertificateID certId = new CertificateID(hashAlg, issuerCert, serialNumber, kalkan.get().getName());

        ocspReqGenerator.addRequest(certId);

        Hashtable x509Extensions = new Hashtable();

        // добавляем nonce
        x509Extensions.put(OCSPObjectIdentifiers.id_pkix_ocsp_nonce, new X509Extension(false, new DEROctetString(new DEROctetString(nonce))) {});
        ocspReqGenerator.setRequestExtensions(new X509Extensions(x509Extensions));

        return ocspReqGenerator.generate();
    }

    private byte[] generateOcspNonce() {
        byte[] nonce = new byte[8];
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(nonce);

        return nonce;
    }

    private OCSPStatus processOcspResponse(InputStream response, byte[] nonce) throws IOException, OCSPException, NoSuchProviderException {
        OCSPResp resp = new OCSPResp(response);

        if (resp.getStatus() != 0) {
            throw new OCSPException("Unsuccessful request. Status: "
                    + resp.getStatus());
        }

        BasicOCSPResp brep = (BasicOCSPResp) resp.getResponseObject();
        byte[] respNonceExt = brep
                .getExtensionValue(OCSPObjectIdentifiers.id_pkix_ocsp_nonce
                        .getId());

        if (respNonceExt != null) {
            ASN1InputStream asn1In = new ASN1InputStream(respNonceExt);
            DERObject derObj = asn1In.readObject();
            asn1In.close();
            byte[] extV = DEROctetString.getInstance(derObj).getOctets();
            asn1In = new ASN1InputStream(extV);
            derObj = asn1In.readObject();
            asn1In.close();

            if (!Arrays.equals(nonce, DEROctetString.getInstance(derObj).getOctets())) {
                throw new OCSPException("Nonce aren't equals.");
            }
        }

        SingleResp[] singleResps = brep.getResponses();
        SingleResp singleResp = singleResps[0];
        Object status = singleResp.getCertStatus();


        if (status == null) {
            return new OCSPStatus(OCSPStatus.OCSPResult.ACTIVE, null, 0);
        }
        else if (status instanceof RevokedStatus) {
            RevokedStatus rev = (RevokedStatus)status;
            return new OCSPStatus(OCSPStatus.OCSPResult.REVOKED, rev.getRevocationTime(), rev.getRevocationReason());
        }

        return new OCSPStatus(OCSPStatus.OCSPResult.UNKNOWN, null, 0);
    }

}
