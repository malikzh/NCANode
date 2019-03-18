package kz.ncanode.api.version.v10.methods;

import kz.gov.pki.kalkan.jce.provider.cms.CMSSignedData;
import kz.gov.pki.kalkan.jce.provider.cms.SignerInformation;
import kz.gov.pki.kalkan.jce.provider.cms.SignerInformationStore;
import kz.ncanode.api.ApiServiceProvider;
import kz.ncanode.api.core.ApiArgument;
import kz.ncanode.api.core.ApiMethod;
import kz.ncanode.api.core.ApiVersion;
import kz.ncanode.api.exceptions.ApiErrorException;
import kz.ncanode.api.version.v10.arguments.CmsArgument;
import kz.ncanode.api.version.v10.arguments.VerifyCrlArgument;
import kz.ncanode.api.version.v10.arguments.VerifyOcspArgument;
import org.json.simple.JSONObject;

import java.security.cert.CertStore;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
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
        CertStore clientCerts = cms.getCertificatesAndCRLs("Collection", man.kalkan.get().getName());

        JSONObject resp = new JSONObject();

        Iterator sit = signers.getSigners().iterator();

        X509Certificate cert = null;

        if (sit.hasNext()) {
            SignerInformation signer = (SignerInformation) sit.next();
            X509CertSelector signerConstraints = signer.getSID();
            Collection certCollection = clientCerts.getCertificates(signerConstraints);
            Iterator certIt = certCollection.iterator();

            if (certIt.hasNext()) {
                cert = (X509Certificate) certIt.next();
                cert.checkValidity();
            } else {
                throw new Exception("Certificate not found");
            }
        } else {
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

        if (chain != null && chain.size() >= 1) {
            issuerCert = chain.get(0);
        }

        JSONObject certInf;
        certInf = man.pki.certInfo(cert, ((boolean)args.get(1).get() && issuerCert != null) , ((boolean)args.get(2).get() && issuerCert != null), issuerCert);
        certInf.put("chain", chainInf);
        resp.put("cert", certInf);

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
