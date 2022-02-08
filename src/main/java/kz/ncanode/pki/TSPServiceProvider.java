package kz.ncanode.pki;

import kz.gov.pki.kalkan.asn1.ASN1Encodable;
import kz.gov.pki.kalkan.asn1.ASN1EncodableVector;
import kz.gov.pki.kalkan.asn1.DERSet;
import kz.gov.pki.kalkan.asn1.cms.Attribute;
import kz.gov.pki.kalkan.asn1.cms.AttributeTable;
import kz.gov.pki.kalkan.asn1.pkcs.PKCSObjectIdentifiers;
import kz.gov.pki.kalkan.jce.provider.KalkanProvider;
import kz.gov.pki.kalkan.jce.provider.cms.CMSException;
import kz.gov.pki.kalkan.jce.provider.cms.CMSSignedData;
import kz.gov.pki.kalkan.jce.provider.cms.SignerInformation;
import kz.gov.pki.kalkan.tsp.*;
import kz.ncanode.Helper;
import kz.ncanode.config.ConfigServiceProvider;
import kz.ncanode.ioc.ServiceProvider;
import kz.ncanode.kalkan.KalkanServiceProvider;
import kz.ncanode.log.ErrorLogServiceProvider;
import kz.ncanode.log.OutLogServiceProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.*;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

public class TSPServiceProvider implements ServiceProvider {
    private ConfigServiceProvider   config = null;
    private OutLogServiceProvider   out    = null;
    private ErrorLogServiceProvider err    = null;
    private KalkanServiceProvider   kalkan = null;

    public TSPServiceProvider(ConfigServiceProvider config, OutLogServiceProvider out, ErrorLogServiceProvider err, KalkanServiceProvider kalkan, CrlServiceProvider crl) {
        this.config = config;
        this.out    = out;
        this.err    = err;
        this.kalkan = kalkan;
    }

    public TimeStampToken createTSP(byte[] data, String hashAlg, String reqPolicy) throws NoSuchProviderException, NoSuchAlgorithmException, IOException, TSPException {

        // Generate hash
        MessageDigest md = MessageDigest.getInstance(hashAlg, KalkanProvider.PROVIDER_NAME);
        md.update(data);
        byte[] hash = md.digest();

        // Creating TSP request
        TimeStampRequestGenerator reqGen = new TimeStampRequestGenerator();
        reqGen.setCertReq(true);
        reqGen.setReqPolicy(reqPolicy);
        BigInteger nonce = BigInteger.valueOf(System.currentTimeMillis());
        TimeStampRequest request = reqGen.generate(hashAlg, hash, nonce);
        byte[] reqData = request.getEncoded();
        String configTspUrl = config.get("pki", "tsp_url");

        // выполняем запрос к http://tsp.pki.gov.kz с ретраями,
        // потому что иногда он отвечает медленно или с ошибками.
        int retries = 0;
        int maxRetries = 2;
        IOException lastException = null;

        while (retries < maxRetries) {
            try {
                TimeStampResponse response = new TimeStampResponse(requestTsp(configTspUrl, reqData));
                response.validate(request);

                return response.getTimeStampToken();
            } catch (IOException e) {
                lastException = e;
                retries++;
            }
        }

        throw lastException;
    }

    public TimeStampTokenInfo verifyTSP(CMSSignedData data) throws NoSuchAlgorithmException, NoSuchProviderException, CMSException, IOException, TSPException, CertStoreException, CertificateNotYetValidException, CertificateExpiredException {
        TimeStampToken tspt = new TimeStampToken(data);
        X509CertSelector signerConstraints = tspt.getSID();
        CertStore certs = data.getCertificatesAndCRLs("Collection", KalkanProvider.PROVIDER_NAME);
        Collection<?> certCollection = certs.getCertificates(signerConstraints);
        Iterator<?> certIt = certCollection.iterator();
        X509Certificate cert;

        if (!certIt.hasNext()) {
            throw new TSPException("Validating certificate not found");
        }

        cert = (X509Certificate) certIt.next();
        tspt.validate(cert, KalkanProvider.PROVIDER_NAME);

        return tspt.getTimeStampInfo();
    }

    /**
     * Добавляет метку TSP к информации о подписанте.
     */
    public SignerInformation addTspToSigner(SignerInformation signer, X509Certificate cert, String useTsaPolicy) throws NoSuchAlgorithmException, NoSuchProviderException, TSPException, IOException {
        AttributeTable unsignedAttributes = signer.getUnsignedAttributes();
        ASN1EncodableVector vector = new ASN1EncodableVector();

        if (unsignedAttributes != null) {
            vector = unsignedAttributes.toASN1EncodableVector();
        }

        TimeStampToken tsp = createTSP(signer.getSignature(), Helper.getTspHashAlgorithmByOid(cert.getSigAlgOID()), useTsaPolicy);
        byte[] ts = tsp.getEncoded();
        ASN1Encodable signatureTimeStamp = new Attribute(PKCSObjectIdentifiers.id_aa_signatureTimeStampToken, new DERSet(Helper.byteToASN1(ts)));
        vector.add(signatureTimeStamp);
        SignerInformation newSigner = SignerInformation.replaceUnsignedAttributes(signer, new AttributeTable(vector));

        return newSigner;
    }

    /**
     * Возвращает TSP атрибуты подписи
     */
    public Vector<Attribute> getSignerTspAttributes(SignerInformation signer) {
        Vector<Attribute> tspAttrs = new Vector<>();

        if (signer.getUnsignedAttributes() == null) {
            return tspAttrs;
        }

        Hashtable attrs = signer.getUnsignedAttributes().toHashtable();

        if (attrs == null || !attrs.containsKey(PKCSObjectIdentifiers.id_aa_signatureTimeStampToken)) {
            return tspAttrs;
        }

        // в подписи может быть один или несколько tsp атрибутов
        Object attrOrAttrs = attrs.get(PKCSObjectIdentifiers.id_aa_signatureTimeStampToken);

        if (attrOrAttrs instanceof Attribute) {
            tspAttrs.add((Attribute) attrOrAttrs);
        } else {
            tspAttrs = (Vector<Attribute>) attrOrAttrs;
        }

        return tspAttrs;
    }

    /**
     * Есть ли у подписи метка TSP
     */
    public boolean signerHasTsp(SignerInformation signer) {
        return !getSignerTspAttributes(signer).isEmpty();
    }

    /**
     * Выполняет запрос к сервису TSP
     */
    protected InputStream requestTsp(String url, byte[] request) throws IOException {
        URL tspUrl = new URL(url);
        HttpURLConnection con = (HttpURLConnection) tspUrl.openConnection();
        // connection timeout: 1 second
        con.setConnectTimeout(1000);
        // read timeout: 3 seconds
        con.setReadTimeout(3000);
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/timestamp-query");
        OutputStream reqStream = con.getOutputStream();
        reqStream.write(request);
        reqStream.close();

        return con.getInputStream();
    }
}
