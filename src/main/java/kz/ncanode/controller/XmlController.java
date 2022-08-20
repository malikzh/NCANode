package kz.ncanode.controller;

import kz.ncanode.dto.request.XmlSignRequest;
import kz.ncanode.dto.response.XmlSignResponse;
import kz.ncanode.service.XmlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("xml")
@RequiredArgsConstructor
public class XmlController {
    private final XmlService xmlService;

    @PostMapping("/sign")
    public ResponseEntity<XmlSignResponse> sign(@Valid @RequestBody XmlSignRequest xmlSignRequest) {
        return ResponseEntity.ok(xmlService.sign(xmlSignRequest));
    }
}
