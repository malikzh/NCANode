package kz.ncanode.dto.request;

import kz.ncanode.dto.certificate.CertificateRevocation;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Data
@SuperBuilder
public abstract class VerifyRequest {

    @Builder.Default
    private Set<CertificateRevocation> revocationCheck = Set.of();

}
