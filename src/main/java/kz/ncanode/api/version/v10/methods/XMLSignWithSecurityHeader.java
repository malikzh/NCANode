package kz.ncanode.api.version.v10.methods;

import kz.gov.pki.kalkan.jce.provider.cms.CMSSignedData;
import kz.ncanode.Helper;
import kz.ncanode.api.ApiServiceProvider;
import kz.ncanode.api.core.ApiArgument;
import kz.ncanode.api.core.ApiMethod;
import kz.ncanode.api.core.ApiVersion;
import kz.ncanode.api.exceptions.ApiErrorException;
import kz.ncanode.api.version.v10.arguments.*;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.message.WSSecHeader;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.XMLUtils;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import jakarta.xml.soap.*;
import javax.xml.namespace.QName;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;
import java.util.UUID;

public class XMLSignWithSecurityHeader extends ApiMethod {
    public XMLSignWithSecurityHeader(ApiVersion ver, ApiServiceProvider man) {
        super(ver, man);
    }

    @Override
    public JSONObject handle() throws ApiErrorException {
        KeyStore p12 = (KeyStore) args.get(0).get();
        Document xml = (Document) args.get(1).get();

        String rawDocument = "";

        try {
            StringWriter os = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();
            trans.transform(new DOMSource(xml), new StreamResult(os));
            os.close();
            rawDocument = os.toString();
        } catch (Exception e) {
            throw new ApiErrorException(e.getMessage());
        }

        // todo Добавить возможность выбора алиаса
        Enumeration<String> als = null;
        try {
            als = p12.aliases();
        } catch (KeyStoreException e) {
            throw new ApiErrorException(e.getMessage());
        }
        String alias = null;
        while (als.hasMoreElements()) {
            alias = als.nextElement();
        }

        // get private key
        PrivateKey privateKey;
        try {
            String password = ((P12ApiArgument) args.get(0)).getPassword();
            privateKey = (PrivateKey) p12.getKey(alias, password.toCharArray());
        } catch (Exception e) {
            throw new ApiErrorException(e.getMessage());
        }

        // get cert
        X509Certificate cert = null;
        try {
            cert = (X509Certificate) p12.getCertificate(alias);
        } catch (KeyStoreException e) {
            throw new ApiErrorException(e.getMessage());
        }

        String[] alg = Helper.getSignMethodByOID(cert.getSigAlgOID());
        String signMethod = alg[0];
        String digestMethod = alg[1];

        InputStream is;
        try {
            is = new ByteArrayInputStream(rawDocument.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new ApiErrorException(e.getMessage());
        }

        String result = "";

        try {
            // sign a soap request according to a reference implementation from smartbridge
            SOAPMessage msg = MessageFactory.newInstance().createMessage(null, is);
            SOAPEnvelope env = msg.getSOAPPart().getEnvelope();
            SOAPBody body = env.getBody();

            String bodyId = "id-" + UUID.randomUUID().toString();
            body.addAttribute(new QName(WSConstants.WSU_NS, "Id", WSConstants.WSU_PREFIX), bodyId);

            SOAPHeader header = env.getHeader();
            if (header == null) {
                header = env.addHeader();
            }

            Document doc = env.getOwnerDocument();

            Transforms transforms = new Transforms(env.getOwnerDocument());
            transforms.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);

            Element c14nMethod = XMLUtils.createElementInSignatureSpace(doc, "CanonicalizationMethod");
            c14nMethod.setAttributeNS(null, "Algorithm", Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);

            Element signatureMethod = XMLUtils.createElementInSignatureSpace(doc, "SignatureMethod");
            signatureMethod.setAttributeNS(null, "Algorithm", signMethod);

            XMLSignature sig = new XMLSignature(doc, "", signatureMethod, c14nMethod);
            sig.addDocument("#" + bodyId, transforms, digestMethod);
            sig.getSignedInfo().getSignatureMethodElement().setNodeValue(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);

            WSSecHeader secHeader = new org.apache.wss4j.dom.message.WSSecHeader(doc);
            secHeader.setMustUnderstand(true);
            secHeader.insertSecurityHeader();
            header.appendChild(secHeader.getSecurityHeaderElement());
            header.getFirstChild().appendChild(sig.getElement());

            org.apache.ws.security.message.token.SecurityTokenReference reference = new org.apache.ws.security.message.token.SecurityTokenReference(doc);
            reference.setKeyIdentifier(cert);

            sig.getKeyInfo().addUnknownElement(reference.getElement());
            sig.sign(privateKey);

            StringWriter os = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();
            trans.transform(new DOMSource(doc), new StreamResult(os));
            os.close();
            result = os.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiErrorException(e.getMessage());
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw e;
        }

        JSONObject resp = new JSONObject();
        resp.put("xml", result);
        resp.put("raw", rawDocument);

        // Create TSP Sign
        if ((Boolean) args.get(2).get()) {
            String useTsaPolicy = (String) args.get(3).get();
            String tspHashAlgorithm = (String) args.get(4).get();

            try {
                CMSSignedData tsp = man.tsp.createTSP(rawDocument.getBytes(), tspHashAlgorithm, useTsaPolicy).toCMSSignedData();

                // encode tsp to Base64
                String tspBase64 = new String(Base64.getEncoder().encode(tsp.getEncoded()));
                resp.put("tsp", tspBase64);

            } catch (Exception e) {
                throw new ApiErrorException(e.getMessage());
            }
        }

        return resp;
    }

    @Override
    public ArrayList<ApiArgument> arguments() {
        ArrayList<ApiArgument> args = new ArrayList<>();
        args.add(new P12ApiArgument(true, ver, man));
        args.add(new XmlArgument(true, ver, man));

        // tsp arguments
        args.add(new CreateTspArgument(false, ver, man));
        args.add(new UseTsaPolicyArgument(false, ver, man));
        args.add(new TspHashAlgorithmArgument(false, ver, man));
        return args;
    }
}
