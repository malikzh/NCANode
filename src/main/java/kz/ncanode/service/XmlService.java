package kz.ncanode.service;

import kz.gov.pki.kalkan.jce.provider.KalkanProvider;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class XmlService {
    private final KalkanProvider kalkanProvider;

    public Document load(String xml) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);

        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        try(ByteArrayInputStream xmlStream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))) {
            return documentBuilder.parse(xmlStream);
        }
    }
}
