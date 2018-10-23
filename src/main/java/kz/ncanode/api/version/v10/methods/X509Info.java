package kz.ncanode.api.version.v10.methods;

import kz.ncanode.api.ApiServiceProvider;
import kz.ncanode.api.core.ApiArgument;
import kz.ncanode.api.core.ApiMethod;
import kz.ncanode.api.core.ApiVersion;
import kz.ncanode.api.exceptions.ApiErrorException;
import kz.ncanode.api.version.v10.arguments.CertArgument;
import kz.ncanode.api.version.v10.arguments.VerifyCrlArgument;
import kz.ncanode.api.version.v10.arguments.VerifyOcspArgument;
import org.json.simple.JSONObject;

import java.security.cert.X509Certificate;
import java.util.ArrayList;

public class X509Info extends ApiMethod {
    public X509Info(ApiVersion ver, ApiServiceProvider man) {
        super(ver, man);
    }

    @Override
    public JSONObject handle() throws ApiErrorException {
        X509Certificate cert = (X509Certificate) args.get(0).get();

        JSONObject resp = new JSONObject();

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
        args.add(new CertArgument(true, ver, man));
        args.add(new VerifyOcspArgument(false, ver, man));
        args.add(new VerifyCrlArgument(false, ver, man));

        return args;
    }
}
