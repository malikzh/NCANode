package kz.ncanode.api.core;

import kz.ncanode.api.ApiServiceProvider;
import kz.ncanode.api.exceptions.ApiErrorException;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public abstract class ApiMethod {

    public ApiVersion ver = null;
    public ApiServiceProvider man = null;

    public ArrayList<ApiArgument> args = null;

    public int status = 0x00;
    public String message = "";

    public ApiMethod(ApiVersion ver, ApiServiceProvider man) {
        this.ver = ver;
        this.man = man;
    }

    public abstract JSONObject handle() throws ApiErrorException;
    public abstract ArrayList<ApiArgument> arguments();
}
