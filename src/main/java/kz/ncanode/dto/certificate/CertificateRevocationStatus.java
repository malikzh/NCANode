package kz.ncanode.dto.certificate;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class CertificateRevocationStatus {
    private boolean revoked;
    private CertificateRevocation by;
    private Date revocationTime;
    private String reason;
}
