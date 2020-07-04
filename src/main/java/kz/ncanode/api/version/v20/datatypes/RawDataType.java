package kz.ncanode.api.version.v20.datatypes;

import kz.ncanode.api.core.ApiDependencies;
import kz.ncanode.api.core.InputType;
import kz.ncanode.api.exceptions.InvalidArgumentException;

import java.util.Base64;

public class RawDataType extends ApiDependencies implements InputType {
    String value = null;
    byte[] raw = null;

    @Override
    public void validate() throws InvalidArgumentException {
        try {
            raw = Base64.getDecoder().decode(value);
        } catch (Exception e) {
            throw new InvalidArgumentException(e.getMessage());
        }
    }

    @Override
    public void input(Object data) {
        value = (String)data;
    }

    public byte[] get() {
        return raw;
    }
}
