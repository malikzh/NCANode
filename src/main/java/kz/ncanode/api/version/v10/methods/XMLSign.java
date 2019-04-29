package kz.ncanode.api.version.v10.methods;

import kz.gov.pki.kalkan.jce.provider.cms.CMSSignedData;
import kz.ncanode.Helper;
import kz.ncanode.api.ApiServiceProvider;
import kz.ncanode.api.core.ApiArgument;
import kz.ncanode.api.core.ApiMethod;
import kz.ncanode.api.core.ApiVersion;
import kz.ncanode.api.exceptions.ApiErrorException;
import kz.ncanode.api.version.v10.arguments.*;
import org.apache.xml.security.encryption.XMLCipherParameters;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;

public class XMLSign extends ApiMethod {
    public XMLSign(ApiVersion ver, ApiServiceProvider man) {
        super(ver, man);
    }

    @Override
    public JSONObject handle() throws ApiErrorException {
        KeyStore p12 = (KeyStore)args.get(0).get();
        Document xml = (Document)args.get(1).get();

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

        String[] alg = Helper.getSignMethodByOID(cert.getSigAlgOID());

        // signing
        XMLSignature sig;
        try {
            sig = new XMLSignature(xml, "", alg[0]);
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
                sig.addDocument("", transforms, alg[1]);
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
        resp.put("raw", rawDocument);

        // Create TSP Sign
        if ((Boolean)args.get(2).get()) {
            String useTsaPolicy     = (String)args.get(3).get();
            String tspHashAlgorithm = (String)args.get(4).get();

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
