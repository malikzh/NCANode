package kz.ncanode.integration

import com.fasterxml.jackson.databind.ObjectMapper
import kz.ncanode.common.IntegrationSpecification
import kz.ncanode.controller.WsseController
import kz.ncanode.dto.request.WsseSignRequest
import kz.ncanode.dto.request.XmlVerifyRequest
import kz.ncanode.dto.response.VerificationResponse
import kz.ncanode.dto.response.XmlSignResponse
import kz.ncanode.service.WsseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WsseIntegrationTest extends IntegrationSpecification {

    private final static String URI_SIGN   = "/wsse/sign"
    private final static String URI_VERIFY = "/wsse/verify"

    private final static String XML_STRING = """
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
    @Autowired
    WsseService wsseService

    def setup() {
        configureMockMvc(new WsseController(wsseService))
    }


    def "test wsse sign"() {
        given:
        def request = WsseSignRequest.builder()
            .xml(XML_STRING)
            .key(KEY_INDIVIDUAL_VALID_2015)
            .password(KEY_INDIVIDUAL_VALID_2015_PASSWORD)
            .build()

        def requestJson = new ObjectMapper().writeValueAsString(request)

        when:
        def response = doPostQuery(URI_SIGN, requestJson, 200, XmlSignResponse)

        then:
        response != null
        response.xml != null
    }

    def "test wsse verify"() {
        given:
        def request = XmlVerifyRequest.builder()
            .xml(XML_STRING)
            .build()

        def requestJson = new ObjectMapper().writeValueAsString(request)

        when:
        def response = doPostQuery(URI_VERIFY, requestJson, 200, VerificationResponse)

        then:
        response != null
        !response.valid
    }
}
