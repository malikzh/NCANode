package kz.ncanode.api.version.v10.methods;

import kz.ncanode.api.ApiServiceProvider;
import kz.ncanode.api.core.ApiArgument;
import kz.ncanode.api.core.ApiMethod;
import kz.ncanode.api.core.ApiVersion;
import kz.ncanode.api.exceptions.ApiErrorException;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class NODEInfo extends ApiMethod {
    public NODEInfo(ApiVersion ver, ApiServiceProvider man) {
        super(ver, man);
    }

    @Override
    public JSONObject handle() throws ApiErrorException {
        JSONObject resp = new JSONObject();

        resp.put("version", man.info.getVersion());
        resp.put("name", man.info.getFullName());

        return resp;
    }

    @Override
    public ArrayList<ApiArgument> arguments() {
        return null;
    }
}
