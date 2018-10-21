package kz.ncanode.api.version.v10.arguments;

import kz.ncanode.api.ApiServiceProvider;
import kz.ncanode.api.core.ApiArgument;
import kz.ncanode.api.core.ApiVersion;
import kz.ncanode.api.exceptions.InvalidArgumentException;

import java.security.KeyStore;
import java.util.Base64;

public class P12ApiArgument extends ApiArgument {

    private boolean required = false;
    private KeyStore key = null;
    private String password = "";

    ApiVersion ver;
    ApiServiceProvider man;

    public P12ApiArgument(boolean required, ApiVersion ver, ApiServiceProvider man) {
        this.required = required;
        this.ver = ver;
        this.man = man;
    }

    @Override
    public void validate() throws InvalidArgumentException {
        if (params == null) {
            if (required) {
                throw new InvalidArgumentException("Parameter required");
            } else {
                return;
            }
        }

        String p12B64   = (String)params.get("p12");
        String password = (String)params.get("password");
        this.password   = password;

        if (p12B64 == null) {
            throw new InvalidArgumentException("Parameter required");
        }

        if (password == null) {
            throw new InvalidArgumentException("Password not specified");
        }

        try {
            key = man.pki.loadKey(Base64.getDecoder().decode(p12B64), password);
        } catch (Exception e) {
            throw new InvalidArgumentException(e.getMessage());
        }

    }

    @Override
    public Object get() {
        return key;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String name() {
        return "p12";
    }
}
