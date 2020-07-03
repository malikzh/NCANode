package kz.ncanode.api.version.v20.datatypes;

import kz.ncanode.api.core.ApiDependencies;
import kz.ncanode.api.core.InputType;
import kz.ncanode.api.exceptions.InvalidArgumentException;

import java.security.KeyStore;
import java.util.Base64;

public class Pkcs12DataType extends ApiDependencies implements InputType {
    String p12B64;
    KeyStore key;

    @Override
    public void validate() throws InvalidArgumentException {

    }

    @Override
    public void input(Object data) {
        p12B64 = (String)data;
    }

    public void loadWithPassword(String password) throws InvalidArgumentException {
        try {
            key = getApiServiceProvider().pki.loadKey(Base64.getDecoder().decode(p12B64), password);
        } catch (Exception e) {
            throw new InvalidArgumentException(e.getMessage());
        }
    }

    public KeyStore get() {
        return key;
    }
}
