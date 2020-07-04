package kz.ncanode.api.version.v20.datatypes;

import kz.gov.pki.kalkan.jce.provider.cms.CMSException;
import kz.gov.pki.kalkan.jce.provider.cms.CMSSignedData;
import kz.gov.pki.kalkan.util.encoders.Base64;
import kz.ncanode.api.core.ApiDependencies;
import kz.ncanode.api.core.InputType;
import kz.ncanode.api.exceptions.InvalidArgumentException;

public class CmsDataType extends ApiDependencies implements InputType {
    String cmsB64 = null;
    CMSSignedData cms = null;

    @Override
    public void validate() throws InvalidArgumentException {
        byte[] decoded = Base64.decode(cmsB64);
        try {
            cms = new CMSSignedData(decoded);
        } catch (CMSException e) {
            throw new InvalidArgumentException(e.getMessage());
        }
    }

    @Override
    public void input(Object data) {
        cmsB64 = (String)data;
    }

    public CMSSignedData get() {
        return cms;
    }
}
