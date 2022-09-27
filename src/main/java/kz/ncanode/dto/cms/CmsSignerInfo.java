package kz.ncanode.dto.cms;

import kz.ncanode.dto.certificate.CertificateInfo;
import kz.ncanode.dto.tsp.TspInfo;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Data
@Builder
public class CmsSignerInfo {
    @Singular
    private List<CertificateInfo> certificates;
    private TspInfo tsp;
}
