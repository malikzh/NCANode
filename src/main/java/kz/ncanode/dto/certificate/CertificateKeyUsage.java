package kz.ncanode.dto.certificate;

public enum CertificateKeyUsage {
    UNKNOWN,
    AUTH,
    SIGN,
    ;

    public static CertificateKeyUsage fromKeyUsageBits(boolean[] keyUsageBits) {
        if (keyUsageBits[0] && keyUsageBits[1]) {
            return SIGN;
        } else if (keyUsageBits[0] && keyUsageBits[2]) {
            return AUTH;
        }

        return UNKNOWN;
    }
}
