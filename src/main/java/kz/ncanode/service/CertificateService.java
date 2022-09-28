package kz.ncanode.service;

import kz.ncanode.wrapper.CertificateWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class CertificateService {
    public final CrlService crlService;
    public final OcspService ocspService;
    public final CaService caService;

    public void attachValidationData(final CertificateWrapper cert, boolean checkOcsp, boolean checkCrl) {
        cert.setIssuerCertificate(caService.getRootCertificateFor(cert).orElseThrow());
        cert.setOcspStatus(checkOcsp ? ocspService.verify(cert, cert.getIssuerCertificate()) : null);
        cert.setCrlStatus(checkCrl ? crlService.verify(cert) : null);
    }

    public Date getCurrentDate() {
        return new Date();
    }
}
