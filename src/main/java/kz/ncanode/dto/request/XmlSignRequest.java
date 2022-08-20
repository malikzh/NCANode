package kz.ncanode.dto.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Builder
public class XmlSignRequest {
    @NotEmpty
    private String xml;

    @NotEmpty
    private List<SignerRequest> signers;

    private boolean clearSignatures;
}
