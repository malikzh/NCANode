package kz.ncanode.api.version.v20.models;

import kz.ncanode.api.core.ApiModel;
import kz.ncanode.api.core.annotations.InputField;
import kz.ncanode.api.version.v20.datatypes.BooleanDataType;
import kz.ncanode.api.version.v20.datatypes.CertDataType;

public class InfoX509Model extends ApiModel {
    @InputField(required = true)
    public CertDataType cert = new CertDataType();

    @InputField
    public BooleanDataType checkOcsp = new BooleanDataType();

    @InputField
    public BooleanDataType checkCrl = new BooleanDataType();
}
