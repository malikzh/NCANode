package kz.ncanode.unit.service

import groovy.xml.DOMBuilder
import kz.ncanode.common.WithTestData
import kz.ncanode.dto.request.WsseSignRequest
import kz.ncanode.service.WsseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import spock.lang.Unroll

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class WsseServiceTest extends Specification implements WithTestData {
    private final static String XML_VALID_STRING = """
<?xml version="1.0"?>
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
<SOAP-ENV:Header/>
<SOAP-ENV:Body>
    <ns3:SendMessage xmlns:ns3="http://bip.bee.kz/SyncChannel/v10/Types" xmlns="">
        <request>
            <requestInfo>
                <messageId>ccb1010b-8446-4937-913f-c87a717f6809</messageId>
                <serviceId>GBDFL_UniversalServiceSync</serviceId>
                <messageDate>2019-12-25T12:48:21.024Z</messageDate>
                <sender>
                    <senderId>smartbridge</senderId>
                    <password>123</password>
                </sender>
                <properties>
                    <key>CONNECTOR_CLIENT</key>
                    <value>true</value>
                </properties>
            </requestInfo>
            <requestData>
                <ns4:data xmlns:ns4="http://bip.bee.kz/SyncChannel/v10/Interfaces" xmlns:cs="http://message.persistence.interactive.nat" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="cs:Request">
                    <messageId>1</messageId>
                    <messageDate>2017-10-12T15:22:16.183+06:00</messageDate>
                    <senderCode>smartbridge</senderCode>
                    <iin>931002451325</iin>
                </ns4:data>
            </requestData>
        </request>
    </ns3:SendMessage>
</SOAP-ENV:Body>
</SOAP-ENV:Envelope>
"""

    private final static String XML_SIGNED_STRING = """
<?xml version="1.0" encoding="UTF-8" standalone="no"?><SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
<SOAP-ENV:Header><wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" SOAP-ENV:mustUnderstand="1"><ds:Signature xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
<ds:SignedInfo>
<ds:CanonicalizationMethod Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#"/>
<ds:SignatureMethod Algorithm="http://www.w3.org/2001/04/xmldsig-more#rsa-sha256"/>
<ds:Reference URI="#id-a779e2e7-f14f-4dec-b56e-0ed5af898062">
<ds:Transforms>
<ds:Transform Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#"/>
</ds:Transforms>
<ds:DigestMethod Algorithm="http://www.w3.org/2001/04/xmlenc#sha256"/>
<ds:DigestValue>eGruZ1NpXL6OOqBos+L45meIPEooayEVcNKQ3BxJEG0=</ds:DigestValue>
</ds:Reference>
</ds:SignedInfo>
<ds:SignatureValue>
KYRyZBm9VL8OoqrD3tDsnMYloiw26W+qe+68sddI3Bsuald/Ewurt+GTrEJbLGLI7wxmNTJIZIvd&#13;
MDSts9+ztn5CnVkVJkARhuE75hTFsJJoe7LZM/TMs4iucePmGxTYbaLCtbpV8mkXMxNI6zb0DgD/&#13;
r+oFz+gx2bN6UxayKv1ChkK1R7feCT3SKOma+NMaTLjQEq5KM4ON+Vd1cBjhcMLfdxACG+9UBxIZ&#13;
/JwlRGFGtx/fP0B4ie7yjqizNNW3eBrm+3luVNs91wmEaEadkw85HKaNEFpSwU1iaAMDtfqgA0xP&#13;
UXLQe0C3QW9y7oHf6LV3mHSGovNXNqEU47PTFw==
</ds:SignatureValue>
<ds:KeyInfo>
<wsse:SecurityTokenReference><wsse:KeyIdentifier EncodingType="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary" ValueType="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3">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</wsse:KeyIdentifier></wsse:SecurityTokenReference>
</ds:KeyInfo>
</ds:Signature></wsse:Security></SOAP-ENV:Header>
<SOAP-ENV:Body xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" wsu:Id="id-a779e2e7-f14f-4dec-b56e-0ed5af898062">
    <ns3:SendMessage xmlns:ns3="http://bip.bee.kz/SyncChannel/v10/Types">
        <request>
            <requestInfo>
                <messageId>ccb1010b-8446-4937-913f-c87a717f6809</messageId>
                <serviceId>GBDFL_UniversalServiceSync</serviceId>
                <messageDate>2019-12-25T12:48:21.024Z</messageDate>
                <sender>
                    <senderId>smartbridge</senderId>
                    <password>123</password>
                </sender>
                <properties>
                    <key>CONNECTOR_CLIENT</key>
                    <value>true</value>
                </properties>
            </requestInfo>
            <requestData>
                <ns4:data xmlns:cs="http://message.persistence.interactive.nat" xmlns:ns4="http://bip.bee.kz/SyncChannel/v10/Interfaces" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="cs:Request">
                    <messageId>1</messageId>
                    <messageDate>2017-10-12T15:22:16.183+06:00</messageDate>
                    <senderCode>smartbridge</senderCode>
                    <iin>931002451325</iin>
                </ns4:data>
            </requestData>
        </request>
    </ns3:SendMessage>
</SOAP-ENV:Body>
</SOAP-ENV:Envelope>
"""

    @Autowired
    WsseService wsseService

    @Unroll('#caseName')
    def "test sign method"() {
        given:
        def wsseSignRequest = new WsseSignRequest()
        wsseSignRequest.setXml(xml)
        wsseSignRequest.setP12(key)
        wsseSignRequest.setPassword(password)

        when:
        def response = wsseService.sign(wsseSignRequest)
        def signed = DOMBuilder.parse(new StringReader(response.xml))
        def signatureValues = signed.getElementsByTagName('ds:SignatureValue')

        then:
        response != null
        signed != null

        and:
        signatureValues.length > 0
        !signatureValues.item(0).textContent.isEmpty()


        where:
        caseName         | xml              | key                            | password
        'sign gost 2004' | XML_VALID_STRING | KEY_INDIVIDUAL_VALID_SIGN_2004 | KEY_INDIVIDUAL_VALID_SIGN_2004_PASSWORD
        'sign gost 2015' | XML_VALID_STRING | KEY_INDIVIDUAL_VALID_2015      | KEY_INDIVIDUAL_VALID_2015_PASSWORD
    }
}
