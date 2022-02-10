package kz.ncanode.api.version.v20;

import kz.ncanode.api.ApiServiceProvider;
import kz.ncanode.api.core.ApiController;
import kz.ncanode.api.core.ApiStatus;
import kz.ncanode.api.core.ApiVersion;
import kz.ncanode.api.exceptions.ApiErrorException;
import kz.ncanode.api.version.v20.controllers.CmsController;
import kz.ncanode.api.version.v20.controllers.InfoController;
import kz.ncanode.api.version.v20.controllers.NodeController;
import org.json.simple.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Версия API 2.0
 */
public class ApiVersion20 implements ApiVersion {
    private ApiServiceProvider man;
    private List<ApiController> controllers;

    public ApiVersion20(ApiServiceProvider man) {
        controllers = new ArrayList<>();
        this.man = man;
        registerControllers();

        for (ApiController controller : controllers) {
            controller.setDependencies(this, man);
        }
    }

    // Регистрация контроллёров
    private void registerControllers() {
        controllers.add(new NodeController());
        controllers.add(new InfoController());
        controllers.add(new CmsController());
    }

    @Override
    public void setApiManager(ApiServiceProvider apiManager) {
        man = apiManager;
    }

    @Override
    public JSONObject process(JSONObject request) {
        JSONObject response = new JSONObject();

        String method = "";

        try {
            method = (String) request.get("method");
        } catch (ClassCastException e) {
            JSONObject resp = new JSONObject();
            resp.put("status", ApiStatus.STATUS_INVALID_PARAMETER);
            resp.put("httpCode", HttpURLConnection.HTTP_BAD_REQUEST);
            resp.put("message", "Invalid parameter \"method\"");
            return resp;
        }

        // Если метод пустой
        if (method == null || method.isEmpty()) {
            JSONObject resp = new JSONObject();
            resp.put("status", ApiStatus.STATUS_METHOD_NOT_SPECIFIED);
            resp.put("httpCode", HttpURLConnection.HTTP_BAD_REQUEST);
            resp.put("message", "\"method\" not specified");
            return resp;
        }

        ApiController controller = findController(method);

        if (controller == null || !controller.hasMethod(method)) {
            JSONObject resp = new JSONObject();
            resp.put("status", ApiStatus.STATUS_METHOD_NOT_FOUND);
            resp.put("httpCode", HttpURLConnection.HTTP_BAD_REQUEST);
            resp.put("message", "Method not found");
            return resp;
        }

        // Получаем параметры
        JSONObject params;

        try {
            params = (JSONObject) request.get("params");
        } catch (ClassCastException e) {
            JSONObject resp = new JSONObject();
            resp.put("status", ApiStatus.STATUS_INVALID_PARAMETER);
            resp.put("httpCode", HttpURLConnection.HTTP_BAD_REQUEST);
            resp.put("message", "Invalid parameter \"params\"");
            return resp;
        }

        if (params == null) {
            params = new JSONObject();
        }

        try {
            controller.callMethod(method, params, response);
        } catch (ApiErrorException e) {
            JSONObject resp = new JSONObject();
            resp.put("httpCode", e.getHttpCode());
            resp.put("status", e.getStatus());
            resp.put("message", "Api error: " + e.getMessage());

            return resp;
        }

        return response;
    }

    private String getControllerName(String method) {
        int idx = method.lastIndexOf(".");
        return method.substring(0, idx);
    }

    private ApiController findController(String method) {
        String controllerName = getControllerName(method);

        for (ApiController controller : controllers) {
            kz.ncanode.api.core.annotations.ApiController apiControllerAnnotation =
                    controller.getClass().getAnnotation(kz.ncanode.api.core.annotations.ApiController.class);

            if (controllerName.equals(apiControllerAnnotation.value())) {
                return controller;
            }
        }

        return null;
    }
}
