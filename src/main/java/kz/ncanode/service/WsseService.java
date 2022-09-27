package kz.ncanode.service;

import kz.ncanode.dto.request.WsseSignRequest;
import kz.ncanode.dto.response.XmlSignResponse;
import kz.ncanode.exception.ClientException;
import kz.ncanode.exception.KeyException;
import kz.ncanode.exception.ServerException;
import kz.ncanode.wrapper.CertificateWrapper;
import kz.ncanode.wrapper.KalkanWrapper;
import kz.ncanode.wrapper.KeyStoreWrapper;
import kz.ncanode.wrapper.XMLSignatureWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ws.security.message.token.SecurityTokenReference;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.message.WSSecHeader;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.transforms.Transforms;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Сервис для работы с Wsse (Soap)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WsseService {

    private final KalkanWrapper kalkanWrapper;

    /**
     * Подписывает Wsse XML
     *
     * @param wsseSignRequest Запрос на подпись
     * @return Подписанный SOAP-конверт
     */
    public XmlSignResponse sign(final WsseSignRequest wsseSignRequest) {
        try {
            // read key
            final KeyStoreWrapper keystore = kalkanWrapper.read(wsseSignRequest.getP12(), wsseSignRequest.getAlias(), wsseSignRequest.getPassword());
            final CertificateWrapper cert = keystore.getCertificate();

            // sign a soap request according to a reference implementation from smartbridge
            SOAPMessage msg = MessageFactory.newInstance().createMessage(null, new ByteArrayInputStream(wsseSignRequest.getXml().getBytes(StandardCharsets.UTF_8)));
            SOAPEnvelope env = msg.getSOAPPart().getEnvelope();
            SOAPBody body = env.getBody();

            String bodyId = "id-" + UUID.randomUUID();
            body.addAttribute(new QName(WSConstants.WSU_NS, "Id", WSConstants.WSU_PREFIX), bodyId);

            SOAPHeader header = env.getHeader();
            if (header == null) {
                header = env.addHeader();
            }

            Document doc = env.getOwnerDocument();

            Transforms transforms = new Transforms(env.getOwnerDocument());
            transforms.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);

            XMLSignatureWrapper signature = new XMLSignatureWrapper(doc, cert.getSignAlgorithmId(), Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);

            signature.getXmlSignature().addDocument("#" + bodyId, transforms, cert.getHashAlgorithmId());
            signature.getXmlSignature().getSignedInfo().getSignatureMethodElement().setNodeValue(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);

            WSSecHeader secHeader = new WSSecHeader(doc);
            secHeader.setMustUnderstand(true);
            secHeader.insertSecurityHeader();
            header.appendChild(secHeader.getSecurityHeaderElement());
            header.getFirstChild().appendChild(signature.getXmlSignature().getElement());

            SecurityTokenReference reference = new SecurityTokenReference(doc);
            reference.setKeyIdentifier(cert.getX509Certificate());

            signature.getXmlSignature().getKeyInfo().addUnknownElement(reference.getElement());
            signature.getXmlSignature().sign(keystore.getPrivateKey());



            try (StringWriter os = new StringWriter()) {
                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer trans = tf.newTransformer();
                trans.transform(new DOMSource(doc), new StreamResult(os));

                return XmlSignResponse.builder()
                    .xml(os.toString())
                    .build();
            }
        } catch (KeyException e) {
            throw new ClientException(e.getMessage(), e);
        }
        catch (Exception e) {
            throw new ServerException("Cannot create SOAP Envelope. Please see logs");
        }
    }
}
