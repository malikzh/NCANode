package kz.ncanode.api.version.v20.models;

import kz.ncanode.api.core.ApiModel;
import kz.ncanode.api.core.annotations.InputField;
import kz.ncanode.api.exceptions.InvalidArgumentException;
import kz.ncanode.api.version.v20.datatypes.BooleanDataType;
import kz.ncanode.api.version.v20.datatypes.Pkcs12DataType;
import kz.ncanode.api.version.v20.datatypes.StringDataType;

public class InfoPKCS12Model extends ApiModel {
    @InputField(required = true)
    public Pkcs12DataType p12 = new Pkcs12DataType();

    @InputField(required = true)
    public StringDataType password = new StringDataType();

    @InputField
    public StringDataType alias = new StringDataType();

    @InputField
    public BooleanDataType checkOcsp = new BooleanDataType();

    @InputField
    public BooleanDataType checkCrl = new BooleanDataType();

    @Override
    protected void afterAccept() throws InvalidArgumentException {
        p12.loadWithPassword(password.get());
    }
}
