package kz.ncanode.api.version.v10.arguments;

import kz.ncanode.api.ApiServiceProvider;
import kz.ncanode.api.core.ApiArgument;
import kz.ncanode.api.core.ApiVersion;
import kz.ncanode.api.exceptions.InvalidArgumentException;
import kz.ncanode.pki.X509Manager;

import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class CertArgument extends ApiArgument {
    private boolean required = false;
    private X509Certificate cert = null;

    ApiVersion ver;
    ApiServiceProvider man;

    public CertArgument(boolean required, ApiVersion ver, ApiServiceProvider man) {
        this.required = required;
        this.ver = ver;
        this.man = man;
    }

    @Override
    public void validate() throws InvalidArgumentException {
        String certB64;

        try {
            certB64 = ((String)params.get("cert"));
        } catch (ClassCastException e) {
            throw  new InvalidArgumentException("invalid value");
        }

        if (certB64 == null || certB64.isEmpty()) {
            throw  new InvalidArgumentException("value not specified");
        }

        certB64 = certB64.replaceAll("\\s", "");

        try {
            cert = X509Manager.load(Base64.getDecoder().decode(certB64));
        } catch (Exception e) {
            throw new InvalidArgumentException(e.getMessage());
        }
    }

    @Override
    public Object get() {
        return cert;
    }

    @Override
    public String name() {
        return "cert";
    }
}
