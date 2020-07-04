package kz.ncanode.api.version.v20.datatypes;

import kz.ncanode.api.core.ApiDependencies;
import kz.ncanode.api.core.InputType;
import kz.ncanode.api.exceptions.InvalidArgumentException;
import kz.ncanode.pki.X509Manager;

import java.security.cert.X509Certificate;
import java.util.Base64;

public class CertDataType extends ApiDependencies implements InputType {
    private X509Certificate cert;
    private String b64;

    @Override
    public void validate() throws InvalidArgumentException {
        try {
            cert = X509Manager.load(Base64.getDecoder().decode(b64));
        } catch (Exception e) {
            throw new InvalidArgumentException(e.getMessage());
        }
    }

    @Override
    public void input(Object data) {
        b64 = (String)data;
        b64 = b64.replaceAll("\\s", "");
    }

    public X509Certificate getCert() {
        return cert;
    }
}
