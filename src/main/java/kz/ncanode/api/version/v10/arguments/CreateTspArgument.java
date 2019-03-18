package kz.ncanode.api.version.v10.arguments;

import kz.ncanode.api.ApiServiceProvider;
import kz.ncanode.api.core.ApiArgument;
import kz.ncanode.api.core.ApiVersion;
import kz.ncanode.api.exceptions.InvalidArgumentException;

public class CreateTspArgument extends ApiArgument {

    ApiVersion ver;
    ApiServiceProvider man;
    private boolean required = false;
    private boolean createTsp = false;

    public CreateTspArgument(boolean required, ApiVersion ver, ApiServiceProvider man) {
        this.required = required;
        this.ver = ver;
        this.man = man;
    }

    @Override
    public void validate() throws InvalidArgumentException {
        try {
            if (params.get("createTsp") == null && required) {
                throw new InvalidArgumentException("Argument 'createTsp' is required");
            }

            createTsp = (boolean)(params.get("createTsp") == null ? false : params.get("createTsp"));
        } catch (ClassCastException e) {
            //
        }
    }

    @Override
    public Object get() {
        return createTsp;
    }

    @Override
    public String name() {
        return "createTsp";
    }
}
