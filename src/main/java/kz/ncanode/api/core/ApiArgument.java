package kz.ncanode.api.core;

import kz.ncanode.api.exceptions.InvalidArgumentException;
import org.json.simple.JSONObject;

public abstract class ApiArgument {

    public JSONObject params = null;

    public abstract void validate() throws InvalidArgumentException;
    public abstract Object get();
    public abstract String name();
}
