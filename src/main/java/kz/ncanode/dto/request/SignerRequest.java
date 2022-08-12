package kz.ncanode.dto.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class SignerRequest {
    @NotEmpty
    private String key;

    @NotEmpty
    private String password;

    private String keyAlias;
}
