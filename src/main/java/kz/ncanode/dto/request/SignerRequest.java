package kz.ncanode.dto.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class SignerRequest {

    @NotEmpty
    private String key;

    @NotEmpty
    private String password;

    private String keyAlias;

}
