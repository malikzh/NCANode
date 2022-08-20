package kz.ncanode.service;

import kz.ncanode.dto.request.XmlSignRequest;
import kz.ncanode.dto.response.XmlSignResponse;
import kz.ncanode.wrapper.DocumentWrapper;
import kz.ncanode.wrapper.KalkanWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * XML/XMLDSIG Service.
 *
 * Сервис отвечает за всё что связано с XML/XMLDSIG.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class XmlService {
    private final KalkanWrapper kalkanWrapper;

    /**
     * Read XML from String
     *
     * @param xml XML-String
     * @return Document Object
     */
    public DocumentWrapper read(String xml) {
        return new DocumentWrapper(xml);
    }

    /**
     * Подписывание XML
     *
     * @param xmlSignRequest Запрос на подпись XML
     * @return Ответ с подписанным XML
     */
    public XmlSignResponse sign(XmlSignRequest xmlSignRequest) {
        final DocumentWrapper document = read(xmlSignRequest.getXml());

        kalkanWrapper.read(xmlSignRequest.getSigners()).forEach(keyStore ->
            document.createXmlSignature(keyStore.getCertificate()).sign(keyStore.getPrivateKey())
        );

        return XmlSignResponse.builder()
            .xml(document.toString())
            .build();
    }
}
