package kz.ncanode.unit.wrapper

import kz.ncanode.common.WithKeys
import kz.ncanode.common.WithSignerRequests
import kz.ncanode.constants.MessageConstants
import kz.ncanode.exception.KeyException
import kz.ncanode.wrapper.KalkanWrapper
import kz.ncanode.wrapper.KeyStoreWrapper
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import spock.lang.Specification
import spock.lang.Unroll

import static org.mockito.Mockito.doReturn
import static org.mockito.Mockito.mock

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class KalkanWrapperTest extends Specification implements WithSignerRequests, WithKeys {

    @SpyBean
    private KalkanWrapper kalkanWrapper

    @Unroll('#caseName')
    def "read valid key"() {
        when: 'read key'
        def key = kalkanWrapper.read(rawKey, keyAlias, password)

        then: 'check key'
        key != null

        where:
        caseName                  | rawKey                         | password                                | keyAlias
        'read old key'            | KEY_INDIVIDUAL_VALID_SIGN_2004 | KEY_INDIVIDUAL_VALID_SIGN_2004_PASSWORD | null
        'read new key'            | KEY_INDIVIDUAL_VALID_2015      | KEY_INDIVIDUAL_VALID_2015_PASSWORD      | null
        'read old key with alias' | KEY_INDIVIDUAL_VALID_SIGN_2004 | KEY_INDIVIDUAL_VALID_SIGN_2004_PASSWORD | KEY_INDIVIDUAL_VALID_SIGN_2004_ALIAS
        'read new key with alias' | KEY_INDIVIDUAL_VALID_2015      | KEY_INDIVIDUAL_VALID_2015_PASSWORD      | KEY_INDIVIDUAL_VALID_2015_ALIAS
    }

    @Unroll('#caseName')
    def "read invalid key"() {
        when:
        def key = kalkanWrapper.read(rawKey, keyAlias, password)

        then: 'exception must be thrown'
        def error = thrown(KeyException)

        and: 'check exception message'
        error.message == expectedMessage

        where:
        caseName                       | rawKey                    | password                           | keyAlias          || expectedMessage
        'read key with invalid base64' | KEY_INVALID_BASE64        | KEY_INVALID_PASSWORD               | null              || MessageConstants.KEY_INVALID_BASE64
        'read invalid key'             | KEY_INVALID               | KEY_INVALID_PASSWORD               | null              || MessageConstants.KEY_INVALID_FORMAT
        'read with invalid password'   | KEY_INDIVIDUAL_VALID_2015 | KEY_INVALID_PASSWORD               | null              || MessageConstants.KEY_INVALID_PASSWORD
        'read with invalid alias'      | KEY_INDIVIDUAL_VALID_2015 | KEY_INDIVIDUAL_VALID_2015_PASSWORD | KEY_INVALID_ALIAS || KEY_INVALID_ALIAS_MESSAGE
    }

    def "read keys from signer request array"() {
        given:
        def keyStoreWrapper1 = mock(KeyStoreWrapper)
        def keyStoreWrapper2 = mock(KeyStoreWrapper)

        doReturn(keyStoreWrapper1).when(kalkanWrapper).read(SIGNER_REQUEST_1.getKey(), SIGNER_REQUEST_1.getKeyAlias(), SIGNER_REQUEST_1.getPassword())
        doReturn(keyStoreWrapper2).when(kalkanWrapper).read(SIGNER_REQUEST_2.getKey(), SIGNER_REQUEST_2.getKeyAlias(), SIGNER_REQUEST_2.getPassword())

        def signerRequests = [
            SIGNER_REQUEST_1,
            SIGNER_REQUEST_2
        ]

        when: 'read requests'
        def signers = kalkanWrapper.read(signerRequests)

        then: 'check signers'
        signers.size() == 2
        keyStoreWrapper1 != keyStoreWrapper2
        signers[0] == keyStoreWrapper1
        signers[1] == keyStoreWrapper2
    }
}
