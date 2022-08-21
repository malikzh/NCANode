package kz.ncanode.unit.service

import kz.ncanode.common.WithTestData
import kz.ncanode.dto.crl.CrlResult
import kz.ncanode.service.CrlService
import kz.ncanode.wrapper.KalkanWrapper
import org.apache.http.impl.client.CloseableHttpClient
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.util.ResourceUtils
import spock.lang.Specification
import spock.lang.Unroll

import java.security.cert.CertificateFactory
import java.security.cert.X509CRL

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CrlServiceTest extends Specification implements WithTestData {

    @Autowired
    KalkanWrapper kalkanWrapper

    @MockBean
    CloseableHttpClient httpClient

    @SpyBean
    CrlService crlService

    def CRL_GOST = (X509CRL)CertificateFactory.getInstance("X.509").generateCRL(new FileInputStream(ResourceUtils.getFile("classpath:crl/nca_gost_test.crl")))
    def CRL_RSA = (X509CRL)CertificateFactory.getInstance("X.509").generateCRL(new FileInputStream(ResourceUtils.getFile("classpath:crl/nca_rsa_test.crl")))

    def CRLS = [
        "nca_gost_test.crl": CRL_GOST,
        "nca_rsa_test.crl": CRL_RSA,
    ]

    @Unroll("#caseName")
    def "check certificate 2004 verification in CRL"() {
        given: 'load cert and crls'
        Mockito.doReturn(CRLS).when(crlService).getLoadedCrlEntries()

        def key = kalkanWrapper.read(keyStr, null, KEY_INDIVIDUAL_VALID_SIGN_2004_PASSWORD)

        when: 'verify cert'
        def status = crlService.verify(key.getCertificate())

        then: 'check for null and exceptions'
        noExceptionThrown()
        status != null

        and: 'check crl result'
        status.getResult() == expectedStatus

        where:
        caseName                          | keyStr                           || expectedStatus
        'check revoked auth 2004 key'     | KEY_INDIVIDUAL_AUTH_REVOKED_2004 || CrlResult.REVOKED
        'check revoked sign 2004 key'     | KEY_INDIVIDUAL_SIGN_REVOKED_2004 || CrlResult.REVOKED
        'check revoked ceo sign 2004 key' | KEY_CEO_SIGN_REVOKED_2004        || CrlResult.REVOKED
        'check active sign key'           | KEY_INDIVIDUAL_VALID_SIGN_2004   || CrlResult.ACTIVE
    }
}
