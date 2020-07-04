package kz.ncanode.api.version.v20.datatypes;

import kz.ncanode.api.core.ApiDependencies;
import kz.ncanode.api.core.InputType;
import kz.ncanode.api.exceptions.InvalidArgumentException;

import java.util.List;

public class StringSelectDataType extends ApiDependencies implements InputType {
    String value = null;
    List<String> valueList;

    public StringSelectDataType(String defaultValue, List<String> valueList) {
        this.valueList = valueList;
        this.value = defaultValue;
    }

    @Override
    public void validate() throws InvalidArgumentException {
        if (!valueList.contains(value)) {
            throw new InvalidArgumentException("Invalid value. Possible values is: " + valueList.toString());
        }
    }

    @Override
    public void input(Object data) {
        if (data != null) {
            value = (String) data;
        }
    }

    public String get() {
        return value;
    }
}
