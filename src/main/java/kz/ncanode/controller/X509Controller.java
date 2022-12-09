package kz.ncanode.controller;

import kz.ncanode.dto.certificate.CertificateRevocation;
import kz.ncanode.dto.request.X509InfoRequest;
import kz.ncanode.dto.response.VerificationResponse;
import kz.ncanode.service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("x509")
@RequiredArgsConstructor
public class X509Controller {
    private final CertificateService certificateService;

    @PostMapping("/info")
    public ResponseEntity<VerificationResponse> verify(@Valid @RequestBody X509InfoRequest x509InfoRequest) {
        return ResponseEntity.ok(certificateService.info(x509InfoRequest.getCerts(),
            x509InfoRequest.getRevocationCheck().contains(CertificateRevocation.OCSP),
            x509InfoRequest.getRevocationCheck().contains(CertificateRevocation.CRL)
        ));
    }
}
