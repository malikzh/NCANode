package kz.ncanode.api.version.v20.models;

import kz.gov.pki.kalkan.jce.provider.cms.CMSException;
import kz.gov.pki.kalkan.jce.provider.cms.CMSProcessable;
import kz.gov.pki.kalkan.jce.provider.cms.CMSProcessableByteArray;
import kz.gov.pki.kalkan.jce.provider.cms.CMSSignedData;
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

    protected CMSSignedData signedData;

    /**
     * Если на подпись прислали уже подписанный документ,
     * он будет инициализирован в this.signedData
     */
    @Override
    protected void afterAccept() {
        try {
            this.signedData = new CMSSignedData(data.get());
        } catch (CMSException ignored) {
            // документ не подписан, это нормально
        }
    }

    /**
     * Возвращает данные, которые требуется подписать.
     * Если получен уже подписанный документ, то вернёт его исходное содержимое.
     * Иначе вернёт полученный из запроса документ целиком.
     */
    public CMSProcessable getDataToEncode() {
        if (this.isAlreadySigned()) {
            return this.signedData.getSignedContent();
        }

        return new CMSProcessableByteArray(this.data.get());
    }

    /**
     * Запрошена ли подпись уже подписанного документа
     */
    public boolean isAlreadySigned() {
        return this.signedData != null;
    }

    public CMSSignedData getSignedData() {
        return signedData;
    }
}
