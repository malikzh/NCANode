package kz.ncanode.dto.certificate;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.Date;

@Jacksonized
@Data
@Builder
public class CertificateRevocationStatus {
    private boolean revoked;
    private CertificateRevocation by;
    private Date revocationTime;
    private String reason;
}
