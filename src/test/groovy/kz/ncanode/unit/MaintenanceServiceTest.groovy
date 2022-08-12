package kz.ncanode.unit

import kz.ncanode.service.MaintenanceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class MaintenanceServiceTest extends Specification {

    @Autowired
    MaintenanceService infoService;

    def "when getVersion then return a version"() {
        when:
        def version = infoService.getVersion()

        then: 'version must not be a null'
        version != null

        and:
        version == getClass().getPackage().getImplementationVersion()
    }
}
