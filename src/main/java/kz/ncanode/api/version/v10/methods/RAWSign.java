package kz.ncanode.api.version.v10.methods;

import kz.gov.pki.kalkan.jce.provider.cms.CMSException;
import kz.gov.pki.kalkan.jce.provider.cms.CMSProcessableByteArray;
import kz.gov.pki.kalkan.jce.provider.cms.CMSSignedData;
import kz.gov.pki.kalkan.jce.provider.cms.CMSSignedDataGenerator;
import kz.gov.pki.kalkan.tsp.TSPException;
import kz.ncanode.api.ApiServiceProvider;
import kz.ncanode.api.core.ApiArgument;
import kz.ncanode.api.core.ApiMethod;
import kz.ncanode.api.core.ApiVersion;
import kz.ncanode.api.exceptions.ApiErrorException;
import kz.ncanode.api.version.v10.arguments.*;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Enumeration;

public class RAWSign extends ApiMethod {
    public RAWSign(ApiVersion ver, ApiServiceProvider man) {
        super(ver, man);
    }

    @Override
    public JSONObject handle() throws ApiErrorException {
        try {
            return signData();
        } catch (Exception e) {
            throw new ApiErrorException(e.getMessage());
        }
    }

    private JSONObject signData() throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException, InvalidAlgorithmParameterException, CertStoreException, CMSException, IOException, TSPException {
        KeyStore p12 = (KeyStore)args.get(0).get();
        byte[]   raw = ((RawArgument)args.get(1)).getBytes();

        // todo Добавить возможность выбора алиаса
        Enumeration<String> als = p12.aliases();
        String alias = null;
        while (als.hasMoreElements()) {
            alias = als.nextElement();
        }

        // get private key
        String password = ((P12ApiArgument)args.get(0)).getPassword();
        PrivateKey privateKey = (PrivateKey) p12.getKey(alias, password.toCharArray());

        // get certificate
        X509Certificate cert = null;
        cert = (X509Certificate) p12.getCertificate(alias);
        String sigAlgOid = cert.getSigAlgOID();

        CMSSignedDataGenerator gen = new CMSSignedDataGenerator();

        Signature sig = null;
        sig = Signature.getInstance(cert.getSigAlgName(), man.kalkan.get());
        sig.initSign(privateKey);
        sig.update(raw);

        CMSProcessableByteArray cmsData = new CMSProcessableByteArray(raw);
        gen.addSigner(privateKey, cert, CMSSignedDataGenerator.DIGEST_SHA256);
        CertStore chainStore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(Arrays.asList(cert)),man.kalkan.get().getName());
        gen.addCertificatesAndCRLs(chainStore);
        CMSSignedData signed = gen.generate(cmsData, true, man.kalkan.get().getName());

        JSONObject resp = new JSONObject();
        resp.put("cms", new String(Base64.getEncoder().encode(signed.getEncoded())));

        // Create TSP Sign
        if ((Boolean)args.get(2).get()) {
            String useTsaPolicy     = (String)args.get(3).get();
            String tspHashAlgorithm = (String)args.get(4).get();

            CMSSignedData tsp = man.tsp.createTSP(raw, tspHashAlgorithm, useTsaPolicy);

            // encode tsp to Base64
            String tspBase64 = new String(Base64.getEncoder().encode(tsp.getEncoded()));
            resp.put("tsp", tspBase64);
        }

        return resp;
    }

    @Override
    public ArrayList<ApiArgument> arguments() {
        ArrayList<ApiArgument> args = new ArrayList<>();
        args.add(new P12ApiArgument(true, ver, man));
        args.add(new RawArgument(true, ver, man));

        // TSP arguments
        args.add(new CreateTspArgument(false, ver, man));
        args.add(new UseTsaPolicyArgument(false, ver, man));
        args.add(new TspHashAlgorithmArgument(false, ver, man));
        return args;
    }
}
