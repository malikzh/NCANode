package kz.ncanode.unit.service

import kz.gov.pki.kalkan.asn1.knca.KNCAObjectIdentifiers
import kz.gov.pki.kalkan.jce.provider.cms.CMSSignedData
import kz.gov.pki.kalkan.tsp.TSPAlgorithms
import kz.ncanode.common.WithTestData
import kz.ncanode.service.TspService
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.impl.client.CloseableHttpClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.util.ResourceUtils
import spock.lang.Specification

import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.doReturn

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class TspServiceTest extends Specification implements WithTestData {
    private static File TSP_SAMPLE_RESPONSE = ResourceUtils.getFile("classpath:tsp/tsp_response.bin")
    private static File TSP_SAMPLE_TOKEN = ResourceUtils.getFile("classpath:tsp/tsp_token.bin")
    private final static BigInteger TSP_SAMPLE_NONCE = new BigInteger("1663507112663")
    private final static byte[] SAMPLE_DATA = [0xCA, 0xFE, 0xBA, 0xBE]

    @MockBean
    CloseableHttpClient client

    @SpyBean
    TspService tspService

    def "create new tsp token"() {
        given:
        doReturn(createMockedResponse(new FileInputStream(TSP_SAMPLE_RESPONSE))).when(client).execute(any(HttpUriRequest))
        doReturn(TSP_SAMPLE_NONCE).when(tspService).generateNonce()

        when:
        def tspToken = tspService.create(SAMPLE_DATA, TSPAlgorithms.SHA1, KNCAObjectIdentifiers.tsa_gost_policy.getId())

        then:
        noExceptionThrown()
        tspToken != null

        and:
        tspToken.getTimeStampInfo().nonce == TSP_SAMPLE_NONCE
    }

    def "verify token"() {
        given:
        def cmsSignedData = new CMSSignedData(new FileInputStream(TSP_SAMPLE_TOKEN))

        when:
        def token = tspService.info(cmsSignedData)

        then:
        noExceptionThrown()
        token.isPresent()

        and:
        token.get().nonce == TSP_SAMPLE_NONCE
    }
}
