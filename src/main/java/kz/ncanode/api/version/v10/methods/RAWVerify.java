package kz.ncanode.api.version.v10.methods;

import kz.gov.pki.kalkan.asn1.cms.Attribute;
import kz.gov.pki.kalkan.asn1.pkcs.PKCSObjectIdentifiers;
import kz.gov.pki.kalkan.jce.provider.cms.CMSSignedData;
import kz.gov.pki.kalkan.jce.provider.cms.SignerInformation;
import kz.gov.pki.kalkan.jce.provider.cms.SignerInformationStore;
import kz.gov.pki.kalkan.tsp.TimeStampTokenInfo;
import kz.gov.pki.kalkan.util.encoders.Hex;
import kz.ncanode.Helper;
import kz.ncanode.api.ApiServiceProvider;
import kz.ncanode.api.core.ApiArgument;
import kz.ncanode.api.core.ApiMethod;
import kz.ncanode.api.core.ApiVersion;
import kz.ncanode.api.exceptions.ApiErrorException;
import kz.ncanode.api.version.v10.arguments.CmsArgument;
import kz.ncanode.api.version.v10.arguments.VerifyCrlArgument;
import kz.ncanode.api.version.v10.arguments.VerifyOcspArgument;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.security.cert.CertStore;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

public class RAWVerify extends ApiMethod {
    public RAWVerify(ApiVersion ver, ApiServiceProvider man) {
        super(ver, man);
    }

    @Override
    public JSONObject handle() throws ApiErrorException {
        try {
            return verifySign();
        } catch (Exception e) {
            throw new ApiErrorException(e.getMessage());
        }
    }

    private JSONObject verifySign() throws Exception {
        CMSSignedData cms = (CMSSignedData) args.get(0).get();
        SignerInformationStore signers = cms.getSignerInfos();
        boolean isSignatureValid = true;
        String providerName = man.kalkan.get().getName();
        CertStore clientCerts = cms.getCertificatesAndCRLs("Collection", providerName);

        JSONObject resp = new JSONObject();

        Iterator sit = signers.getSigners().iterator();

        X509Certificate cert = null;

        boolean signInfo = false;

        JSONArray tspinf = new JSONArray();

        while (sit.hasNext()) {
            signInfo = true;

            SignerInformation signer = (SignerInformation) sit.next();
            X509CertSelector signerConstraints = signer.getSID();
            Collection certCollection = clientCerts.getCertificates(signerConstraints);
            Iterator certIt = certCollection.iterator();

            boolean certCheck = false;

            while (certIt.hasNext()) {
                certCheck = true;
                cert = (X509Certificate) certIt.next();
                cert.checkValidity();
                if (!signer.verify(cert.getPublicKey(), providerName)) {
                    isSignatureValid = false;
                }
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
                    TimeStampTokenInfo tspi = man.tsp.verifyTSP(tspCms);

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
        resp.put("valid", isSignatureValid);
        resp.put("tsp", tspinf);

        if (!signInfo) {
            throw new Exception("SignerInformation not found");
        }

        // Chain information
        ArrayList<java.security.cert.X509Certificate> chain = null;
        ArrayList<JSONObject> chainInf = null;
        chain = man.ca.chain(cert);

        chainInf = new ArrayList<>();

        if (chain != null) {
            for (java.security.cert.X509Certificate chainCert : chain) {
                JSONObject chi = man.pki.certInfo(chainCert, false, false, null);
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
            JSONObject certInf;
            certInf = man.pki.certInfo(cert,(Boolean) args.get(1).get() , (Boolean) args.get(2).get(), issuerCert);
            certInf.put("chain", chainInf);
            resp.put("cert", certInf);
        } catch (Exception e) {
            throw new ApiErrorException(e.getMessage());
        }

        return resp;
    }

    @Override
    public ArrayList<ApiArgument> arguments() {
        ArrayList<ApiArgument> args = new ArrayList<>();
        args.add(new CmsArgument(true, ver, man));
        args.add(new VerifyOcspArgument(false, ver, man));
        args.add(new VerifyCrlArgument(false, ver, man));
        return args;
    }
}
