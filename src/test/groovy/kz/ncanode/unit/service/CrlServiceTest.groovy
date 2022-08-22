package kz.ncanode.unit.service

import kz.ncanode.common.WithTestData
import kz.ncanode.dto.crl.CrlResult
import kz.ncanode.service.CrlService
import kz.ncanode.util.Util
import kz.ncanode.wrapper.KalkanWrapper
import org.apache.http.impl.client.CloseableHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.util.ResourceUtils
import spock.lang.Specification
import spock.lang.Unroll

import java.security.cert.CertificateFactory
import java.security.cert.X509CRL

import static org.mockito.ArgumentMatchers.isNotNull
import static org.mockito.Mockito.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CrlServiceTest extends Specification implements WithTestData {

    @Autowired
    KalkanWrapper kalkanWrapper

    @MockBean
    CloseableHttpClient httpClient

    @SpyBean
    CrlService crlService

    def CRL_GOST = (X509CRL)CertificateFactory.getInstance("X.509").generateCRL(new FileInputStream(ResourceUtils.getFile("classpath:crl/nca_gost_test.crl")))
    def CRL_GOST_2015 = (X509CRL)CertificateFactory.getInstance("X.509").generateCRL(new FileInputStream(ResourceUtils.getFile("classpath:crl/nca_gost2022_test.crl")))
    def CRL_RSA = (X509CRL)CertificateFactory.getInstance("X.509").generateCRL(new FileInputStream(ResourceUtils.getFile("classpath:crl/nca_rsa_test.crl")))

    def CRLS = [
        "nca_gost_test.crl": CRL_GOST,
        "nca_rsa_test.crl": CRL_RSA,
        "http://test.pki.gov.kz/crl/nca_gost2022_test.crl": CRL_GOST_2015,
    ]

    @Unroll("#caseName")
    def "check certificate 2004 verification in CRL"() {
        given: 'load cert and crls'
        doReturn(CRLS).when(crlService).getLoadedCrlEntries()
        doNothing().when(crlService).downloadCrl(isNotNull())

        def key = kalkanWrapper.read(keyStr, null, KEY_INDIVIDUAL_VALID_SIGN_2004_PASSWORD)

        when: 'verify cert'
        def status = crlService.verify(key.getCertificate())

        then: 'check for null and exceptions'
        noExceptionThrown()
        status != null

        and: 'check crl result'
        status.getResult() == expectedStatus

        and: 'check crl downloading'
        verify(crlService, atLeast(1)).downloadCrl(isNotNull())

        where:
        caseName                          | keyStr                           || expectedStatus
        'check revoked auth 2004 key'     | KEY_INDIVIDUAL_AUTH_REVOKED_2004 || CrlResult.REVOKED
        'check revoked sign 2004 key'     | KEY_INDIVIDUAL_SIGN_REVOKED_2004 || CrlResult.REVOKED
        'check revoked ceo sign 2004 key' | KEY_CEO_SIGN_REVOKED_2004        || CrlResult.REVOKED
        'check active sign key'           | KEY_INDIVIDUAL_VALID_SIGN_2004   || CrlResult.ACTIVE
    }

    def "check certificate 2015 verification in CRL"() {
        given:
        Map<String, X509CRL> downloadedCrls = new HashMap<>()
        doReturn(downloadedCrls).when(crlService).getLoadedCrlEntries()

        doAnswer(inv -> {
            URL url = inv.getArgument(0, URL.class)
            downloadedCrls.put(Util.sha1(url.toString()), CRLS.get(url.toString()))
        }).when(crlService).downloadCrl(isNotNull())

        def key = kalkanWrapper.read(keyStr, null, KEY_INDIVIDUAL_VALID_2015_PASSWORD)

        when:
        def status = crlService.verify(key.getCertificate())

        then:
        noExceptionThrown()
        status != null

        and: 'check crl result'
        status.getResult() == expectedStatus

        where:
        caseName                 | keyStr                      || expectedStatus
        'check revoked 2015 key' | KEY_CEO_REVOKED_2015        || CrlResult.REVOKED
        'check active 2015 key'  | KEY_INDIVIDUAL_VALID_2015   || CrlResult.ACTIVE
    }
}
