package kz.ncanode.dto.ocsp;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class OcspStatus {
    private OcspResult result;
    private Date revocationTime;
    private int revocationReason;
    private String url;
}
