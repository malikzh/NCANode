package kz.ncanode.api.version.v20;

import kz.ncanode.api.ApiServiceProvider;
import kz.ncanode.api.core.ApiMethod;
import kz.ncanode.api.core.ApiVersion;
import org.json.simple.JSONObject;

import java.util.Hashtable;

/**
 * Версия API 2.0
 */
public class ApiVersion20 implements ApiVersion {
    private ApiServiceProvider man = null;
    private Hashtable<String, ApiMethod> methods = null;

    public ApiVersion20(ApiServiceProvider man) {
        methods = new Hashtable<>();
        this.man = man;


    }

    @Override
    public void setApiManager(ApiServiceProvider apiManager) {
        man = apiManager;
    }

    @Override
    public JSONObject process(JSONObject request) {
        JSONObject response = new JSONObject();

        response.put("test", "test123");

        return response;
    }
}
