package kz.ncanode.dto.response;

import kz.ncanode.dto.certificate.CertificateInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class Pkcs12InfoResponse extends StatusResponse {
    private List<CertificateInfo> certificates;
}
