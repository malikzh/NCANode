package kz.ncanode.api.version.v10.methods;

import kz.ncanode.api.ApiServiceProvider;
import kz.ncanode.api.core.ApiArgument;
import kz.ncanode.api.core.ApiMethod;
import kz.ncanode.api.core.ApiVersion;
import kz.ncanode.api.exceptions.ApiErrorException;
import kz.ncanode.api.version.v10.arguments.VerifyCrlArgument;
import kz.ncanode.api.version.v10.arguments.VerifyOcspArgument;
import kz.ncanode.api.version.v10.arguments.XmlArgument;
import kz.ncanode.pki.X509Manager;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;

public class XMLVerify extends ApiMethod {
    public XMLVerify(ApiVersion ver, ApiServiceProvider man) {
        super(ver, man);
    }

    @Override
    public JSONObject handle() throws ApiErrorException {
        Document xml = (Document)args.get(0).get();

        Element sigElement = null;
        Element rootEl = (Element) xml.getFirstChild();

        NodeList list = rootEl.getElementsByTagName("ds:Signature");
        int length = list.getLength();

        // read certificate info
        Element certEl = getElementByTagName(xml.getDocumentElement(), "ds:X509Certificate");

        boolean result = false;

        try {
            for (int i = 0; i < length; i++) {
                Node sigNode = list.item(length - 1);
                sigElement = (Element) sigNode;
                if (sigElement == null) {
                    throw new ApiErrorException("Bad signature: Element 'ds:Reference' is not found in XML document");
                }
                XMLSignature signature = new XMLSignature(sigElement, "");
                KeyInfo ki = signature.getKeyInfo();
                X509Certificate cert = ki.getX509Certificate();
                if (cert != null) {
                    result = signature.checkSignatureValue(cert);
                    rootEl.removeChild(sigElement);
                }
            }
        } catch (Exception e) {
            throw new ApiErrorException(e.getMessage());
        }

        JSONObject resp = new JSONObject();


        if (certEl != null) {
            X509Certificate cert;
            try {
                String certBase64 = certEl.getTextContent().replaceAll("\\s", "");
                cert = X509Manager.load(Base64.getDecoder().decode(certBase64));
            } catch (Exception e) {
                throw new ApiErrorException(e.getMessage());
            }


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
                certInf = man.pki.certInfo(cert, ((boolean)args.get(1).get() && issuerCert != null) , ((boolean)args.get(2).get() && issuerCert != null), issuerCert);
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

    private Element getElementByTagName(Element element, String tagName){
        if(tagName.equalsIgnoreCase(element.getTagName()))
            return element;
        for(int i=0; i < element.getChildNodes().getLength(); i++){
            Node node = element.getChildNodes().item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE){
                Element child = getElementByTagName((Element) node, tagName);
                if(child != null)
                    return child;
            }
        }
        return null;
    }
}
