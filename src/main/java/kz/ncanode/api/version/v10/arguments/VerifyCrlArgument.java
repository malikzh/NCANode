package kz.ncanode.api.version.v10.arguments;

import kz.ncanode.api.ApiServiceProvider;
import kz.ncanode.api.core.ApiArgument;
import kz.ncanode.api.core.ApiVersion;
import kz.ncanode.api.exceptions.InvalidArgumentException;

public class VerifyCrlArgument extends ApiArgument {

    ApiVersion ver;
    ApiServiceProvider man;
    private boolean required = false;
    private boolean verifyCrl = false;

    public VerifyCrlArgument(boolean required, ApiVersion ver, ApiServiceProvider man) {
        this.required = required;
        this.ver = ver;
        this.man = man;
    }

    @Override
    public void validate() throws InvalidArgumentException {
        try {
            verifyCrl = (boolean)(params.get("verifyCrl") == null ? false : params.get("verifyCrl"));
        } catch (ClassCastException e) {
            //
        }
    }

    @Override
    public Object get() {
        return verifyCrl;
    }

    @Override
    public String name() {
        return "verifyCrl";
    }
}
