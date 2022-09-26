package kz.ncanode.service;

import kz.ncanode.dto.request.XmlSignRequest;
import kz.ncanode.dto.response.VerificationResponse;
import kz.ncanode.dto.response.XmlSignResponse;
import kz.ncanode.exception.ClientException;
import kz.ncanode.wrapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Objects;


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
    private final CertificateService certificateService;

    /**
     * Read XML from String
     *
     * @param xml XML-String
     * @param removeSignatures Удалять подписи в XML
     * @return Document Object
     */
    public DocumentWrapper read(String xml, boolean removeSignatures) {
        final DocumentWrapper document = new DocumentWrapper(xml);

        if (removeSignatures) {
            final Element root = document.getDocumentElement();
            final NodeList signatures = root.getElementsByTagName("ds:Signature");

            for (int i=0; i< signatures.getLength(); ++i) {
                root.removeChild(signatures.item(i));
            }
        }

        return document;
    }

    /**
     * Подписывание XML
     *
     * @param xmlSignRequest Запрос на подпись XML
     * @return Ответ с подписанным XML
     */
    public XmlSignResponse sign(XmlSignRequest xmlSignRequest) {
        final DocumentWrapper document = read(xmlSignRequest.getXml(), xmlSignRequest.isClearSignatures());

        int i = 0;

        for (KeyStoreWrapper keyStore : kalkanWrapper.read(xmlSignRequest.getSigners())) {
            document.createXmlSignature(keyStore.getCertificate(), xmlSignRequest.getSigners().get(i++).getReferenceUri())
                .sign(keyStore.getPrivateKey());
        }

        return XmlSignResponse.builder()
            .xml(document.toString())
            .build();
    }

    /**
     * Проверяет XML-подписи
     *
     * @param xml XML-строка
     * @param checkOcsp Проверять в OCSP
     * @param checkCrl Проверять в CRL
     * @return Результат проверки
     */
    public VerificationResponse verify(String xml, boolean checkOcsp, boolean checkCrl) {
        final DocumentWrapper document = read(xml, false);
        final Element root = document.getDocumentElement();
        final NodeList signatures = root.getElementsByTagName("ds:Signature");
        final int signaturesLength = signatures.getLength();

        boolean valid = true;

        final ArrayList<CertificateWrapper> certs = new ArrayList<>();

        for (int i = 0; i<signaturesLength; ++i) {
            final Element signature = (Element)signatures.item(i);

            if (Objects.isNull(signature)) {
                throw new ClientException("Bad signature: Element 'ds:Reference' is not found in XML document");
            }

            final XMLSignatureWrapper xmlSignature = new XMLSignatureWrapper(signature);

            val cert = xmlSignature.getCertificate().orElse(null);

            if (cert == null) {
                valid = false;
                certs.add(null);
                continue;
            }

            certificateService.attachValidationData(cert, checkOcsp, checkCrl);

            if (!xmlSignature.check() || !cert.isValid(checkOcsp, checkCrl)) {
                valid = false;
            }

            certs.add(cert);
        }

        return VerificationResponse.builder()
            .valid(valid)
            .signers(certs.stream().map(c -> c.toCertificateInfo(checkOcsp, checkCrl)).toList())
            .build();
    }
}
