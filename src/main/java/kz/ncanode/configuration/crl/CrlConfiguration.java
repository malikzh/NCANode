package kz.ncanode.configuration.crl;

import java.net.URL;
import java.util.Map;

public interface CrlConfiguration {
    boolean isEnabled();
    Integer getTtl();
    String getUrl();
    Map<String, URL> getUrlList();
    CrlBaseConfiguration getDelta();
}
