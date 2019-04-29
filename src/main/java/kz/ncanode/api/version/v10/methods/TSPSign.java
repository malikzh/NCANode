package kz.ncanode.api.version.v10.methods;

import kz.gov.pki.kalkan.jce.provider.cms.CMSSignedData;
import kz.ncanode.api.ApiServiceProvider;
import kz.ncanode.api.core.ApiArgument;
import kz.ncanode.api.core.ApiMethod;
import kz.ncanode.api.core.ApiVersion;
import kz.ncanode.api.exceptions.ApiErrorException;
import kz.ncanode.api.version.v10.arguments.RawArgument;
import kz.ncanode.api.version.v10.arguments.TspHashAlgorithmArgument;
import kz.ncanode.api.version.v10.arguments.UseTsaPolicyArgument;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Base64;

public class TSPSign extends ApiMethod {
    public TSPSign(ApiVersion ver, ApiServiceProvider man) {
        super(ver, man);
    }

    @Override
    public JSONObject handle() throws ApiErrorException {

        byte[] raw = ((RawArgument)args.get(0)).getBytes();
        String useTsaPolicy     = (String)args.get(1).get();
        String tspHashAlgorithm = (String)args.get(2).get();

        JSONObject resp = new JSONObject();

        try {
            CMSSignedData tsp = man.tsp.createTSP(raw, tspHashAlgorithm, useTsaPolicy).toCMSSignedData();

            // encode tsp to Base64
            String tspBase64 = new String(Base64.getEncoder().encode(tsp.getEncoded()));
            resp.put("tsp", tspBase64);

        } catch (Exception e) {
            throw new ApiErrorException(e.getMessage());
        }

        return resp;
    }

    @Override
    public ArrayList<ApiArgument> arguments() {
        ArrayList<ApiArgument> args = new ArrayList<>();
        args.add(new RawArgument(true, ver, man));
        args.add(new UseTsaPolicyArgument(false, ver, man));
        args.add(new TspHashAlgorithmArgument(false, ver, man));
        return args;
    }
}
