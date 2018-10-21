package kz.ncanode.api.version.v10.methods;

import kz.gov.pki.kalkan.asn1.pkcs.PKCSObjectIdentifiers;
import kz.ncanode.api.ApiServiceProvider;
import kz.ncanode.api.core.ApiArgument;
import kz.ncanode.api.core.ApiMethod;
import kz.ncanode.api.core.ApiVersion;
import kz.ncanode.api.exceptions.ApiErrorException;
import kz.ncanode.api.version.v10.arguments.P12ApiArgument;
import kz.ncanode.api.version.v10.arguments.XmlArgument;
import org.apache.xml.security.encryption.XMLCipherParameters;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;

public class XMLSign extends ApiMethod {
    public XMLSign(ApiVersion ver, ApiServiceProvider man) {
        super(ver, man);
    }

    @Override
    public JSONObject handle() throws ApiErrorException {
        KeyStore p12 = (KeyStore)args.get(0).get();
        Document xml = (Document)args.get(1).get();


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
            String password = ((P12ApiArgument)args.get(0)).getPassword();
            privateKey = (PrivateKey) p12.getKey(alias, password.toCharArray());
        } catch (Exception e) {
            throw new ApiErrorException(e.getMessage());
        }

        String signMethod;
        String digestMethod;

        // get cert
        X509Certificate cert = null;
        try {
            cert = (X509Certificate) p12.getCertificate(alias);
        } catch (KeyStoreException e) {
            throw new ApiErrorException(e.getMessage());
        }
        String sigAlgOid = cert.getSigAlgOID();
        if (sigAlgOid.equals(PKCSObjectIdentifiers.sha1WithRSAEncryption.getId())) {
            signMethod = Constants.MoreAlgorithmsSpecNS + "rsa-sha1";
            digestMethod = Constants.MoreAlgorithmsSpecNS + "sha1";
        } else if (sigAlgOid.equals(PKCSObjectIdentifiers.sha256WithRSAEncryption.getId())) {
            signMethod = Constants.MoreAlgorithmsSpecNS + "rsa-sha256";
            digestMethod = XMLCipherParameters.SHA256;
        } else {
            signMethod = Constants.MoreAlgorithmsSpecNS + "gost34310-gost34311";
            digestMethod = Constants.MoreAlgorithmsSpecNS + "gost34311";
        }

        // signing
        XMLSignature sig;
        try {
            sig = new XMLSignature(xml, "", signMethod);
        } catch (XMLSecurityException e) {
            throw new ApiErrorException(e.getMessage());
        }

        String result = "";

        try {

            if (xml.getFirstChild() != null) {
                xml.getFirstChild().appendChild(sig.getElement());
                Transforms transforms = new Transforms(xml);
                transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
                transforms.addTransform(XMLCipherParameters.N14C_XML_CMMNTS);
                sig.addDocument("", transforms, digestMethod);
                sig.addKeyInfo(cert);
                sig.sign(privateKey);
                StringWriter os = new StringWriter();
                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer trans = tf.newTransformer();
                trans.transform(new DOMSource(xml), new StreamResult(os));
                os.close();
                result = os.toString();
            }
        } catch (Exception e) {
            throw new ApiErrorException(e.getMessage());
        }


        JSONObject resp = new JSONObject();
        resp.put("xml", result);
        return resp;
    }

    @Override
    public ArrayList<ApiArgument> arguments() {
        ArrayList<ApiArgument> args = new ArrayList<>();
        args.add(new P12ApiArgument(true, ver, man));
        args.add(new XmlArgument(true, ver, man));
        return args;
    }
}
