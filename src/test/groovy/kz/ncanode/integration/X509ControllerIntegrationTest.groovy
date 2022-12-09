package kz.ncanode.integration

import com.fasterxml.jackson.databind.ObjectMapper
import kz.ncanode.common.IntegrationSpecification
import kz.ncanode.controller.X509Controller
import kz.ncanode.dto.request.X509InfoRequest
import kz.ncanode.dto.response.VerificationResponse
import kz.ncanode.service.CertificateService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class X509ControllerIntegrationTest extends IntegrationSpecification {

    // URI
    private final static String URI_INFO = "/x509/info"

    @Autowired
    CertificateService certificateService

    def setup() {
        configureMockMvc(new X509Controller(certificateService))
    }

    def "test x509 info"() {
        given:
        def request = X509InfoRequest.builder()
            .certs([CERT_INDIVIDUAL])
            .build()

        def requestJson = new ObjectMapper().writeValueAsString(request)

        when:
        def response = doPostQuery(URI_INFO, requestJson, 200, VerificationResponse)

        then:
        response != null
        response.signers.size() == 1
        !response.valid
    }
}
