package kz.ncanode.integration

import com.fasterxml.jackson.databind.ObjectMapper
import kz.ncanode.common.IntegrationSpecification
import kz.ncanode.controller.CmsController
import kz.ncanode.dto.request.CmsCreateRequest
import kz.ncanode.dto.request.CmsVerifyRequest
import kz.ncanode.dto.request.SignerRequest
import kz.ncanode.dto.response.CmsDataResponse
import kz.ncanode.dto.response.CmsResponse
import kz.ncanode.dto.response.CmsVerificationResponse
import kz.ncanode.service.CmsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

import java.nio.charset.StandardCharsets

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CmsIntegrationTest extends IntegrationSpecification {
    private final static String TEST_DATA = "lol_kek"
    private final static String SIGNED_CMS = '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'

    // URI
    private final static String URI_SIGN = "/cms/sign"
    private final static String URI_SIGN_ADD = "/cms/sign/add"
    private final static String URI_VERIFY = "/cms/verify"
    private final static String URI_EXTRACT = "/cms/extract"

    @Autowired
    CmsService cmsService

    def setup() {
        configureMockMvc(new CmsController(cmsService))
    }

    def "test sign method"() {
        given:
        def request = new CmsCreateRequest()
        request.data = Base64.encoder.encodeToString(TEST_DATA.getBytes(StandardCharsets.UTF_8))
        request.signers = [
            SignerRequest.builder()
                .key(KEY_INDIVIDUAL_VALID_SIGN_2004)
                .password(KEY_INDIVIDUAL_VALID_SIGN_2004_PASSWORD)
                .build()
        ]

        def requestJson = new ObjectMapper().writeValueAsString(request)

        when:
        def response = doPostQuery(URI_SIGN, requestJson, 200, CmsResponse)

        then:
        response != null
        response.cms != null
    }

    def "test sign add method"() {
        given:
        def request = new CmsCreateRequest()
        request.cms = SIGNED_CMS
        request.signers = [
            SignerRequest.builder()
                .key(KEY_INDIVIDUAL_VALID_2015)
                .password(KEY_INDIVIDUAL_VALID_2015_PASSWORD)
                .build()
        ]

        def requestJson = new ObjectMapper().writeValueAsString(request)

        when:
        def response = doPostQuery(URI_SIGN_ADD, requestJson, 200, CmsResponse)

        then:
        response != null
        response.cms != null
    }

    def "test verify"() {
        given:
        def request = CmsVerifyRequest.builder()
            .cms(SIGNED_CMS)
            .build()

        def requestJson = new ObjectMapper().writeValueAsString(request)

        when:
        def response = doPostQuery(URI_VERIFY, requestJson, 200, CmsVerificationResponse)

        then:
        response != null
        response.signers.size() == 1
        !response.valid
    }

    def "test extract"() {
        given:
        def request = CmsVerifyRequest.builder()
            .cms(SIGNED_CMS)
            .build()

        def requestJson = new ObjectMapper().writeValueAsString(request)

        when:
        def response = doPostQuery(URI_EXTRACT, requestJson, 200, CmsDataResponse)

        then:
        response != null
        response.data == Base64.encoder.encodeToString(TEST_DATA.getBytes(StandardCharsets.UTF_8))
    }
}
