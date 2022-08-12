package kz.ncanode.unit

import kz.ncanode.service.InfoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class InfoServiceTest extends Specification {

    @Autowired
    InfoService infoService;

    def "when getVersion then return a version"() {
        when:
        def version = infoService.getVersion()

        then:
        version == getClass().getPackage().getImplementationVersion()
    }
}
