package kz.ncanode.dto.ocsp;

import kz.ncanode.dto.certificate.CertificateRevocation;
import kz.ncanode.dto.certificate.CertificateRevocationStatus;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class OcspStatus {
    private OcspResult result;
    private Date revocationTime;
    private int revocationReason;
    private String message;
    private String url;

    public boolean isActive() {
        return getResult().equals(OcspResult.ACTIVE);
    }

    public CertificateRevocationStatus toCertificateRevocationStatus() {
        return CertificateRevocationStatus.builder()
            .revoked(result.equals(OcspResult.REVOKED))
            .revocationTime(revocationTime)
            .by(CertificateRevocation.OCSP)
            .reason(message)
            .build();
    }
}
