package kz.ncanode.dto.http;

import lombok.Data;

@Data
public class HttpProxyConfig {
    private String url;
    private String username;
    private String password;
}
