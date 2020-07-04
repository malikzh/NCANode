package kz.ncanode.api.version.v20.models;

import kz.ncanode.api.core.ApiModel;
import kz.ncanode.api.core.annotations.InputField;
import kz.ncanode.api.version.v20.datatypes.*;

import java.util.ArrayList;
import java.util.Arrays;

public class CmsSignModel extends ApiModel {
    @InputField(required = true)
    public Pkcs12ArrayDataType p12array = new Pkcs12ArrayDataType();

    @InputField(required = true)
    public RawDataType data = new RawDataType();

    @InputField
    public StringSelectDataType useTsaPolicy = new StringSelectDataType("TSA_GOST_POLICY",
            new ArrayList<String>(
            Arrays.asList("TSA_GOST_POLICY", "TSA_GOSTGT_POLICY")
    ));

    @InputField
    public BooleanDataType withTsp = new BooleanDataType();
}
