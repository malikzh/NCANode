package kz.ncanode.api.version.v20.controllers;

import kz.gov.pki.kalkan.asn1.cms.Attribute;
import kz.gov.pki.kalkan.asn1.cms.AttributeTable;
import kz.gov.pki.kalkan.asn1.knca.KNCAObjectIdentifiers;
import kz.gov.pki.kalkan.asn1.pkcs.PKCSObjectIdentifiers;
import kz.gov.pki.kalkan.jce.provider.cms.*;
import kz.gov.pki.kalkan.tsp.TSPException;
import kz.gov.pki.kalkan.tsp.TimeStampTokenInfo;
import kz.gov.pki.kalkan.util.encoders.Hex;
import kz.ncanode.Helper;
import kz.ncanode.api.core.ApiStatus;
import kz.ncanode.api.core.annotations.ApiController;
import kz.ncanode.api.core.annotations.ApiMethod;
import kz.ncanode.api.exceptions.ApiErrorException;
import kz.ncanode.api.version.v20.models.CmsExtractModel;
import kz.ncanode.api.version.v20.models.CmsSignModel;
import kz.ncanode.api.version.v20.models.CmsVerifyModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.*;
import java.util.*;

@ApiController("cms")
public class CmsController extends kz.ncanode.api.core.ApiController {
    @ApiMethod(url = "sign")
    public void sign(CmsSignModel model, JSONObject response) throws KeyStoreException, ApiErrorException, UnrecoverableKeyException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException, InvalidAlgorithmParameterException, CertStoreException, CMSException, IOException, TSPException {
        CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
        CMSProcessableByteArray cmsData = new CMSProcessableByteArray(model.data.get());

        List<X509Certificate> certs = new ArrayList<>();

        for (int i=0; i<model.p12array.size(); ++i) {
            KeyStore p12      = model.p12array.getKey(i);
            String password   = model.p12array.getPassword(i);
            String alias      = model.p12array.getAlias(i);

            if (alias == null || alias.isEmpty()) {
                Enumeration<String> als = p12.aliases();

                while (als.hasMoreElements()) {
                    alias = als.nextElement();
                }
            }

            if (!p12.containsAlias(alias)) {
                throw new ApiErrorException("Certificate with specified alias not found");
            }

            // Получаем закрытый ключ
            PrivateKey privateKey = (PrivateKey) p12.getKey(alias, password.toCharArray());

            // Получаем сертификат
            X509Certificate cert = (X509Certificate) p12.getCertificate(alias);
            certs.add(cert);

            Signature sig = null;
            sig = Signature.getInstance(cert.getSigAlgName(), getApiServiceProvider().kalkan.get());
            sig.initSign(privateKey);
            sig.update(model.data.get());

            CertStore chainStore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(Arrays.asList(cert)), getApiServiceProvider().kalkan.get().getName());
            gen.addSigner(privateKey, cert, Helper.getDigestAlgorithmOidBYSignAlgorithmOid(cert.getSigAlgOID()));
            gen.addCertificatesAndCRLs(chainStore);
        }

        CMSSignedData signed = gen.generate(cmsData, true, getApiServiceProvider().kalkan.get().getName());


        if (model.withTsp.get()) {
            String useTsaPolicy = model.useTsaPolicy.get().equals("TSA_GOSTGT_POLICY") ?
                    KNCAObjectIdentifiers.tsa_gostgt_policy.getId() :
                    KNCAObjectIdentifiers.tsa_gost_policy.getId();

            SignerInformationStore signerStore = signed.getSignerInfos();

            List<SignerInformation> newSigners = new ArrayList<SignerInformation>();

            int i = 0;

            for (SignerInformation signer : (Collection<SignerInformation>) signerStore.getSigners()) {
                X509Certificate cert = certs.get(i++);
                newSigners.add(getApiServiceProvider().tsp.addTspToSigner(signer, cert, useTsaPolicy));
            }

            signed = CMSSignedData.replaceSigners(signed, new SignerInformationStore(newSigners));
        }


        response.put("cms", new String(Base64.getEncoder().encode(signed.getEncoded())));
        response.put("status", ApiStatus.STATUS_OK);
        response.put("message", "");
    }

    @ApiMethod(url = "verify")
    public void verify(CmsVerifyModel model, JSONObject response) throws Exception {
        CMSSignedData cms = model.cms.get();

        SignerInformationStore signers = cms.getSignerInfos();
        String providerName = getApiServiceProvider().kalkan.get().getName();
        CertStore clientCerts = cms.getCertificatesAndCRLs("Collection", providerName);

        JSONObject resp = new JSONObject();

        Iterator sit = signers.getSigners().iterator();

        boolean signInfo = false;

        JSONArray tspinf = new JSONArray();

        List<X509Certificate> certs = new ArrayList<>();
        List<Boolean> certsSignValid = new ArrayList<>();

        while (sit.hasNext()) {
            signInfo = true;

            SignerInformation signer = (SignerInformation) sit.next();
            X509CertSelector signerConstraints = signer.getSID();
            Collection certCollection = clientCerts.getCertificates(signerConstraints);
            Iterator certIt = certCollection.iterator();

            boolean certCheck = false;

            while (certIt.hasNext()) {
                certCheck = true;
                X509Certificate cert = (X509Certificate) certIt.next();
                cert.checkValidity();
                certs.add(cert);
                certsSignValid.add(signer.verify(cert.getPublicKey(), providerName));
            }

            if (!certCheck) {
                throw new Exception("Certificate not found");
            }

            // Tsp verification
            if (signer.getUnsignedAttributes() != null) {
                Hashtable attrs = signer.getUnsignedAttributes().toHashtable();

                if (attrs.containsKey(PKCSObjectIdentifiers.id_aa_signatureTimeStampToken)) {
                    Attribute attr = (Attribute) attrs.get(PKCSObjectIdentifiers.id_aa_signatureTimeStampToken);


                    if (attr.getAttrValues().size() != 1) {
                        throw new Exception("Too many TSP tokens");
                    }

                    CMSSignedData tspCms = new CMSSignedData(attr.getAttrValues().getObjectAt(0).getDERObject().getEncoded());
                    TimeStampTokenInfo tspi = getApiServiceProvider().tsp.verifyTSP(tspCms);

                    JSONObject tspout = new JSONObject();

                    tspout.put("serialNumber", new String(Hex.encode(tspi.getSerialNumber().toByteArray())));
                    tspout.put("genTime", Helper.dateTime(tspi.getGenTime()));
                    tspout.put("policy", tspi.getPolicy());
                    tspout.put("tsa", tspi.getTsa());
                    tspout.put("tspHashAlgorithm", Helper.getHashingAlgorithmByOID(tspi.getMessageImprintAlgOID()));
                    tspout.put("hash", new String(Hex.encode(tspi.getMessageImprintDigest())));

                    tspinf.add(tspout);
                }
            }
        }

        JSONArray certsList = new JSONArray();

        for (int i = 0; i < certs.size(); ++i) {
            X509Certificate cert = certs.get(i);

            // Chain information
            ArrayList<java.security.cert.X509Certificate> chain = null;
            ArrayList<JSONObject> chainInf = null;
            chain = getApiServiceProvider().ca.chain(cert);

            chainInf = new ArrayList<>();

            if (chain != null) {
                for (java.security.cert.X509Certificate chainCert : chain) {
                    JSONObject chi = getApiServiceProvider().pki.certInfo(chainCert, false, false, null);
                    chainInf.add(chi);
                }
            }

            java.security.cert.X509Certificate issuerCert = null;

            if (chain != null && chain.size() > 1) {
                issuerCert = chain.get(1);
            }

            if (issuerCert == null) {
                throw new ApiErrorException("Cannot find certificate issuer");
            }

            try {
                JSONObject certInf2 = new JSONObject();
                JSONObject certInf = getApiServiceProvider().pki.certInfo(cert, model.checkOcsp.get(), model.checkCrl.get(), issuerCert);
                certInf2.put("chain", chainInf);
                certInf2.put("cert", certInf);
                certsList.add(certInf2);
            } catch (Exception e) {
                throw new ApiErrorException(e.getMessage());
            }
        }

        resp.put("signers", certsList);
        resp.put("tsp", tspinf);

        if (!signInfo) {
            throw new Exception("SignerInformation not found");
        }

        response.put("result", resp);
        response.put("status", ApiStatus.STATUS_OK);
        response.put("message", "");
    }

    @ApiMethod(url = "extract")
    public void extract(CmsExtractModel model, JSONObject response) throws ApiErrorException, IOException, CMSException {
        CMSSignedData cms = model.cms.get();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        cms.getSignedContent().write(out);
        byte[] original = out.toByteArray();
        response.put("originalData", new String(Base64.getEncoder().encode(original)));
        response.put("status", ApiStatus.STATUS_OK);
        response.put("message", "");
    }
}
