package kz.ncanode.api.version.v10.arguments;

import kz.ncanode.api.ApiServiceProvider;
import kz.ncanode.api.core.ApiArgument;
import kz.ncanode.api.core.ApiVersion;
import kz.ncanode.api.exceptions.InvalidArgumentException;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class XmlArgument extends ApiArgument {

    ApiVersion ver;
    ApiServiceProvider man;
    private boolean required = false;
    private Document xml = null;

    public XmlArgument(boolean required, ApiVersion ver, ApiServiceProvider man) {
        this.required = required;
        this.ver = ver;
        this.man = man;
    }

    @Override
    public void validate() throws InvalidArgumentException {

        String xmlstr;

        try {
            xmlstr = (String) params.get("xml");
        } catch (ClassCastException e) {
            throw new InvalidArgumentException(e.getMessage());
        }

        if (xmlstr == null || xmlstr.isEmpty()) {
            throw new InvalidArgumentException("attribute required");
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);

        try {
            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
            ByteArrayInputStream xmlStream = new ByteArrayInputStream(xmlstr.getBytes(StandardCharsets.UTF_8));
            xml = documentBuilder.parse(xmlStream);
            xmlStream.close();
        } catch (Exception e) {
            throw new InvalidArgumentException(e.getMessage());
        }
    }

    @Override
    public Object get() {
        return xml;
    }

    @Override
    public String name() {
        return "xml";
    }
}
