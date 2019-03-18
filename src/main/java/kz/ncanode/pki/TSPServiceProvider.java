package kz.ncanode.pki;

import kz.gov.pki.kalkan.jce.provider.KalkanProvider;
import kz.gov.pki.kalkan.jce.provider.cms.CMSException;
import kz.gov.pki.kalkan.jce.provider.cms.CMSSignedData;
import kz.gov.pki.kalkan.tsp.*;
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
import java.util.Iterator;

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

    public CMSSignedData createTSP(byte[] data, String hashAlg, String reqPolicy) throws NoSuchProviderException, NoSuchAlgorithmException, IOException, TSPException {

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

        // Create url
        String configTspUrl = config.get("pki", "tsp_url");
        URL tspUrl = new URL(configTspUrl);

        // Make request
        HttpURLConnection con = (HttpURLConnection) tspUrl.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/timestamp-query");
        OutputStream reqStream = con.getOutputStream();
        reqStream.write(reqData);
        reqStream.close();

        // Get response
        InputStream respStream = con.getInputStream();
        TimeStampResponse response = new TimeStampResponse(respStream);
        response.validate(request);

        return response.getTimeStampToken().toCMSSignedData();
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
}
