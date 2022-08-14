package kz.ncanode.controller;

import kz.ncanode.dto.request.XmlSignRequest;
import kz.ncanode.exception.ServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("xml")
@RequiredArgsConstructor
public class XmlController {

    @PostMapping("/sign")
    public String sign(@Valid @RequestBody XmlSignRequest xmlSignRequest) {
        throw new ServerException("server err", new Exception("Lol kek checburek"));
        //return "asd";
    }
}
