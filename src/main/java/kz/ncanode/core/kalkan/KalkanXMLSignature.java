package kz.ncanode.core.kalkan;

import kz.ncanode.exception.ServerException;
import lombok.Getter;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;
import org.w3c.dom.Document;

import java.security.PrivateKey;

public class KalkanXMLSignature {

    @Getter
    private final XMLSignature xmlSignature;

    public KalkanXMLSignature(Document xml, KalkanCertificate certificate, PrivateKey privateKey) {
        try {
            xmlSignature = new XMLSignature(xml, "", certificate.getSignAlgorithmId());
        } catch (XMLSecurityException e) {
            throw new ServerException("XML Signature creation error", e);
        }
    }
}
