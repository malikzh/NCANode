package kz.ncanode.api.version.v10.arguments;

import kz.ncanode.api.ApiServiceProvider;
import kz.ncanode.api.core.ApiArgument;
import kz.ncanode.api.core.ApiVersion;
import kz.ncanode.api.exceptions.InvalidArgumentException;

public class VerifyOcspArgument extends ApiArgument {

    ApiVersion ver;
    ApiServiceProvider man;
    private boolean required = false;
    private boolean verifyOcsp = false;

    public VerifyOcspArgument(boolean required, ApiVersion ver, ApiServiceProvider man) {
        this.required = required;
        this.ver = ver;
        this.man = man;
    }

    @Override
    public void validate() throws InvalidArgumentException {
        try {
            verifyOcsp = (boolean)(params.get("verifyOcsp") == null ? false : params.get("verifyOcsp"));
        } catch (ClassCastException e) {
            //
        }
    }

    @Override
    public Object get() {
        return verifyOcsp;
    }

    @Override
    public String name() {
        return "verifyOcsp";
    }
}
