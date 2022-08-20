package kz.ncanode.unit.wrapper

import kz.ncanode.common.SpecificationWithTestData
import kz.ncanode.wrapper.KalkanWrapper
import kz.ncanode.wrapper.KeyStoreWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Unroll

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class KeyStoreWrapperTest extends SpecificationWithTestData {

    @Autowired
    private KalkanWrapper kalkanWrapper

    @Unroll("#caseName")
    def "test valid getPrivateKey"() {
        given:
        def signers = [createKeyStore2015(), createKeyStore2004Sign()]

        when:
        def pkey = signers[signerId].getPrivateKey()

        then:
        noExceptionThrown()
        pkey != null

        where:
        caseName            | signerId
        'test for 2015 key' | 0
        'test for 2004 key' | 1
    }

    @Unroll("#caseName")
    def "test valid getCertificate"() {
        given:
        def signers = [createKeyStore2015(), createKeyStore2004Sign()]

        when:
        def cert = signers[signerId].getCertificate()

        then:
        noExceptionThrown()
        cert != null

        where:
        caseName            | signerId
        'test for 2015 key' | 0
        'test for 2004 key' | 1
    }

    private KeyStoreWrapper createKeyStore2015() {
        return kalkanWrapper.read(KEY_INDIVIDUAL_VALID_2015,  KEY_INDIVIDUAL_VALID_2015_ALIAS, KEY_INDIVIDUAL_VALID_2015_PASSWORD)
    }

    private KeyStoreWrapper createKeyStore2004Sign() {
        return kalkanWrapper.read(KEY_INDIVIDUAL_VALID_SIGN_2004, KEY_INDIVIDUAL_VALID_SIGN_2004_ALIAS, KEY_INDIVIDUAL_VALID_SIGN_2004_PASSWORD)
    }
}
