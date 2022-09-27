package kz.ncanode.dto.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class WsseSignRequest {
    @NotEmpty
    private String xml;

    @NotEmpty
    private String p12;

    @NotEmpty
    private String password;

    private String alias;

    private boolean trimXml = false;
}
