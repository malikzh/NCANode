package kz.ncanode.dto.tsp;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class TspInfo {
    private String serialNumber;
    private Date genTime;
    private String policy;
    private String tsa;
    private String tspHashAlgorithm;
    private String hash;
}
