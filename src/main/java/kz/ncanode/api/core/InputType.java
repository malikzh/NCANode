package kz.ncanode.api.core;

import kz.ncanode.api.exceptions.InvalidArgumentException;
import org.json.simple.JSONObject;

public interface InputType {
    void validate() throws InvalidArgumentException;
    void input(Object data);
}
