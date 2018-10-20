package kz.ncanode.api.core;

import kz.ncanode.api.ApiServiceProvider;
import org.json.simple.JSONObject;

public interface ApiVersion {
    void setApiManager(ApiServiceProvider apiManager);
    JSONObject process(JSONObject request);
}
