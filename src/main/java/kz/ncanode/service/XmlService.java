package kz.ncanode.service;

import kz.gov.pki.kalkan.jce.provider.KalkanProvider;
import kz.ncanode.dto.Signer;
import kz.ncanode.wrapper.XMLDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;


/**
 * XML/XMLDSIG Service.
 *
 * Сервис отвечает за всё что связано с XML/XMLDSIG.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class XmlService {
    private final KalkanProvider kalkanProvider;
    private final KeyService keyService;

    /**
     * Read XML from String
     *
     * @param xml XML-String
     * @return Document Object
     */
    public XMLDocument read(String xml) {
        return new XMLDocument(xml);
    }

    public void sign(Document xml, Signer signer) {
        // todo
    }
}
