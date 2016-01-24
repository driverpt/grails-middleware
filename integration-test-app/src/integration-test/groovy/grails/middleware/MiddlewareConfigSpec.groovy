package grails.middleware
import grails.artefact.middleware.Middleware
import grails.core.GrailsApplication
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import spock.lang.Specification

@Integration
@Rollback
class MiddlewareConfigSpec extends Specification {

    def GrailsApplication grailsApplication

    def setup() {
        // Check application.yml for config
    }

    def cleanup() {
    }

    void "Test Middleware Configuration"() {
        given: "MiddlewareFilter"
        def grailsMiddlewareFilter = grailsApplication.mainContext.grailsMiddlewareFilter.filter

        expect: "Chain size to be 2"
        grailsMiddlewareFilter.chain.size == 2
    }
}

class TestMiddleware1 implements Middleware {}

class TestMiddleware2 implements Middleware {}
