package kz.ncanode.unit

import kz.ncanode.common.SpecificationWithKeys
import kz.ncanode.constants.MessageConstants
import kz.ncanode.exception.KeyException
import kz.ncanode.service.KeyService
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import spock.lang.Unroll

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class KeyServiceTest extends SpecificationWithKeys {

    @SpyBean
    private KeyService keyService

    @Unroll('#caseName')
    def "read valid key"() {
        when: 'read key'
        def key = keyService.read(rawKey, password)

        then: 'check key'
        key != null

        where:
        caseName       | rawKey                         | password
        'read old key' | KEY_INDIVIDUAL_VALID_SIGN_2004 | KEY_INDIVIDUAL_VALID_SIGN_2004_PASSWORD
        'read new key' | KEY_INDIVIDUAL_VALID_2015      | KEY_INDIVIDUAL_VALID_2015_PASSWORD
    }

    @Unroll('#caseName')
    def "read invalid key"() {
        when:
        def key = keyService.read(rawKey, password)

        then: 'exception must be thrown'
        def error = thrown(KeyException)

        and: 'check exception message'
        error.message == expectedMessage

        where:
        caseName                       | rawKey                    | password              || expectedMessage
        'read key with invalid base64' | KEY_INVALID_BASE64        | KEY_INVALID_PASSWORD  || MessageConstants.KEY_INVALID_BASE64
        'read invalid key'             | KEY_INVALID               | KEY_INVALID_PASSWORD  || MessageConstants.KEY_INVALID_FORMAT
        'read with invalid password'   | KEY_INDIVIDUAL_VALID_2015 | KEY_INVALID_PASSWORD  || MessageConstants.KEY_INVALID_PASSWORD
    }
}
