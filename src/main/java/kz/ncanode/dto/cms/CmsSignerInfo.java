package kz.ncanode.dto.cms;

import kz.ncanode.dto.certificate.CertificateInfo;
import kz.ncanode.dto.tsp.TspInfo;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Jacksonized
@Data
@Builder
public class CmsSignerInfo {
    @Singular
    private List<CertificateInfo> certificates;
    private TspInfo tsp;
}
