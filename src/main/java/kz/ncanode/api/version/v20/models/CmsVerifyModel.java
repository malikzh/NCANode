package kz.ncanode.api.version.v20.models;

import kz.ncanode.api.core.ApiModel;
import kz.ncanode.api.core.annotations.InputField;
import kz.ncanode.api.version.v20.datatypes.BooleanDataType;
import kz.ncanode.api.version.v20.datatypes.CmsDataType;

public class CmsVerifyModel extends ApiModel {
    @InputField(required = true)
    public CmsDataType cms = new CmsDataType();

    @InputField
    public BooleanDataType checkOcsp = new BooleanDataType();

    @InputField
    public BooleanDataType checkCrl = new BooleanDataType();
}
