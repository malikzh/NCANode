package kz.ncanode.api.version.v20.datatypes;

import kz.ncanode.api.core.ApiDependencies;
import kz.ncanode.api.core.InputType;
import kz.ncanode.api.exceptions.InvalidArgumentException;

public class BooleanDataType extends ApiDependencies implements InputType {
    boolean value = false;

    @Override
    public void validate() throws InvalidArgumentException {}

    @Override
    public void input(Object data) {
        if (data != null) {
            value = (Boolean)data;
        }
    }

    public boolean get() {
        return value;
    }
}
