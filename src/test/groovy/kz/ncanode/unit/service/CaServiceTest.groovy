package kz.ncanode.unit.service

import kz.ncanode.common.WithTestData
import kz.ncanode.service.CaService
import kz.ncanode.wrapper.CertificateWrapper
import kz.ncanode.wrapper.KalkanWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.util.ResourceUtils
import spock.lang.Shared
import spock.lang.Specification

import static org.mockito.Mockito.doReturn

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CaServiceTest extends Specification implements WithTestData {

    // Root certs
    @Shared
    def NCA_GOST = () -> CertificateWrapper.fromFile(ResourceUtils.getFile("classpath:ca/nca_gost_test.crt")).get()

    @Shared
    def NCA_RSA = () -> CertificateWrapper.fromFile(ResourceUtils.getFile("classpath:ca/nca_rsa_test.crt")).get()

    @Shared
    def NCA_GOST2015 = () -> CertificateWrapper.fromFile(ResourceUtils.getFile("classpath:ca/nca_gost2015_test.cer")).get()

    // Users certs
    @Shared
    def INDIVIDUAL_VALID_SIGN_2004_RSA = () -> CertificateWrapper.fromFile(ResourceUtils.getFile("classpath:certs/individual_valid_sign_2004_rsa.cer")).get()

    @Shared
    def INDIVIDUAL_VALID_SIGN_2004_AUTH = () -> CertificateWrapper.fromFile(ResourceUtils.getFile("classpath:certs/individual_valid_sign_2004_rsa_auth.cer")).get()

    @Shared
    def CEO_VALID_SIGN_2004_GOST = () -> CertificateWrapper.fromFile(ResourceUtils.getFile("classpath:certs/ceo_valid_sign_2004_gost.cer")).get()

    @SpyBean
    CaService caService

    @Autowired
    KalkanWrapper kalkanWrapper

    def setupSpec() {
        initializeKalkanLibrary()
    }

    def "check certificate chain validator"() {
        given:
        doReturn(caCerts).when(caService).getRootCertificates()

        when:
        def result = caService.getRootCertificateFor(cert)

        then:
        noExceptionThrown()

        and:
        result.isPresent()

        and: 'validate public keys'
        result.get().publicKey.equals(expected.publicKey)

        where:
        caseName              | caCerts                 | cert                              || expected
        'check rsa 2004'      | [NCA_GOST(), NCA_RSA()] | INDIVIDUAL_VALID_SIGN_2004_RSA()  || NCA_RSA()
        'check rsa auth 2004' | [NCA_GOST(), NCA_RSA()] | INDIVIDUAL_VALID_SIGN_2004_AUTH() || NCA_RSA()
        'check ceo gost 2004' | [NCA_RSA(), NCA_GOST()] | CEO_VALID_SIGN_2004_GOST()        || NCA_GOST()
    }

    def "check ca for gost-2015"() {
        given:
        def rootCert = NCA_GOST2015()
        doReturn([NCA_GOST(), NCA_RSA(), rootCert]).when(caService).getRootCertificates()

        def cert = kalkanWrapper.read(KEY_INDIVIDUAL_VALID_2015, KEY_INDIVIDUAL_VALID_2015_ALIAS, KEY_INDIVIDUAL_VALID_2015_PASSWORD).getCertificate()

        when:
        def result = caService.getRootCertificateFor(cert)

        then:
        noExceptionThrown()
        result.isPresent()

        and:
        result.get() == rootCert
    }
}
