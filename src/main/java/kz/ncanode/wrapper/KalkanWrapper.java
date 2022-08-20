package kz.ncanode.wrapper;

import kz.gov.pki.kalkan.jce.provider.KalkanProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KalkanWrapper {
    private final KalkanProvider kalkanProvider;
}
