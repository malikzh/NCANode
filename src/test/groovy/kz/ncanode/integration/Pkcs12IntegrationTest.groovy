package kz.ncanode.integration

import com.fasterxml.jackson.databind.ObjectMapper
import kz.ncanode.common.IntegrationSpecification
import kz.ncanode.controller.Pkcs12Controller
import kz.ncanode.dto.request.Pkcs12InfoRequest
import kz.ncanode.dto.request.SignerRequest
import kz.ncanode.dto.response.VerificationResponse
import kz.ncanode.service.CertificateService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class Pkcs12IntegrationTest extends IntegrationSpecification {

    // URI
    private final static String URI_INFO = "/pkcs12/info"

    @Autowired
    CertificateService certificateService

    def setup() {
        configureMockMvc(new Pkcs12Controller(certificateService))
    }

    def "test pkcs12 info"() {
        given:
        def request = Pkcs12InfoRequest.builder()
            .keys([
                SignerRequest.builder()
                    .key(KEY_INDIVIDUAL_VALID_SIGN_2004)
                    .password(KEY_INDIVIDUAL_VALID_SIGN_2004_PASSWORD)
                    .build()
            ])
            .build()

        def requestJson = new ObjectMapper().writeValueAsString(request)

        when:
        def response = doPostQuery(URI_INFO, requestJson, 200, VerificationResponse)

        then:
        response != null
        !response.valid
    }
}
