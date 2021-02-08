package kz.ncanode.api.version.v10.methods;

import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPMessage;
import kz.ncanode.api.ApiServiceProvider;
import kz.ncanode.api.core.ApiArgument;
import kz.ncanode.api.core.ApiMethod;
import kz.ncanode.api.core.ApiVersion;
import kz.ncanode.api.exceptions.ApiErrorException;
import kz.ncanode.api.version.v10.arguments.VerifyCrlArgument;
import kz.ncanode.api.version.v10.arguments.VerifyOcspArgument;
import kz.ncanode.api.version.v10.arguments.XmlArgument;
import org.apache.ws.security.components.crypto.CertificateStore;
import org.apache.ws.security.message.token.SecurityTokenReference;
import org.apache.xml.security.signature.XMLSignature;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

public class XMLVerifyWithSecurityHeader extends ApiMethod {
    public XMLVerifyWithSecurityHeader(ApiVersion ver, ApiServiceProvider man) {
        super(ver, man);
    }

    @Override
    public JSONObject handle() throws ApiErrorException {
        Document xml = (Document) args.get(0).get();

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

        InputStream is = new ByteArrayInputStream(rawDocument.getBytes());

        try {
            SOAPMessage msg = MessageFactory.newInstance().createMessage(null, is);
            SOAPEnvelope env = msg.getSOAPPart().getEnvelope();
            xml = env.getOwnerDocument();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiErrorException(e.getMessage());
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw e;
        }

        Element rootEl = (Element) xml.getFirstChild();
        NodeList list = rootEl.getElementsByTagName("ds:Signature");

        X509Certificate cert;
        boolean result = false;

        try {
            Node sigNode = list.item(0);
            XMLSignature signature = new XMLSignature((Element) sigNode, "");
            NodeList securityRef = rootEl.getElementsByTagName("wsse:SecurityTokenReference");
            SecurityTokenReference ref = new SecurityTokenReference((Element) securityRef.item(0));
            cert = ref.getKeyIdentifier(new CertificateStore(new X509Certificate[]{}))[0];

            if (cert != null) {
                result = signature.checkSignatureValue(cert.getPublicKey());
            }

        } catch (Exception e) {
            throw new ApiErrorException(e.getMessage());
        }

        JSONObject resp = new JSONObject();

        if (cert != null) {
            // Chain information
            ArrayList<X509Certificate> chain = null;
            ArrayList<JSONObject> chainInf = null;
            try {
                chain = man.ca.chain(cert);

                chainInf = new ArrayList<>();

                if (chain != null) {
                    for (X509Certificate chainCert : chain) {
                        JSONObject chi = man.pki.certInfo(chainCert, false, false, null);
                        chainInf.add(chi);
                    }
                }
            } catch (Exception e) {
                throw new ApiErrorException(e.getMessage());
            }

            X509Certificate issuerCert = null;

            if (chain != null && chain.size() > 1) {
                issuerCert = chain.get(1);
            }

            try {
                JSONObject certInf;
                certInf = man.pki.certInfo(cert, ((boolean) args.get(1).get() && issuerCert != null), ((boolean) args.get(2).get() && issuerCert != null), issuerCert);
                certInf.put("chain", chainInf);
                resp.put("cert", certInf);
            } catch (Exception e) {
                throw new ApiErrorException(e.getMessage());
            }
        }

        resp.put("valid", result);

        return resp;
    }

    @Override
    public ArrayList<ApiArgument> arguments() {
        ArrayList<ApiArgument> args = new ArrayList<>();
        args.add(new XmlArgument(true, ver, man));
        args.add(new VerifyOcspArgument(false, ver, man));
        args.add(new VerifyCrlArgument(false, ver, man));
        return args;
    }
}
