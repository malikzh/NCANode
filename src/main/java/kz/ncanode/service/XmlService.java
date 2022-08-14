package kz.ncanode.service;

import kz.gov.pki.kalkan.jce.provider.KalkanProvider;
import kz.ncanode.dto.request.XmlSignRequest;
import kz.ncanode.dto.response.XmlSignResponse;
import kz.ncanode.exception.ServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


/**
 * XML/XMLDSIG Service.
 *
 * Сервис отвечае за всё что связано с XML/XMLDSIG.
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
    public Document read(String xml) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);

            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            try(ByteArrayInputStream xmlStream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))) {
                return documentBuilder.parse(xmlStream);
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            log.error("XML parsing error", e);
            throw new ServerException("Cannot read XML", e);
        }
    }

    /**
     * Sign using XMLDSIG
     *
     * @param request Request
     * @return Signed xml
     */
    public XmlSignResponse sign(XmlSignRequest request) {
        var xml = read(request.getXml());


        return null;
    }
}
