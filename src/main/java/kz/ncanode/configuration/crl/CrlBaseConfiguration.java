package kz.ncanode.configuration.crl;

import kz.ncanode.util.Util;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.Min;
import java.net.URL;
import java.util.Map;

@Slf4j
@Getter
@Setter
public class CrlBaseConfiguration {
    private boolean enabled;
    @Min(1)
    private Integer ttl;
    private String url;
    private CrlBaseConfiguration delta;

    public Map<String, URL> getUrlList() {
        return Util.urlMap(getUrl(), log);
    }
}
