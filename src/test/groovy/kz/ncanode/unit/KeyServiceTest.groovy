package kz.ncanode.unit

import kz.ncanode.common.SpecificationWithKeys
import kz.ncanode.constants.MessageConstants
import kz.ncanode.dto.Signer
import kz.ncanode.dto.request.SignerRequest
import kz.ncanode.exception.KeyException
import kz.ncanode.service.KeyService
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import spock.lang.Unroll

import static org.mockito.Mockito.doReturn
import static org.mockito.Mockito.mock

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class KeyServiceTest extends SpecificationWithKeys {
    private final static SignerRequest SIGNER_REQUEST_1 = SignerRequest.builder()
        .key("key1")
        .password("password1")
        .keyAlias("keyAlias1")
        .build()

    private final static SignerRequest SIGNER_REQUEST_2 = SignerRequest.builder()
        .key("key2")
        .password("password2")
        .keyAlias("keyAlias2")
        .build()

    @SpyBean
    private KeyService keyService

    @Unroll('#caseName')
    def "read valid key"() {
        when: 'read key'
        def key = keyService.read(rawKey, password, keyAlias)

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
        def key = keyService.read(rawKey, password, keyAlias)

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
        def signer1 = mock(Signer)
        def signer2 = mock(Signer)

        doReturn(signer1).when(keyService).read(SIGNER_REQUEST_1.getKey(), SIGNER_REQUEST_1.getPassword(), SIGNER_REQUEST_1.getKeyAlias())
        doReturn(signer2).when(keyService).read(SIGNER_REQUEST_2.getKey(), SIGNER_REQUEST_2.getPassword(), SIGNER_REQUEST_2.getKeyAlias())

        def signerRequests = [
            SIGNER_REQUEST_1,
            SIGNER_REQUEST_2
        ]

        when: 'read requests'
        def signers = keyService.read(signerRequests)

        then: 'check signers'
        signers.size() == 2
        signer1 != signer2
        signers[0] == signer1
        signers[1] == signer2
    }
}
