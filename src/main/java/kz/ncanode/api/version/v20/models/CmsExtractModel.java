package kz.ncanode.api.version.v20.models;

import kz.ncanode.api.core.ApiModel;
import kz.ncanode.api.core.annotations.InputField;
import kz.ncanode.api.version.v20.datatypes.CmsDataType;

public class CmsExtractModel extends ApiModel {
    @InputField(required = true)
    public CmsDataType cms = new CmsDataType();
}
