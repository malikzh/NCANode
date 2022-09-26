package kz.ncanode.dto.crl;

import kz.ncanode.dto.certificate.CertificateRevocation;
import kz.ncanode.dto.certificate.CertificateRevocationStatus;
import kz.ncanode.dto.ocsp.OcspResult;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class CrlStatus {
    private final CrlResult result;
    private final String file;
    private final Date revocationDate;
    private final String reason;

    public CertificateRevocationStatus toCertificateRevocationStatus() {
        return CertificateRevocationStatus.builder()
            .revoked(result.equals(CrlResult.REVOKED))
            .revocationTime(revocationDate)
            .by(CertificateRevocation.CRL)
            .reason(reason)
            .build();
    }
}
