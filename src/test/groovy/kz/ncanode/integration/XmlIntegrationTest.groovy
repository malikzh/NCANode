package kz.ncanode.integration

import com.fasterxml.jackson.databind.ObjectMapper
import kz.ncanode.common.IntegrationSpecification
import kz.ncanode.controller.XmlController
import kz.ncanode.dto.request.SignerRequest
import kz.ncanode.dto.request.XmlSignRequest
import kz.ncanode.dto.request.XmlVerifyRequest
import kz.ncanode.dto.response.VerificationResponse
import kz.ncanode.dto.response.XmlSignResponse
import kz.ncanode.service.XmlService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class XmlIntegrationTest extends IntegrationSpecification {
    private final static String XML_STRING = '<?xml version="1.0" encoding="utf-8"?><a><b>test</b></a>'

    // URI
    private final static String URI_SIGN   = "/xml/sign"
    private final static String URI_VERIFY = "/xml/verify"

    @Autowired
    XmlService xmlService

    def setup() {
        configureMockMvc(new XmlController(xmlService))
    }

    def "test xml sign"() {
        given:
        def request = XmlSignRequest.builder()
        .xml(XML_STRING)
        .signers([
            SignerRequest.builder()
                .key(KEY_INDIVIDUAL_VALID_SIGN_2004)
                .password(KEY_INDIVIDUAL_VALID_SIGN_2004_PASSWORD)
                .build()
        ])
        .build()

        def requestJson = new ObjectMapper().writeValueAsString(request)

        when:
        def response = doPostQuery(URI_SIGN, requestJson, 200, XmlSignResponse)

        then:
        response != null
        response.xml != null
    }

    def "test xml verify"() {
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
