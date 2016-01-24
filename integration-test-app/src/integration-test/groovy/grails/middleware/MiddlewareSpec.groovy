package grails.middleware
import grails.artefact.middleware.Middleware
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.util.GrailsWebMockUtil
import org.springframework.mock.web.MockFilterChain
import spock.lang.Specification

@Integration
@Rollback
public class MiddlewareSpec extends Specification {

    def grailsApplication

    def middleware = Mock(Middleware)

    def middlewareFilter
    def request

    def exception = new RuntimeException()

    def setup() {
        middlewareFilter = grailsApplication.mainContext.grailsMiddlewareFilter
        middlewareFilter.filter.middlewares = [middleware]

        request = GrailsWebMockUtil.bindMockWebRequest()
    }

    def cleanup() {

    }

    def "Middleware normal behaviour"() {
        when:
        middlewareFilter.filter.doFilter(request.request, request.response, new MockFilterChain())

        then:
        1 * middleware.processRequest(request)
        1 * middleware.processResponse(request)
    }

    def "Exception thrown"() {
        given:
        middleware.processRequest(_) >> { grailsWebRequest -> throw new RuntimeException() }

        when:
        middlewareFilter.filter.doFilter(request.request, request.response, new MockFilterChain())

        then:
        1 * middleware.processRequest(request) >> { throw exception }
        1 * middleware.processException(request, exception)
        0 * middleware.processResponse(request)
    }
}