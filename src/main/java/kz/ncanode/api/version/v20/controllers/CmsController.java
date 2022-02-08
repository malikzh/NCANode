package kz.ncanode.api.version.v20.controllers;

import kz.gov.pki.kalkan.asn1.cms.Attribute;
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
import kz.ncanode.kalkan.KalkanServiceProvider;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.*;
import java.security.cert.*;
import java.util.*;
import java.util.stream.Collectors;

@ApiController("cms")
public class CmsController extends kz.ncanode.api.core.ApiController {
    @ApiMethod(url = "sign")
    public void sign(CmsSignModel model, JSONObject response) throws KeyStoreException, ApiErrorException, UnrecoverableKeyException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException, InvalidAlgorithmParameterException, CertStoreException, CMSException, IOException, TSPException {
        KalkanServiceProvider kalkan = getApiServiceProvider().kalkan;
        CMSSignedDataGenerator signedDataGenerator = new CMSSignedDataGenerator();
        CMSProcessable cmsData = model.getDataToEncode();
        List<X509Certificate> certificates = new ArrayList<>();
        SignerInformationStore existingSigners = null;
        int signerCountBefore = 0;
        int documentSizeBefore = model.data.get().length;

        if (model.isAlreadySigned()) {
            certificates = kalkan.getCertificatesFromCmsSignedData(model.getSignedData());
            existingSigners = model.getSignedData().getSignerInfos();
            signedDataGenerator.addSigners(existingSigners);
            signerCountBefore = existingSigners.size();
        }

        for (int i = 0; i < model.p12array.size(); ++i) {
            KeyStore p12 = model.p12array.getKey(i);
            String password = model.p12array.getPassword(i);
            String alias = model.p12array.getAlias(i);

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

            try {
                cert.checkValidity();
            } catch (CertificateExpiredException|CertificateNotYetValidException e) {
                throw new ApiErrorException(e.getMessage(), HttpURLConnection.HTTP_BAD_REQUEST, ApiStatus.STATUS_CERTIFICATE_INVALID);
            }

            certificates.add(cert);

            Signature sig = Signature.getInstance(cert.getSigAlgName(), kalkan.get());
            sig.initSign(privateKey);
            sig.update(model.data.get());

            signedDataGenerator.addSigner(privateKey, cert, Helper.getDigestAlgorithmOidBYSignAlgorithmOid(cert.getSigAlgOID()));
        }

        CertStore chainStore = CertStore.getInstance(
                "Collection",
                new CollectionCertStoreParameters(
                        // если происходит повторная подпись, сертификаты могут дублироваться.
                        // добавим в chainStore только уникальные сертификаты.
                        certificates.stream().distinct().collect(Collectors.toList())
                ),
                kalkan.get().getName()
        );
        signedDataGenerator.addCertificatesAndCRLs(chainStore);
        CMSSignedData signed = signedDataGenerator.generate(cmsData, true, kalkan.get().getName());

        // добавляем метку tsp к сформированным подписям
        if (model.withTsp.get()) {
            String useTsaPolicy = model.useTsaPolicy.get().equals("TSA_GOSTGT_POLICY") ?
                    KNCAObjectIdentifiers.tsa_gostgt_policy.getId() :
                    KNCAObjectIdentifiers.tsa_gost_policy.getId();
            SignerInformationStore signerStore = signed.getSignerInfos();
            List<SignerInformation> signers = new ArrayList<SignerInformation>();

            int i = 0;

            for (SignerInformation signer : (Collection<SignerInformation>) signerStore.getSigners()) {
                X509Certificate cert = certificates.get(i++);

                try {
                    signers.add(getApiServiceProvider().tsp.addTspToSigner(signer, cert, useTsaPolicy));
                } catch (IOException e) {
                    throw new ApiErrorException("Не удалось добавить метку TSP: " + e.getMessage());
                }
            }

            signed = CMSSignedData.replaceSigners(signed, new SignerInformationStore(signers));
        }

        byte[] encodedData = signed.getEncoded();
        response.put("cms", new String(Base64.getEncoder().encode(encodedData)));
        response.put("status", ApiStatus.STATUS_OK);
        response.put("message", "");
        // дополнительная информация для отладки
        response.put("documentSizeBefore", documentSizeBefore);
        response.put("documentSizeAfter", encodedData.length);
        response.put("signerCountBefore", signerCountBefore);
        response.put("signerCountAfter", signed.getSignerInfos().size());
    }

    @ApiMethod(url = "verify")
    public void verify(CmsVerifyModel model, JSONObject response) throws Exception {
        if (model.checkCrl.get() && !getApiServiceProvider().crl.isEnabled()) {
            response.put("status", ApiStatus.STATUS_INVALID_PARAMETER);
            response.put("httpCode", HttpURLConnection.HTTP_BAD_REQUEST);
            response.put(
                    "message",
                    "CRL verification is disabled. Turn it on in service configuration with 'crl_enabled=true'."
            );

            return;
        }

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
        HashMap<String, ArrayList<JSONObject>> certSerialNumbersToTsps = new HashMap<>();

        while (sit.hasNext()) {
            signInfo = true;

            SignerInformation signer = (SignerInformation) sit.next();
            X509CertSelector signerConstraints = signer.getSID();
            Collection certCollection = clientCerts.getCertificates(signerConstraints);
            Iterator certIt = certCollection.iterator();

            boolean certCheck = false;
            List<String> certSerialNumbers = new ArrayList<>();

            while (certIt.hasNext()) {
                certCheck = true;
                X509Certificate cert = (X509Certificate) certIt.next();

                try {
                    cert.checkValidity();
                } catch (CertificateExpiredException|CertificateNotYetValidException e) {
                    throw new ApiErrorException(e.getMessage(), HttpURLConnection.HTTP_BAD_REQUEST, ApiStatus.STATUS_CERTIFICATE_INVALID);
                }

                certs.add(cert);
                certsSignValid.add(signer.verify(cert.getPublicKey(), providerName));
                certSerialNumbers.add(String.valueOf(cert.getSerialNumber()));
            }

            if (!certCheck) {
                throw new ApiErrorException(
                        "Certificate not found. The document signature is probably invalid.",
                        HttpURLConnection.HTTP_BAD_REQUEST,
                        ApiStatus.STATUS_CERTIFICATE_INVALID
                );
            }

            // Tsp verification
            Vector<Attribute> tspAttrs = getApiServiceProvider().tsp.getSignerTspAttributes(signer);

            for (Attribute attr : tspAttrs) {
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

                for (String certSerialNumber : certSerialNumbers) {
                    ArrayList<JSONObject> tspsWithCerialNumber = certSerialNumbersToTsps.getOrDefault(certSerialNumber, new ArrayList<>());
                    tspsWithCerialNumber.add(tspout);
                    certSerialNumbersToTsps.put(certSerialNumber, tspsWithCerialNumber);
                }

                tspinf.add(tspout);
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
                throw new ApiErrorException(
                        "Cannot find certificate issuer. The document signature is probably invalid.",
                        HttpURLConnection.HTTP_BAD_REQUEST,
                        ApiStatus.STATUS_CERTIFICATE_INVALID
                );
            }

            try {
                JSONObject certInf2 = new JSONObject();
                JSONObject certInf = getApiServiceProvider().pki.certInfo(cert, model.checkOcsp.get(), model.checkCrl.get(), issuerCert);
                certInf2.put("chain", chainInf);
                certInf2.put("cert", certInf);
                certInf2.put("tsps", certSerialNumbersToTsps.getOrDefault(String.valueOf(cert.getSerialNumber()), new ArrayList<>()));
                certsList.add(certInf2);
            } catch (Exception e) {
                throw new ApiErrorException(e.getMessage());
            }
        }

        if (!signInfo) {
            throw new ApiErrorException(
                    "Signer information not found. The document signature is probably invalid.",
                    HttpURLConnection.HTTP_BAD_REQUEST,
                    ApiStatus.STATUS_CERTIFICATE_INVALID
            );
        }

        resp.put("signers", certsList);
        resp.put("tsp", tspinf);
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
