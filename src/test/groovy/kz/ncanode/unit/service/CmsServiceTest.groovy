package kz.ncanode.unit.service

import kz.gov.pki.kalkan.jce.provider.cms.CMSSignedData
import kz.ncanode.common.WithTestData
import kz.ncanode.dto.request.CmsCreateRequest
import kz.ncanode.dto.request.SignerRequest
import kz.ncanode.service.CertificateService
import kz.ncanode.service.CmsService
import kz.ncanode.wrapper.CertificateWrapper
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.charset.StandardCharsets

import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.anyBoolean
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CmsServiceTest extends Specification implements WithTestData {

    private final static String TEST_DATA = "test123"
    private final static String SIGNED_CMS = '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'
    private final static String SIGNED_DETACHED_CMS = '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'

    @Autowired
    CmsService cmsService

    @MockBean
    CertificateService certificateService

    @Unroll("#caseName")
    def "test cms creating"() {
        given:
        def request = new CmsCreateRequest()
        request.setData(Base64.getEncoder().encodeToString(TEST_DATA.getBytes(StandardCharsets.UTF_8)))
        request.setSigners([
            new SignerRequest(KEY_INDIVIDUAL_VALID_SIGN_2004, KEY_INDIVIDUAL_VALID_SIGN_2004_PASSWORD, null, null),
            new SignerRequest(KEY_INDIVIDUAL_VALID_2015, KEY_INDIVIDUAL_VALID_2015_PASSWORD, null, null),
        ])
        request.setDetached(detached)

        when:
        def response = cmsService.create(request)
        def cms = new CMSSignedData(Base64.getDecoder().decode(response.cms))

        then:
        response != null
        cms != null

        and: 'check content in cms'
        checkOriginalData(cms, detached)

        and: 'check signers quantity'
        def it = cms.getSignerInfos().getSigners().iterator()
        def signersQuantity = 0
        while (it.hasNext()) {
            signersQuantity++;
            it.next()
        }

        signersQuantity == request.getSigners().size()

        where:
        caseName   | detached
        'attached' | false
        'detached' | true
    }

    @Unroll("#caseName")
    def "test cms verification"() {
        given:
        def validDate = buildValidDate()
        def issuerCert = mockIssuerCertificate(dateValid)

        when(certificateService.getCurrentDate()).thenReturn(validDate)
        when(certificateService.attachValidationData(any(), anyBoolean(), anyBoolean())).thenAnswer(new CertificateServiceAnswer(issuerCert))


        when:
        def response = cmsService.verify(cmsData, detachedData, false, false)

        then:
        response != null

        and:
        response.valid == expectedValid
        response.signers.size() == 2

        where:
        caseName   | cmsData             | detachedData                                                              | dateValid || expectedValid
        'attached' | SIGNED_CMS          | null                                                                      | true      || true
        'detached' | SIGNED_DETACHED_CMS | Base64.encoder.encodeToString(TEST_DATA.getBytes(StandardCharsets.UTF_8)) | true      || true
    }

    private boolean checkOriginalData(CMSSignedData cms, boolean detached) {
        if (!detached) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            cms.getSignedContent().write(out);

            def originalData = out.toString(StandardCharsets.UTF_8)
            out.close()

            return originalData == TEST_DATA
        } else {
            return cms.getSignedContent() == null
        }
    }

    private Date buildValidDate() {
        def date = mock(Date)
        when(date.after(any())).thenReturn(true)
        when(date.before(any())).thenReturn(true)
        return date
    }

    private CertificateWrapper mockIssuerCertificate(boolean dateValid) {
        def cert = mock(CertificateWrapper)
        when(cert.isDateValid(any())).thenReturn(dateValid)

        return cert
    }

    protected class CertificateServiceAnswer implements Answer<Void> {

        private CertificateWrapper issuerCert

        public CertificateServiceAnswer(CertificateWrapper issuerCert) {
            this.issuerCert = issuerCert
        }

        @Override
        Void answer(InvocationOnMock invocation) throws Throwable {
            CertificateWrapper cert = invocation.getArgument(0, CertificateWrapper)
            cert.setIssuerCertificate(issuerCert)
            return null
        }
    }
}
