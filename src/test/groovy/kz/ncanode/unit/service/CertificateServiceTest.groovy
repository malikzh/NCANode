package kz.ncanode.unit.service

import kz.ncanode.common.WithTestData
import kz.ncanode.constants.MessageConstants
import kz.ncanode.exception.ClientException
import kz.ncanode.service.CertificateService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CertificateServiceTest extends Specification implements WithTestData {

    @Autowired
    CertificateService certificateService

    def "check certificate info method"() {
        when:
        def result = certificateService.info(CERT_INDIVIDUAL, false, false)

        then:
        result != null
        result.signers.size() == 1
    }

    def "invalid cetificate info method"() {
        when:
        certificateService.info("YXNkYXNk", false, false)

        then:
        def e = thrown(ClientException)
        e.getMessage() == MessageConstants.CERT_INVALID
    }
}
