package kz.ncanode.controller;

import kz.gov.pki.kalkan.asn1.knca.KNCAObjectIdentifiers;
import kz.gov.pki.kalkan.jce.provider.cms.CMSSignedData;
import kz.gov.pki.kalkan.tsp.TSPAlgorithms;
import kz.ncanode.dto.certificate.CertificateRevocation;
import kz.ncanode.dto.request.TspCreateRequest;
import kz.ncanode.dto.response.TspCreateResponse;
import kz.ncanode.exception.ClientException;
import kz.ncanode.service.TspService;
import lombok.RequiredArgsConstructor;
// import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import javax.validation.Valid;

@RestController
@RequestMapping("tsp")
@RequiredArgsConstructor
public class TspController {
private final TspService tspService;

    @PostMapping("/create")
    public ResponseEntity<TspCreateResponse> create(@Valid @RequestBody TspCreateRequest tspCreateRequest) {
        String tspHashAlgorithm = TSPAlgorithms.GOST34311;
        String useTsaPolicy = KNCAObjectIdentifiers.tsa_gost2015_policy.getId();
        byte[] raw = tspCreateRequest.getXml().getBytes();
        try {
            CMSSignedData tsp = tspService.create(raw, tspHashAlgorithm, useTsaPolicy).toCMSSignedData();
            String tspBase64 = new String(Base64.getEncoder().encode(tsp.getEncoded()));
            return ResponseEntity.ok(TspCreateResponse.builder().xml(tspBase64).build());
        } catch (Exception e) {
            throw new ClientException(e.getMessage());
        }
    }
}
