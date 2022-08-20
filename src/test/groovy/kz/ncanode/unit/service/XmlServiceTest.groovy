package kz.ncanode.unit.service

import groovy.xml.DOMBuilder
import kz.ncanode.common.WithTestData
import kz.ncanode.dto.request.XmlSignRequest
import kz.ncanode.exception.ServerException
import kz.ncanode.service.XmlService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.w3c.dom.Document
import spock.lang.Specification
import spock.lang.Unroll

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class XmlServiceTest extends Specification implements WithTestData {

    private final static String XML_VALID_STRING = '<?xml version="1.0" encoding="utf-8"?><a><b>test</b></a>'
    private final static String XML_INVALID_STRING = '<?xml version="1.0" encoding="utf-8"?><a><b>test</b>/a>'

    @Autowired
    private XmlService xmlService

    def "check parsing valid xml"() {
        when: 'parse xml from string'
        def parsed = xmlService.read(XML_VALID_STRING)

        then: 'parsed xml not null'
        parsed != null

        and: 'no exception thrown'
        noExceptionThrown()

        and: 'valid content'
        xmlIsValid(parsed.getDocument())
    }

    def "check parsing invalid xml"() {
        when: 'parse xml from string'
        def parsed = xmlService.read(XML_INVALID_STRING)

        then: 'check for thrown exception'
        def e = thrown(ServerException)
        e.getCause() != null
    }

    @Unroll("#caseName")
    def "check xml signing"() {
        given: 'create request'
        def request = XmlSignRequest.builder().xml(XML_VALID_STRING).signers(signers).build()

        when: 'sign'
        def response = xmlService.sign(request)

        then: 'check'
        noExceptionThrown()
        response != null

        and: 'check signatures size'
        def signed = DOMBuilder.parse(new StringReader(response.xml))
        signed.documentElement.getElementsByTagName("ds:Signature").getLength() == expectedSignaturesCount

        where:
        caseName              | signers                                                || expectedSignaturesCount
        'sign with old key'   | [SIGNER_REQUEST_VALID_2004]                            || 1
        'sign with new key'   | [SIGNER_REQUEST_VALID_2015]                            || 1
        'sign with both keys' | [SIGNER_REQUEST_VALID_2004, SIGNER_REQUEST_VALID_2015] || 2
    }

    private boolean xmlIsValid(Document xml) {
        xml.childNodes.length == 1 && xml.childNodes.item(0).childNodes.item(0).textContent == 'test'
    }
}
