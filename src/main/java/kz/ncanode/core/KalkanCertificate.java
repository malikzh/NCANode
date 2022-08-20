package kz.ncanode.core;

import kz.ncanode.util.KalkanUtil;
import lombok.Getter;

import java.security.cert.X509Certificate;

public class KalkanCertificate {

    @Getter
    private final X509Certificate x509Certificate;

    private final String[] signAlg;

    public KalkanCertificate(X509Certificate certificate) {
        x509Certificate = certificate;
        signAlg = KalkanUtil.getSignMethodByOID(x509Certificate.getSigAlgOID());
    }

    /**
     * Возвращает ID алгоритма подписи
     *
     * @return String
     */
    public String getSignAlgorithmId() {
        return signAlg[0];
    }

    /**
     * Возвращает ID алгоритма хэширования
     *
     * @return String
     */
    public String getHashAlgorithmId() {
        return signAlg[1];
    }
}
