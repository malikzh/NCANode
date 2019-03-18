package kz.ncanode.api.version.v10.arguments;

import kz.ncanode.api.ApiServiceProvider;
import kz.ncanode.api.core.ApiArgument;
import kz.ncanode.api.core.ApiVersion;
import kz.ncanode.api.exceptions.InvalidArgumentException;

import java.util.Base64;

public class RawArgument extends ApiArgument {

    ApiVersion ver;
    ApiServiceProvider man;
    private boolean required = false;
    byte[] raw = null;

    public RawArgument(boolean required, ApiVersion ver, ApiServiceProvider man) {
        this.required = required;
        this.ver = ver;
        this.man = man;
    }

    @Override
    public void validate() throws InvalidArgumentException {
        String r = (String)params.get("raw");

        if (r == null) {
            if (required) {
                throw new InvalidArgumentException("Argument 'raw' is required");
            } else {
                return;
            }
        }

        raw = Base64.getDecoder().decode(r);
    }

    @Override
    public Object get() {
        return raw;
    }

    public byte[] getBytes() {
        return raw;
    }

    @Override
    public String name() {
        return "raw";
    }
}
