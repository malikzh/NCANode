package kz.ncanode.pki;

import kz.gov.pki.kalkan.asn1.ASN1InputStream;
import kz.gov.pki.kalkan.asn1.DERObject;
import kz.gov.pki.kalkan.asn1.DEROctetString;
import kz.gov.pki.kalkan.asn1.ocsp.OCSPObjectIdentifiers;
import kz.gov.pki.kalkan.asn1.x509.X509Extension;
import kz.gov.pki.kalkan.asn1.x509.X509Extensions;
import kz.gov.pki.kalkan.ocsp.*;
import kz.ncanode.Helper;
import kz.ncanode.api.exceptions.InvalidArgumentException;
import kz.ncanode.config.ConfigServiceProvider;
import kz.ncanode.ioc.ServiceProvider;
import kz.ncanode.kalkan.KalkanServiceProvider;
import kz.ncanode.log.ErrorLogServiceProvider;
import kz.ncanode.log.OutLogServiceProvider;
import org.json.simple.JSONObject;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * Класс для работы с PKI.GOV.KZ. Здесь содержатся реализации OCSP и
 * управление сертификатами.
 */
public class PkiServiceProvider implements ServiceProvider {
    private ConfigServiceProvider config = null;
    private OutLogServiceProvider out = null;
    private ErrorLogServiceProvider err = null;
    private KalkanServiceProvider kalkan = null;
    private CrlServiceProvider crl = null;

    public PkiServiceProvider(ConfigServiceProvider config, OutLogServiceProvider out, ErrorLogServiceProvider err, KalkanServiceProvider kalkan, CrlServiceProvider crl) {
        this.config = config;
        this.out = out;
        this.err = err;
        this.kalkan = kalkan;
        this.crl = crl;
    }

    public KeyStore loadKey(String file, String password) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore store = KeyStore.getInstance("PKCS12", kalkan.get());
        FileInputStream fs = new FileInputStream(file);

        store.load(fs, password.toCharArray());
        fs.close();

        return store;
    }

    public KeyStore loadKey(byte[] p12, String password) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        KeyStore store = KeyStore.getInstance("PKCS12", kalkan.get());

        ByteArrayInputStream bs = new ByteArrayInputStream(p12);
        store.load(bs, password.toCharArray());
        bs.close();

        return store;
    }

    public OCSPStatus verifyOcsp(X509Certificate cert, X509Certificate issuerCert) throws IOException, OCSPException {
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
        InputStream response = null;
        OCSPStatus res = null;
        try {
            URL url = new URL(ocspUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/ocsp-request");
            OutputStream os = connection.getOutputStream();
            os.write(ocspRequest.getEncoded());
            os.close();

            response = connection.getInputStream();
            res = processOcspResponse(response, nonce);
            connection.disconnect();
            return res;
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public JSONObject certInfo(X509Certificate cert, boolean verifyOcsp, boolean verifyCrl, X509Certificate issuerCert) throws CertificateParsingException, IOException, InvalidArgumentException {
        if (verifyCrl && !crl.isEnabled()) {
            throw new InvalidArgumentException(
                    "CRL verification is disabled. Turn it on in service configuration with 'crl_enabled=true'."
            );
        }

        JSONObject response = new JSONObject();

        ArrayList<?> userType = keyUser(cert);


        // subject: commonName, country, orgName, province, locality, iin, bin, email, birthDate, gender
        // keyUsage: для подписи или авторизации
        // keyUser: [] // Физ лицо или юр лицо
        // issuer: commonName, country, orgName, province, locality
        // signAlg
        // notBefore (DateTime)
        // notAfter (DateTime)
        // valid:
        // trusted:
        // ocsp: (option for ocsp)
        // crl: (option for crl)
        // Public key
        // Sign
        Date currentDate = new Date();
        response.put("subject", subjectInfo(cert));
        response.put("issuer", issuerInfo(cert));
        response.put("keyUser", userType);
        response.put("keyUsage", keyUsage(cert));
        response.put("signAlg", cert.getSigAlgName());
        response.put("notBefore", Helper.dateTime(cert.getNotBefore()));
        response.put("notAfter", Helper.dateTime(cert.getNotAfter()));
        response.put("publicKey", new String(Base64.getEncoder().encode(cert.getPublicKey().getEncoded())));
        response.put("sign", new String(Base64.getEncoder().encode(cert.getSignature())));
        response.put("serialNumber", String.valueOf(cert.getSerialNumber()));
        response.put("valid", currentDate.after(cert.getNotBefore()) && currentDate.before(cert.getNotAfter()));

        if (verifyOcsp) {
            OCSPStatus ocspStatus = null;
            JSONObject ocspvJson = new JSONObject();

            try {
                ocspStatus = verifyOcsp(cert, issuerCert);
            } catch (OCSPException e) {
                ocspvJson.put("error", e.getMessage());
            }

            if (ocspStatus != null) {
                Date revokationTime = ocspStatus.getRevokationTime();

                if (revokationTime != null && (Boolean) response.get("valid")) {
                    response.put("valid", revokationTime.after(currentDate));
                }

                ocspvJson.put("status", ocspStatus.getStatus().toString());
                ocspvJson.put("revokationReason", ocspStatus.getRevokationReason());
                ocspvJson.put("revokationTime", revokationTime != null ? Helper.dateTime(revokationTime) : null);
            }

            response.put("ocsp", ocspvJson);
        }

        if (verifyCrl) {
            crl.updateCache(false);
            CrlStatus crlStatus = crl.verify(cert);

            Date revokationTime = crlStatus.getDate();
            String revokationReason = crlStatus.getReason();

            String revtime = "";

            if (revokationTime != null) {
                revtime = Helper.dateTime(revokationTime);

                if ((Boolean) response.get("valid")) {
                    response.put("valid", revokationTime.after(currentDate));
                }
            }

            JSONObject crlJson = new JSONObject();

            if (crlStatus != null) {
                crlJson.put("status", crlStatus.getStatus().toString());
                crlJson.put("revokedBy", crlStatus.getRevokedBy());
                crlJson.put("revokationTime", revtime);
                crlJson.put("revokationReason", revokationReason);
            }

            response.put("crl", crlJson);
        }

        return response;
    }

    public static JSONObject issuerInfo(java.security.cert.X509Certificate cert) {
        JSONObject issuer = new JSONObject();

        String dn = cert.getIssuerDN().toString();
        issuer.put("dn", dn);

        LdapName ldapName = null;

        try {
            ldapName = new LdapName(dn);

            for (Rdn rdn : ldapName.getRdns()) {
                parseRdn(rdn, issuer);
            }
        } catch (InvalidNameException e) {
            e.printStackTrace();
        }

        return issuer;
    }

    public static JSONObject subjectInfo(java.security.cert.X509Certificate cert) {
        JSONObject subject = new JSONObject();

        String dn = cert.getSubjectDN().toString();

        subject.put("dn", dn);

        LdapName ldapName = null;
        String iin = "";

        try {
            ldapName = new LdapName(dn);

            for (Rdn rdn : ldapName.getRdns()) {
                parseRdn(rdn, subject);
            }
        } catch (InvalidNameException e) {
            e.printStackTrace();
        }


        // add iin info
        iin = (String) subject.get("iin");
        if (iin != null && iin.length() == 12) {
            String birthYear = iin.substring(0, 2);
            String birthMonth = iin.substring(2, 4);
            String birthDay = iin.substring(4, 6);
            String birthAge = iin.substring(6, 7);
            String gender = "";

            if (birthAge.equals("1")) {
                birthYear = "18" + birthYear;
                gender = "MALE";
            } else if (birthAge.equals("2")) {
                birthYear = "18" + birthYear;
                gender = "FEMALE";
            } else if (birthAge.equals("3")) {
                birthYear = "19" + birthYear;
                gender = "MALE";
            } else if (birthAge.equals("4")) {
                birthYear = "19" + birthYear;
                gender = "FEMALE";
            } else if (birthAge.equals("5")) {
                birthYear = "20" + birthYear;
                gender = "MALE";
            } else if (birthAge.equals("6")) {
                birthYear = "20" + birthYear;
                gender = "FEMALE";
            }

            subject.put("birthDate", birthYear + "-" + birthMonth + "-" + birthDay);
            subject.put("gender", gender);

        }


        return subject;
    }

    public static String keyUsage(X509Certificate cert) {
        boolean[] ku = cert.getKeyUsage();

        if (ku[0] && ku[1]) {
            return "SIGN";
        } else if (ku[0] && ku[2]) {
            return "AUTH";
        } else {
            return "UNKNOWN";
        }
    }

    public static ArrayList<String> keyUser(X509Certificate cert) throws CertificateParsingException {
        ArrayList<String> result = new ArrayList<>();

        if (cert.getExtendedKeyUsage() == null) {
            return result;
        }

        for (String item : cert.getExtendedKeyUsage()) {
            if (item.equals("1.2.398.3.3.4.1.1")) {
                result.add("INDIVIDUAL");
            } else if (item.equals("1.2.398.3.3.4.1.2")) {
                result.add("ORGANIZATION");
            } else if (item.equals("1.2.398.3.3.4.1.2.1")) {
                result.add("CEO");
            } else if (item.equals("1.2.398.3.3.4.1.2.2")) {
                result.add("CAN_SIGN");
            } else if (item.equals("1.2.398.3.3.4.1.2.3")) {
                result.add("CAN_SIGN_FINANCIAL");
            } else if (item.equals("1.2.398.3.3.4.1.2.4")) {
                result.add("HR");
            } else if (item.equals("1.2.398.3.3.4.1.2.5")) {
                result.add("EMPLOYEE");
            } else if (item.equals("1.2.398.3.3.4.2")) {
                result.add("NCA_PRIVILEGES");
            } else if (item.equals("1.2.398.3.3.4.2.1")) {
                result.add("NCA_ADMIN");
            } else if (item.equals("1.2.398.3.3.4.2.2")) {
                result.add("NCA_MANAGER");
            } else if (item.equals("1.2.398.3.3.4.2.3")) {
                result.add("NCA_OPERATOR");
            } else if (item.equals("1.2.398.3.3.4.3")) {
                result.add("IDENTIFICATION");
            } else if (item.equals("1.2.398.3.3.4.3.1")) {
                result.add("IDENTIFICATION_CON");
            } else if (item.equals("1.2.398.3.3.4.3.2")) {
                result.add("IDENTIFICATION_REMOTE");
            } else if (item.equals("1.2.398.3.3.4.3.2.1")) {
                result.add("IDENTIFICATION_REMOTE_DIGITAL_ID");
            }
        }

        return result;
    }


    // private
    private OCSPReq buildOcspRequest(BigInteger serialNumber, X509Certificate issuerCert, String hashAlg, byte[] nonce) throws OCSPException {
        OCSPReqGenerator ocspReqGenerator = new OCSPReqGenerator();

        CertificateID certId = new CertificateID(hashAlg, issuerCert, serialNumber, kalkan.get().getName());

        ocspReqGenerator.addRequest(certId);

        Hashtable x509Extensions = new Hashtable();

        // добавляем nonce
        x509Extensions.put(OCSPObjectIdentifiers.id_pkix_ocsp_nonce, new X509Extension(false, new DEROctetString(new DEROctetString(nonce))) {
        });
        ocspReqGenerator.setRequestExtensions(new X509Extensions(x509Extensions));

        return ocspReqGenerator.generate();
    }

    private static void parseRdn(Rdn rdn, JSONObject subject) {
        if (rdn.getType().equalsIgnoreCase("CN")) {
            subject.put("commonName", rdn.getValue());
        } else if (rdn.getType().equalsIgnoreCase("SURNAME")) {
            subject.put("surname", rdn.getValue());
        } else if (rdn.getType().equalsIgnoreCase("SERIALNUMBER")) {

            String sn = ((String) rdn.getValue());

            if (sn.startsWith("BIN")) {
                subject.put("bin", ((String) rdn.getValue()).replaceAll("^BIN", ""));
            } else {
                subject.put("iin", ((String) rdn.getValue()).replaceAll("^IIN", ""));
            }
        } else if (rdn.getType().equalsIgnoreCase("C")) {
            subject.put("country", rdn.getValue());
        } else if (rdn.getType().equalsIgnoreCase("L")) {
            subject.put("locality", rdn.getValue());
        } else if (rdn.getType().equalsIgnoreCase("S")) {
            subject.put("state", rdn.getValue());
        } else if (rdn.getType().equalsIgnoreCase("E")) {
            subject.put("email", rdn.getValue());
        } else if (rdn.getType().equalsIgnoreCase("O")) {
            subject.put("organization", rdn.getValue());
        } else if (rdn.getType().equalsIgnoreCase("OU")) {
            subject.put("bin", ((String) rdn.getValue()).replaceAll("^BIN", ""));
        } else if (rdn.getType().equalsIgnoreCase("G")) {
            subject.put("lastName", rdn.getValue());
        }
    }

    private byte[] generateOcspNonce() {
        byte[] nonce = new byte[8];
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(nonce);

        return nonce;
    }

    private OCSPStatus processOcspResponse(InputStream response, byte[] nonce) throws IOException, OCSPException {
        OCSPResp resp = new OCSPResp(response);

        if (resp.getStatus() != 0) {
            return new OCSPStatus(OCSPStatus.OCSPResult.UNKNOWN, null, 0);
        }

        BasicOCSPResp brep = (BasicOCSPResp) resp.getResponseObject();
        byte[] respNonceExt = brep.getExtensionValue(OCSPObjectIdentifiers.id_pkix_ocsp_nonce.getId());

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
        } else if (status instanceof RevokedStatus) {
            RevokedStatus rev = (RevokedStatus) status;

            int reason = 0;

            try {
                reason = rev.getRevocationReason();
            } catch (IllegalStateException e) {
                reason = 0;
            }

            return new OCSPStatus(OCSPStatus.OCSPResult.REVOKED, rev.getRevocationTime(), reason);
        }

        return new OCSPStatus(OCSPStatus.OCSPResult.UNKNOWN, null, 0);
    }

}
