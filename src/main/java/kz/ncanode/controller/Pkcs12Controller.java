package kz.ncanode.controller;

import kz.ncanode.dto.certificate.CertificateRevocation;
import kz.ncanode.dto.request.Pkcs12InfoRequest;
import kz.ncanode.dto.response.Pkcs12InfoResponse;
import kz.ncanode.service.CertificateService;
import kz.ncanode.wrapper.KalkanWrapper;
import kz.ncanode.wrapper.KeyStoreWrapper;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("pkcs12")
@RequiredArgsConstructor
public class Pkcs12Controller {

    private final KalkanWrapper kalkanWrapper;
    private final CertificateService certificateService;

    @PostMapping("/info")
    public ResponseEntity<Pkcs12InfoResponse> info(@Valid @RequestBody Pkcs12InfoRequest request) {
        return ResponseEntity.ok(Pkcs12InfoResponse.builder()
            .certificates(Optional.of(request.getKeys()).map(kalkanWrapper::read).orElseThrow().stream()
                .map(KeyStoreWrapper::getCertificate).map(cert -> {
                    val date = certificateService.getCurrentDate();
                    val withOcsp = request.getRevocationCheck().contains(CertificateRevocation.OCSP);
                    val withCrl = request.getRevocationCheck().contains(CertificateRevocation.CRL);

                    certificateService.attachValidationData(cert, withOcsp, withCrl);

                    return cert.toCertificateInfo(date, withOcsp, withCrl);
                }).toList())
            .build());
    }
}
