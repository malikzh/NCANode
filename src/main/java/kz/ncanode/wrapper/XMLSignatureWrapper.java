package kz.ncanode.wrapper;

import kz.ncanode.exception.ServerException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.security.PrivateKey;

@Slf4j
public class XMLSignatureWrapper {

    @Getter
    private final XMLSignature xmlSignature;

    public XMLSignatureWrapper(Document document, String signAlgorithmId) {
        try {
            xmlSignature = new XMLSignature(document, "", signAlgorithmId);
        } catch (XMLSecurityException e) {
            throw new ServerException("XML Signature creation error", e);
        }
    }

    public XMLSignatureWrapper(Document document, String signAlgorithmId, String c14nAlgorithmId) {
        try {
            Element signatureMethod = XMLUtils.createElementInSignatureSpace(document, "SignatureMethod");
            signatureMethod.setAttributeNS(null, "Algorithm", signAlgorithmId);

            Element c14nMethod = XMLUtils.createElementInSignatureSpace(document, "CanonicalizationMethod");
            c14nMethod.setAttributeNS(null, "Algorithm", c14nAlgorithmId);

            xmlSignature = new XMLSignature(document, "", signatureMethod, c14nMethod);
        } catch (XMLSecurityException e) {
            log.error("XML Signature creation error", e);
            throw new ServerException("XML Signature creation error", e);
        }
    }

    public void sign(PrivateKey privateKey) {
        try {
            getXmlSignature().sign(privateKey);
        } catch (XMLSignatureException e) {
            log.error("XML Signing error", e);
            throw new ServerException("XML Signing error", e);
        }
    }
}
