package kz.ncanode.api.version.v20.datatypes;

import kz.ncanode.api.core.ApiDependencies;
import kz.ncanode.api.core.InputType;
import kz.ncanode.api.exceptions.InvalidArgumentException;

public class StringDataType extends ApiDependencies implements InputType {
    String value = null;

    @Override
    public void validate() throws InvalidArgumentException {

    }

    @Override
    public void input(Object data) {
        value = (String)data;
    }

    public String get() {
        return value;
    }
}
