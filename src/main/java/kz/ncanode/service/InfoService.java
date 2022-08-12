package kz.ncanode.service;

import org.springframework.stereotype.Service;

@Service
public class InfoService {
    public String getVersion() {
        return getClass().getPackage().getImplementationVersion();
    }
}
