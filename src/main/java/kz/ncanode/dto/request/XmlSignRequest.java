package kz.ncanode.dto.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class XmlSignRequest {
    @NotEmpty
    private String xml;

    @NotEmpty
    private List<SignerRequest> signerRequestList;
}
