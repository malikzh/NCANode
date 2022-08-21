package kz.ncanode.dto.crl;

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
}
