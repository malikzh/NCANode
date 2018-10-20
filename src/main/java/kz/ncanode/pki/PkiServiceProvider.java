package kz.ncanode.pki;

import kz.ncanode.config.ConfigServiceProvider;
import kz.ncanode.ioc.ServiceProvider;
import kz.ncanode.log.ErrorLogServiceProvider;
import kz.ncanode.log.OutLogServiceProvider;

import java.security.cert.X509Certificate;

public class PkiServiceProvider implements ServiceProvider {
    private ConfigServiceProvider   config = null;
    private OutLogServiceProvider   out    = null;
    private ErrorLogServiceProvider err  = null;

    enum OCSPResult {
        ACTIVE,
        REVOKED,
        UNKNOWN
    }

    public PkiServiceProvider(ConfigServiceProvider config, OutLogServiceProvider out, ErrorLogServiceProvider err) {
        this.config = config;
        this.out    = out;
        this.err    = err;
    }

    public OCSPResult verifyOcsp(X509Certificate cert) {



    }
}
