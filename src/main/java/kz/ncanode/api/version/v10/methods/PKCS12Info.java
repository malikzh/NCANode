package kz.ncanode.api.version.v10.methods;

import kz.ncanode.api.ApiServiceProvider;
import kz.ncanode.api.core.ApiArgument;
import kz.ncanode.api.core.ApiMethod;
import kz.ncanode.api.core.ApiVersion;
import kz.ncanode.api.exceptions.ApiErrorException;
import kz.ncanode.api.version.v10.arguments.P12ApiArgument;
import kz.ncanode.api.version.v10.arguments.VerifyCrlArgument;
import kz.ncanode.api.version.v10.arguments.VerifyOcspArgument;
import org.json.simple.JSONObject;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;

public class PKCS12Info extends ApiMethod {
    public PKCS12Info(ApiVersion ver, ApiServiceProvider man) {
        super(ver, man);
    }

    @Override
    public JSONObject handle() throws ApiErrorException {
        JSONObject resp = new JSONObject();

        KeyStore p12 = (KeyStore)args.get(0).get();

        Enumeration<String> als = null;
        try {
            als = p12.aliases();
        } catch (KeyStoreException e) {
            throw new ApiErrorException(e.getMessage());
        }
        String alias = null;
        while (als.hasMoreElements()) {
            alias = als.nextElement();
        }




        X509Certificate cert;

        try {
            cert = (X509Certificate)p12.getCertificate(alias);
        } catch (KeyStoreException e) {
            throw new ApiErrorException(e.getMessage());
        }

        // Chain information
        ArrayList<X509Certificate> chain = null;
        ArrayList<JSONObject> chainInf = null;
        try {
            chain = man.ca.chain(cert);

            chainInf = new ArrayList<>();

            if (chain != null) {
                for (X509Certificate chainCert : chain) {
                    JSONObject chi = man.pki.certInfo(chainCert, false, false, null);
                    chainInf.add(chi);
                }
            }
        } catch (Exception e) {
            throw new ApiErrorException(e.getMessage());
        }


        X509Certificate issuerCert = null;

        if (chain != null && chain.size() > 1) {
            issuerCert = chain.get(1);
        }


        try {
            resp = man.pki.certInfo(cert, ((boolean)args.get(1).get() && issuerCert != null) , ((boolean)args.get(2).get() && issuerCert != null), issuerCert);
            resp.put("chain", chainInf);
        } catch (Exception e) {
            throw new ApiErrorException(e.getMessage());
        }


        return resp;
    }

    @Override
    public ArrayList<ApiArgument> arguments() {
        ArrayList<ApiArgument> args = new ArrayList<>();
        args.add(new P12ApiArgument(true, ver, man));
        args.add(new VerifyOcspArgument(false, ver, man));
        args.add(new VerifyCrlArgument(false, ver, man));

        return args;
    }
}
