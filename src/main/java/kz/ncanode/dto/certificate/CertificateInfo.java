package kz.ncanode.dto.certificate;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CertificateInfo {
    private final boolean valid;
    private final LocalDateTime notBefore;
    private final LocalDateTime notAfter;
    private final CertificateKeyUsage keyUsage;
    private final String serialNumber;
    private final String signAlg;
    private final CertificateKeyUser keyUser;
    private final String publicKey;
    private final String signature;
    private final CertificateSubject subject;
    private final CertificateSubject issuer;
}
