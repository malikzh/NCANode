package kz.ncanode.api.version.v10.arguments;

import kz.ncanode.api.ApiServiceProvider;
import kz.ncanode.api.core.ApiArgument;
import kz.ncanode.api.core.ApiVersion;
import kz.ncanode.api.exceptions.InvalidArgumentException;

public class TspInCmsArgument extends ApiArgument {


    ApiVersion ver;
    ApiServiceProvider man;
    private boolean required = false;
    private boolean tspInCms = false;

    public TspInCmsArgument(boolean required, ApiVersion ver, ApiServiceProvider man) {
        this.required = required;
        this.ver = ver;
        this.man = man;
    }

    @Override
    public void validate() throws InvalidArgumentException {
        try {
            if (params.get("tspInCms") == null && required) {
                throw new InvalidArgumentException("Argument 'tspInCms' is required");
            }

            tspInCms = (boolean)(params.get("tspInCms") == null ? false : params.get("tspInCms"));
        } catch (ClassCastException e) {
            //
        }
    }

    @Override
    public Object get() {
        return tspInCms;
    }

    @Override
    public String name() {
        return "tspInCms";
    }
}
