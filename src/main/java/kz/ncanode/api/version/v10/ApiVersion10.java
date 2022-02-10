package kz.ncanode.api.version.v10;

import kz.ncanode.api.ApiServiceProvider;
import kz.ncanode.api.core.ApiArgument;
import kz.ncanode.api.core.ApiMethod;
import kz.ncanode.api.core.ApiStatus;
import kz.ncanode.api.core.ApiVersion;
import kz.ncanode.api.exceptions.ApiErrorException;
import kz.ncanode.api.exceptions.InvalidArgumentException;
import kz.ncanode.api.version.v10.methods.*;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Версия API 1.0
 *
 * Данный класс реализует роутинг методов для данной версии API
 */
public class ApiVersion10 implements ApiVersion {

    private ApiServiceProvider man = null;

    private Hashtable<String, ApiMethod> methods = null;

    public ApiVersion10(ApiServiceProvider man) {
        methods = new Hashtable<>();
        this.man = man;

        // PKCS12
        methods.put("PKCS12.info", new PKCS12Info(this, man));

        // XML
        methods.put("XML.sign", new XMLSign(this, man));
        methods.put("XML.signWithSecurityHeader", new XMLSignWithSecurityHeader(this, man));
        methods.put("XML.verify", new XMLVerify(this, man));
        methods.put("XML.verifyWithSecurityHeader", new XMLVerifyWithSecurityHeader(this, man));

        // TSP
        methods.put("TSP.verify", new TSPVerify(this, man));
        methods.put("TSP.sign", new TSPSign(this, man));

        // RAW
        methods.put("RAW.sign", new RAWSign(this, man));
        methods.put("RAW.verify", new RAWVerify(this, man));

        // X509
        methods.put("X509.info", new X509Info(this, man));

        // NODE
        methods.put("NODE.info", new NODEInfo(this, man));

    }

    public ApiVersion10() {
        methods = new Hashtable<>();
        this.man = man;
    }

    @Override
    public void setApiManager(ApiServiceProvider apiManager) {
        man = apiManager;
    }

    /**
     * Направляет API запрос в нужный метод
     *
     * @param request запрос
     * @return ответ от API
     */
    @Override
    public JSONObject process(JSONObject request) {
        // route method
        // check arguments required
        // check arguments validate
        // run method
        // catch ApiError exception
        // process result

        String method = "";

        try {
            method = (String)request.get("method");
        } catch (ClassCastException e) {
            JSONObject resp = new JSONObject();
            resp.put("status", ApiStatus.STATUS_INVALID_PARAMETER);
            resp.put("message", "Invalid parameter \"method\"");
            return resp;
        }

        if (method == null || method.isEmpty()) {
            JSONObject resp = new JSONObject();
            resp.put("status", ApiStatus.STATUS_METHOD_NOT_SPECIFIED);
            resp.put("message", "\"method\" not specified");
            return resp;
        }

        if (!methods.containsKey(method)) {
            JSONObject resp = new JSONObject();
            resp.put("status", ApiStatus.STATUS_METHOD_NOT_FOUND);
            resp.put("message", "Method not found");
            return resp;
        }

        JSONObject params;

        try {
            params = (JSONObject)request.get("params");
        } catch (ClassCastException e) {
            JSONObject resp = new JSONObject();
            resp.put("status", ApiStatus.STATUS_INVALID_PARAMETER);
            resp.put("message", "Invalid parameter \"params\"");
            return resp;
        }

        ApiMethod m = methods.get(method);


        // строим список аргументов
        ArrayList<ApiArgument> args = m.arguments();

        if (args != null && args.size() > 0) {

            if (params == null) {
                JSONObject resp = new JSONObject();
                resp.put("status", ApiStatus.STATUS_PARAMS_NOT_FOUND);
                resp.put("message", "\"params\" not found in request");
                return resp;
            }

            for (ApiArgument arg : args) {
                try {
                    arg.params = params;
                    arg.validate();
                } catch (InvalidArgumentException e) {
                    JSONObject resp = new JSONObject();
                    resp.put("status", ApiStatus.STATUS_INVALID_PARAMETER);
                    resp.put("message", "Invalid parameter \"" + arg.name() + "\". Error: " + e.getMessage());
                    return resp;
                }
            }
        }

        m.args = args;

        JSONObject response;

        try {
            response = m.handle();
        } catch (ApiErrorException e) {
            JSONObject resp = new JSONObject();
            resp.put("status", e.getStatus());
            resp.put("message", "Api error: " + e.getMessage());
            return resp;
        }

        JSONObject rr = new JSONObject();

        rr.put("status", m.status);
        rr.put("message", m.message);
        rr.put("result", response);

        return rr;
    }
}
