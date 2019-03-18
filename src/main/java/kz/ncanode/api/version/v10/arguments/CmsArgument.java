package kz.ncanode.api.version.v10.arguments;

import kz.gov.pki.kalkan.jce.provider.cms.CMSException;
import kz.gov.pki.kalkan.jce.provider.cms.CMSSignedData;
import kz.gov.pki.kalkan.util.encoders.Base64;
import kz.ncanode.api.ApiServiceProvider;
import kz.ncanode.api.core.ApiArgument;
import kz.ncanode.api.core.ApiVersion;
import kz.ncanode.api.exceptions.InvalidArgumentException;

public class CmsArgument extends ApiArgument {
    ApiVersion ver;
    ApiServiceProvider man;
    private boolean required = false;
    private CMSSignedData cms = null;

    public CmsArgument(boolean required, ApiVersion ver, ApiServiceProvider man) {
        this.required = required;
        this.ver = ver;
        this.man = man;
    }

    @Override
    public void validate() throws InvalidArgumentException {
        String cmsBase64 = (String)params.get("cms");

        if (cmsBase64 == null) {
            if (required) {
                throw new InvalidArgumentException("Argument 'cms' is required");
            } else {
                return;
            }
        }

        byte[] decoded = Base64.decode(cmsBase64);
        try {
            cms = new CMSSignedData(decoded);
        } catch (CMSException e) {
            throw new InvalidArgumentException(e.getMessage());
        }
    }

    @Override
    public Object get() {
        return cms;
    }

    @Override
    public String name() {
        return "cms";
    }
}
