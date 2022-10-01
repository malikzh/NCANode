package kz.ncanode.dto.certificate;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Jacksonized
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CertificateInfo {
    private final boolean valid;
    private final List<CertificateRevocationStatus> revocations;
    private final Date notBefore;
    private final Date notAfter;
    private final CertificateKeyUsage keyUsage;
    private final String serialNumber;
    private final String signAlg;
    private final Set<CertificateKeyUser> keyUser;
    private final String publicKey;
    private final String signature;
    private final CertificateSubject subject;
    private final CertificateSubject issuer;
}
