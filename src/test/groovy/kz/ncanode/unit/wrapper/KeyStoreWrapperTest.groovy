package kz.ncanode.unit.wrapper

import kz.ncanode.common.WithTestData
import kz.ncanode.constants.MessageConstants
import kz.ncanode.exception.ServerException
import kz.ncanode.wrapper.KalkanWrapper
import kz.ncanode.wrapper.KeyStoreWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import spock.lang.Unroll

import java.security.KeyStore
import java.security.KeyStoreException
import java.security.KeyStoreSpi
import java.security.NoSuchAlgorithmException
import java.security.UnrecoverableKeyException

import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class KeyStoreWrapperTest extends Specification implements WithTestData {

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

    @Unroll("#caseName")
    def "check exception thrown for getPrivateKey"() {
        given:
        def keyStoreSpiMock = mock(KeyStoreSpi.class);
        def keystore = new KeyStore(keyStoreSpiMock, null, "test"){ };
        keystore.load(null)

        doThrow(exception).when(keyStoreSpiMock).engineGetKey(any(), any())

        def keystoreWrapper = new KeyStoreWrapper(keystore, "null", "", [])

        when:
        keystoreWrapper.getPrivateKey()

        then:
        thrown(ServerException)

        where:
        caseName                    | exception
        'NoSuchAlgorithmException'  | new NoSuchAlgorithmException()
        'UnrecoverableKeyException' | new UnrecoverableKeyException()
    }

    def "check exception thrown for getCertificate()"() {
        given:
        def keystore = mock(KeyStore)
        when(keystore.getCertificate(any())).thenThrow(new KeyStoreException())

        def keystoreWrapper = new KeyStoreWrapper(keystore, "null", "", [])

        when:
        keystoreWrapper.getCertificate()

        then:
        def e = thrown(ServerException)
        e.getMessage() == MessageConstants.KEY_CANT_EXTRACT_CERTIFICATE
    }

    private KeyStoreWrapper createKeyStore2015() {
        return kalkanWrapper.read(KEY_INDIVIDUAL_VALID_2015,  KEY_INDIVIDUAL_VALID_2015_ALIAS, KEY_INDIVIDUAL_VALID_2015_PASSWORD)
    }

    private KeyStoreWrapper createKeyStore2004Sign() {
        return kalkanWrapper.read(KEY_INDIVIDUAL_VALID_SIGN_2004, KEY_INDIVIDUAL_VALID_SIGN_2004_ALIAS, KEY_INDIVIDUAL_VALID_SIGN_2004_PASSWORD)
    }
}
