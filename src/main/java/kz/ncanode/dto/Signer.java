package kz.ncanode.dto;

import lombok.Builder;
import lombok.Data;

import java.security.KeyStore;

@Data
@Builder
public class Signer {
    private KeyStore key;
    private String password;
    private String alias;
}
