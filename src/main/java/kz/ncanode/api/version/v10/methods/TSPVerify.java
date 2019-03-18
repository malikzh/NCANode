package kz.ncanode.api.version.v10.methods;

import kz.gov.pki.kalkan.jce.provider.cms.CMSSignedData;
import kz.gov.pki.kalkan.tsp.TimeStampTokenInfo;
import kz.gov.pki.kalkan.util.encoders.Hex;
import kz.ncanode.Helper;
import kz.ncanode.api.ApiServiceProvider;
import kz.ncanode.api.core.ApiArgument;
import kz.ncanode.api.core.ApiMethod;
import kz.ncanode.api.core.ApiVersion;
import kz.ncanode.api.exceptions.ApiErrorException;
import kz.ncanode.api.version.v10.arguments.CmsArgument;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class TSPVerify extends ApiMethod {
    public TSPVerify(ApiVersion ver, ApiServiceProvider man) {
        super(ver, man);
    }

    @Override
    public JSONObject handle() throws ApiErrorException {
        CMSSignedData cms = (CMSSignedData) args.get(0).get();

        JSONObject resp = new JSONObject();

        try {
            TimeStampTokenInfo tspInfo = man.tsp.verifyTSP(cms);

            resp.put("serialNumber", new String(Hex.encode(tspInfo.getSerialNumber().toByteArray())));
            resp.put("genTime", Helper.dateTime(tspInfo.getGenTime()));
            resp.put("policy", tspInfo.getPolicy());
            resp.put("tsa", tspInfo.getTsa());
            resp.put("tspHashAlgorithm", Helper.getHashingAlgorithmByOID(tspInfo.getMessageImprintAlgOID()));
            resp.put("hash", new String(Hex.encode(tspInfo.getMessageImprintDigest())));
        } catch (Exception e) {
            throw new ApiErrorException(e.getMessage());
        }

        return resp;
    }

    @Override
    public ArrayList<ApiArgument> arguments() {
        ArrayList<ApiArgument> args = new ArrayList<>();
        args.add(new CmsArgument(true, ver, man));
        return args;
    }
}
