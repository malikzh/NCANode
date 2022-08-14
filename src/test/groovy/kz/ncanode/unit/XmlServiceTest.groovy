package kz.ncanode.unit

import kz.ncanode.exception.ServerException
import kz.ncanode.service.XmlService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.w3c.dom.Document
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class XmlServiceTest extends Specification {

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
        xmlIsValid(parsed)
    }

    def "check parsing invalid xml"() {
        when: 'parse xml from string'
        def parsed = xmlService.read(XML_INVALID_STRING)

        then: 'check for thrown exception'
        def e = thrown(ServerException)
        e.getCause() != null
    }

    private boolean xmlIsValid(Document xml) {
        xml.childNodes.length == 1 && xml.childNodes.item(0).childNodes.item(0).textContent == 'test'
    }
}
