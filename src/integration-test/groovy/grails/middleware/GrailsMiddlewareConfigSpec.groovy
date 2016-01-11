package grails.middleware
import grails.artefact.middleware.Middleware
import grails.core.GrailsApplication
import grails.plugins.GrailsPluginManager
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import spock.lang.Specification

@Integration
@Rollback
class GrailsMiddlewareConfigSpec extends Specification {

    def GrailsApplication grailsApplication
    def GrailsPluginManager grailsPluginManager

    def setup() {
        grailsApplication.config.grails.middleware = [order: [TestMiddleware1.name, TestMiddleware2.name]]
        grailsApplication.rebuild()

    }

    def cleanup() {
    }

    void "Test Middleware Configuration"() {
        given: "GrailsMiddlewareFilter"
        def grailsMiddlewareFilter = grailsApplication.mainContext.getBean("grailsMiddlewareFilter").filter

        expect: "Chain size to be 2"
        grailsMiddlewareFilter.chain.size == 2
    }
}

class TestMiddleware1 implements Middleware {}

class TestMiddleware2 implements Middleware {}
