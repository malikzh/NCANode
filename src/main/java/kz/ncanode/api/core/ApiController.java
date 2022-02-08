package kz.ncanode.api.core;

import kz.ncanode.api.ApiServiceProvider;
import kz.ncanode.api.core.annotations.ApiMethod;
import kz.ncanode.api.exceptions.ApiErrorException;
import kz.ncanode.api.exceptions.InvalidArgumentException;
import org.json.simple.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;

public abstract class ApiController extends ApiDependencies {

    public boolean hasMethod(String method) {
        int idx = method.lastIndexOf(".");
        String methodName = method.substring(idx+1);

        for (Method m : this.getClass().getMethods()) {
            ApiMethod am = m.getAnnotation(ApiMethod.class);

            if (am.url().equals(methodName)) {
                return true;
            }
        }

        return false;
    }

    public void callMethod(String method, JSONObject request, JSONObject response) throws ApiErrorException {
        int idx = method.lastIndexOf(".");
        String methodName = method.substring(idx+1);

        for (Method m : this.getClass().getMethods()) {
            ApiMethod am = m.getAnnotation(ApiMethod.class);

            if (am.url().equals(methodName)) {
                try {
                    invokeMethod(m, request, response);
                } catch (InvalidArgumentException e) {
                    throw new ApiErrorException(e.getMessage(), HttpURLConnection.HTTP_BAD_REQUEST, ApiStatus.STATUS_INVALID_PARAMETER);
                } catch (Exception e) {
                    Throwable cause = e.getCause();

                    if (cause instanceof ApiErrorException) {
                        getApiServiceProvider().err.write(String.format(
                                "Error while executing method '%s': %s",
                                methodName,
                                cause.getMessage()
                        ));

                        throw ((ApiErrorException) cause);
                    } else {
                        e.printStackTrace();

                        throw new ApiErrorException(e.getMessage());
                    }
                }

                break;
            }
        }
    }

    private void invokeMethod(Method method, JSONObject request, JSONObject response)
            throws ApiErrorException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, InvalidArgumentException {
        Class<?>[] parameterTypes = method.getParameterTypes();

        if (parameterTypes.length == 2) { // Метод получает параметры
            ApiModel model = (ApiModel) parameterTypes[0].getConstructor().newInstance();
            model.setDependencies(getApiVersion(), getApiServiceProvider());
            model.accept(request);
            method.invoke(this, model, response);
        }
        else if (parameterTypes.length == 1) { // Метод не получает параметров
            method.invoke(this, response);
        } else {
            throw new ApiErrorException("Invalid method '" + method.getName() + "' implementation");
        }
    }

}
