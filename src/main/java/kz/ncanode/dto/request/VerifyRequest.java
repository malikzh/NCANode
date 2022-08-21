package kz.ncanode.dto.request;

import kz.ncanode.constants.RevocationCheckMode;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Data
@SuperBuilder
public abstract class VerifyRequest {

    @Builder.Default
    private Set<RevocationCheckMode> revocationCheck = Set.of(RevocationCheckMode.CRL);

}
