package kz.ncanode.unit.service

import groovy.xml.DOMBuilder
import kz.ncanode.common.WithTestData
import kz.ncanode.dto.request.XmlSignRequest
import kz.ncanode.exception.ServerException
import kz.ncanode.service.XmlService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.w3c.dom.Document
import spock.lang.Specification
import spock.lang.Unroll

import static kz.ncanode.common.SignerRequestTestData.SIGNER_REQUEST_VALID_2004
import static kz.ncanode.common.SignerRequestTestData.SIGNER_REQUEST_VALID_2004_WITH_REFERENCE
import static kz.ncanode.common.SignerRequestTestData.SIGNER_REQUEST_VALID_2015
import static kz.ncanode.common.SignerRequestTestData.SIGNER_REQUEST_VALID_2015_WITH_REFERENCE

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class XmlServiceTest extends Specification implements WithTestData {

    private final static String XML_VALID_STRING = '<?xml version="1.0" encoding="utf-8"?><a><b>test</b></a>'
    private final static Closure<String> XML_VALID_STRING_WITH_REFERENCE = () -> "<?xml version=\"1.0\" encoding=\"utf-8\"?><!DOCTYPE a [\n" +
        "<!ATTLIST a id ID #IMPLIED> ]><a id=\"${REFERENCE_URI}\"><b>test</b></a>"
    private final static String XML_SIGNED_VALID_STRING = """
<?xml version="1.0" encoding="utf-8" standalone="no"?><a><b>test</b><ds:Signature xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
<ds:SignedInfo>
<ds:CanonicalizationMethod Algorithm="http://www.w3.org/TR/2001/REC-xml-c14n-20010315"/>
<ds:SignatureMethod Algorithm="http://www.w3.org/2001/04/xmldsig-more#rsa-sha256"/>
<ds:Reference URI="">
<ds:Transforms>
<ds:Transform Algorithm="http://www.w3.org/2000/09/xmldsig#enveloped-signature"/>
<ds:Transform Algorithm="http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments"/>
</ds:Transforms>
<ds:DigestMethod Algorithm="http://www.w3.org/2001/04/xmlenc#sha256"/>
<ds:DigestValue>9KpJreFMA6K98mcu2hxdAe/vPzfl0uybYurT5DT/bTU=</ds:DigestValue>
</ds:Reference>
</ds:SignedInfo>
<ds:SignatureValue>
aAeSW+p2AfXts7PfkFPx77L5vUOs3pVW7wlGOfGayC+x0IIpwy7/O9ge4uAlgSCo2Z0IqBY9z+0q&#13;
PbX6EoaF59aE2uHkgHBuKLc4XqA4eOJERESgDcoqXp1DpjvENLGyiCbqGDumf8P5XUfUm5d7HGJy&#13;
EV8qEMLw2mIQ1alaPTy938cRQQO7fw8H2CsHrAvbtt/PabpduFfKACqbKukXDk+cR/b+XoouH3aP&#13;
aQzBvbuVekIp1OxiGHV0+2FphlGfqSLBzO66EA9zL+XQ+dxfF8cz46nNvnK4MoUtsQ/EXBjmLd7s&#13;
5Jh28f9LqZBE1iZVFyYJAaGk6CgIwgE+63GOog==
</ds:SignatureValue>
<ds:KeyInfo>
<ds:X509Data>
<ds:X509Certificate>
MIIF+zCCA+OgAwIBAgIUQnk5ChQfU1OmKUe2HtZegXQUZ9EwDQYJKoZIhvcNAQELBQAwLTELMAkG&#13;
A1UEBhMCS1oxHjAcBgNVBAMMFdKw0JrQniAzLjAgKFJTQSBURVNUKTAeFw0yMTAxMTgxMzA5MTRa&#13;
Fw0yMjAxMTgxMzA5MTRaMHkxHjAcBgNVBAMMFdCi0JXQodCi0J7QkiDQotCV0KHQojEVMBMGA1UE&#13;
BAwM0KLQldCh0KLQntCSMRgwFgYDVQQFEw9JSU4xMjM0NTY3ODkwMTExCzAJBgNVBAYTAktaMRkw&#13;
FwYDVQQqDBDQotCV0KHQotCe0JLQmNCnMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA&#13;
maK+pHxaV71IlHAB023ZWaN8VSNkAAjmb9t3nci/ewuDlhblLGa/707c9yYrien3Oco4jlZ70Obz&#13;
HtDVzM3I3jFFF7crEPETLYuOgjQbJcH9B51DvzarYeqXpoU8t1gUE9BabPnsq02QUcVbCCnHgvoh&#13;
tpPVXCO86Cb/nOVKaXIl2M4Iyr16gQczCwnz5pyavLYG5cTiDywWB4YRvL0wXpNkURgl8kmUJIW+&#13;
pm1j1fyvY+5N/obZ04DQ6zpyhpf6sxk3/+sqX6eJ11KJ6FNoIlMQasXeBtXtJs8jZY7tmga2jIlg&#13;
PK4lGy4uQVCpIDkzuioxCPaz8ZA47zCSoiI7LwIDAQABo4IBxTCCAcEwDgYDVR0PAQH/BAQDAgbA&#13;
MB0GA1UdJQQWMBQGCCsGAQUFBwMEBggqgw4DAwQBATAfBgNVHSMEGDAWgBSmjBYzfLjoNWcGPl5B&#13;
V1WirzRQaDAdBgNVHQ4EFgQU4/41rdo7RcvqOj8e1I8mPcVcVW4wXgYDVR0gBFcwVTBTBgcqgw4D&#13;
AwIDMEgwIQYIKwYBBQUHAgEWFWh0dHA6Ly9wa2kuZ292Lmt6L2NwczAjBggrBgEFBQcCAjAXDBVo&#13;
dHRwOi8vcGtpLmdvdi5rei9jcHMwPAYDVR0fBDUwMzAxoC+gLYYraHR0cDovL3Rlc3QucGtpLmdv&#13;
di5rei9jcmwvbmNhX3JzYV90ZXN0LmNybDA+BgNVHS4ENzA1MDOgMaAvhi1odHRwOi8vdGVzdC5w&#13;
a2kuZ292Lmt6L2NybC9uY2FfZF9yc2FfdGVzdC5jcmwwcgYIKwYBBQUHAQEEZjBkMDgGCCsGAQUF&#13;
BzAChixodHRwOi8vdGVzdC5wa2kuZ292Lmt6L2NlcnQvbmNhX3JzYV90ZXN0LmNlcjAoBggrBgEF&#13;
BQcwAYYcaHR0cDovL3Rlc3QucGtpLmdvdi5rei9vY3NwLzANBgkqhkiG9w0BAQsFAAOCAgEAS9tP&#13;
6okU2l3cpZ73t06DZn/jHI+d9cJOk7xFDEjsX1rRe2HJKsDloOY066sOKou75Zk0/qF0ukPyZuPX&#13;
lX8vzsiPlclelkXpQTEvYV8vTnwLhhJJSFt9dNFWT3YTQDO2fpx1K+Jxu06zHQsB7UsCyFm5efKq&#13;
J6RwBw+a+SH9T8DrwuPuuVQvt8PKT/9hbK46GsLI4X46KmXSO2JmYVgcKWj5U3rmF3ZAdaMN32qK&#13;
a96uR2VYPOn75nWvTg+hDaf3RDjxufLTtfXrW857i7ngeUER8mlaINV4k09yAhFlSBtUWcDEpGKh&#13;
kxaJuDBXPYKhkWPuk3I0HOe6XLT5bo9W+hNvKKxjHkEGdKdk0MlKQyiGHSirmTsRK58ifkFMO3kS&#13;
lN1ZvM8oLLtGwkuJgPMgG5o2UX1Gg5Q/dZjwZIQ3buV1CZ5DfceH3kgLPCTCG/ae6S/EY2I1cPhr&#13;
R8jSdCitzK2huq9LYDgwZipAhLnmnIMgYY9ouCELmaG/2n1ZsHKq6s5Aylssrfkb9yowY9G+EFVZ&#13;
4dVEXO2XSFlGT2RpvutiLrLqK6iajz9iYQETd9mLKCo0lcvu7RMqfKqR0v6emTyjt2joM5Grw45e&#13;
cfruSnHhKSYvgmTKB4KqIxdUA+U763u5x37NXK2NR1cJHLaqTfqcDPMMuTK2Fjz7MIs27zQ=
</ds:X509Certificate>
</ds:X509Data>
</ds:KeyInfo>
</ds:Signature></a>
"""
    private final static String XML_INVALID_STRING = '<?xml version="1.0" encoding="utf-8"?><a><b>test</b>/a>'

    @Autowired
    private XmlService xmlService

    def "check parsing valid xml"() {
        when: 'parse xml from string'
        def parsed = xmlService.read(XML_VALID_STRING, false)

        then: 'parsed xml not null'
        parsed != null

        and: 'no exception thrown'
        noExceptionThrown()

        and: 'valid content'
        xmlIsValid(parsed.getDocument())
    }

    def "check parsing invalid xml"() {
        when: 'parse xml from string'
        xmlService.read(XML_INVALID_STRING, false)

        then: 'check for thrown exception'
        def e = thrown(ServerException)
        e.getCause() != null
    }

    @Unroll("#caseName")
    def "check xml signing"() {
        given: 'create request'
        def request = XmlSignRequest.builder().xml(XML_VALID_STRING).signers(signers).build()

        when: 'sign'
        def response = xmlService.sign(request)

        then: 'check'
        noExceptionThrown()
        response != null

        and: 'check signatures size'
        def signed = DOMBuilder.parse(new StringReader(response.xml))
        signed.documentElement.getElementsByTagName("ds:Signature").getLength() == expectedSignaturesCount

        where:
        caseName              | signers                                                || expectedSignaturesCount
        'sign with old key'   | [SIGNER_REQUEST_VALID_2004()]                            || 1
        'sign with new key'   | [SIGNER_REQUEST_VALID_2015()]                            || 1
        'sign with both keys' | [SIGNER_REQUEST_VALID_2004(), SIGNER_REQUEST_VALID_2015()] || 2
    }

    @Unroll("#caseName")
    def "check xml signature appending"() {
        given: 'create request'
        def request = XmlSignRequest.builder().xml(XML_SIGNED_VALID_STRING).signers([SIGNER_REQUEST_VALID_2015()]).clearSignatures(clearSignatures).build()

        when: 'sign'
        def response = xmlService.sign(request)

        then: 'check'
        noExceptionThrown()
        response != null

        and: 'check signatures size'
        def signed = DOMBuilder.parse(new StringReader(response.xml))
        signed.documentElement.getElementsByTagName("ds:Signature").getLength() == expectedSignaturesCount

        where:
        caseName                   | clearSignatures || expectedSignaturesCount
        'without clear signatures' | false           || 2
        'with clear signatures'    | true            || 1
    }

    def "check for reference uri"() {
        given: 'create request'
        def request = XmlSignRequest.builder().xml(XML_VALID_STRING_WITH_REFERENCE()).signers([SIGNER_REQUEST_VALID_2004_WITH_REFERENCE(), SIGNER_REQUEST_VALID_2015_WITH_REFERENCE()]).build()

        when: 'sign'
        def response = xmlService.sign(request)

        then: 'check for null'
        noExceptionThrown()
        response != null

        and: 'check reference uri'
        def signed = DOMBuilder.parse(new StringReader(response.xml))
        def references = signed.documentElement.getElementsByTagName('ds:Reference')

        references.item(0).attributes.getNamedItem("URI").textContent == ('#' + REFERENCE_URI)
        references.item(1).attributes.getNamedItem("URI").textContent == ('#' + REFERENCE_URI)
    }

    private boolean xmlIsValid(Document xml) {
        xml.childNodes.length == 1 && xml.childNodes.item(0).childNodes.item(0).textContent == 'test'
    }
}
