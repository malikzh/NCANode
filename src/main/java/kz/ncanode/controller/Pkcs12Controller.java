package kz.ncanode.controller;

import kz.ncanode.dto.request.Pkcs12InfoRequest;
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
@RequestMapping("pkcs12")
@RequiredArgsConstructor
public class Pkcs12Controller {
    private final CertificateService certificateService;

    @PostMapping("/info")
    public ResponseEntity<VerificationResponse> info(@Valid @RequestBody Pkcs12InfoRequest request) {
        return ResponseEntity.ok(certificateService.verifyCerts(request));
    }
}
