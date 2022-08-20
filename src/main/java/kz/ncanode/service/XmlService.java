package kz.ncanode.service;

import kz.ncanode.wrapper.DocumentWrapper;
import kz.ncanode.wrapper.KeyStoreWrapper;
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
    /**
     * Read XML from String
     *
     * @param xml XML-String
     * @return Document Object
     */
    public DocumentWrapper read(String xml) {
        return new DocumentWrapper(xml);
    }

    public void sign(DocumentWrapper xml, KeyStoreWrapper keyStoreWrapper) {
        // todo
    }
}
