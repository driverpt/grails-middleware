package grails.middleware

import grails.artefact.middleware.Middleware
import grails.util.GrailsWebMockUtil
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.mock.web.MockFilterChain
import org.springframework.web.context.request.RequestContextHolder
import spock.lang.Specification

class GrailsMiddlewareFilterSpec extends Specification {

    def middlewareChain = [new FirstMiddleware(), new SecondMiddleware()]
    def filter = new GrailsMiddlewareFilter()

    void setup() {
        filter.setMiddlewares(middlewareChain)
    }

    void cleanup() {
        RequestContextHolder.setRequestAttributes(null)
    }

    void "Test Middleware works"() {
        when:"The chain is executed"
        def webRequest = GrailsWebMockUtil.bindMockWebRequest()
        filter.doFilter(webRequest.request, webRequest.response, new MockFilterChain())

        then:"Modification is 12345678"
        webRequest.request.getAttribute("Modification") == "12345678"
    }

    void "Test that response is handled properly"() {
        when:"The chain is executed"
        def webRequest = GrailsWebMockUtil.bindMockWebRequest()
        filter.doFilter(webRequest.request, webRequest.response, new MockFilterChain())

        then:"Modification on response is 12345678"
        webRequest.response.getHeader("X-Modification") == "12345678"
    }

    void "Test that Exception is carefully handled"() {
        given:"We add the Exception Middleware"
        middlewareChain.add(new ExceptionMiddleware())

        when:"The chain is executed"
        def webRequest = GrailsWebMockUtil.bindMockWebRequest()
        filter.doFilter(webRequest.request, webRequest.response, new MockFilterChain())

        then:"Modification on response is 12345678"
        webRequest.response.getHeader("X-FirstException") == "RuntimeException"
    }
}

class FirstMiddleware implements Middleware {
    @Override
    void processRequest(GrailsWebRequest grailsWebRequest) {
        grailsWebRequest.request.setAttribute("FirstModification", "1234")
    }

    @Override
    void processResponse(GrailsWebRequest grailsWebRequest) {
        def firstModification = grailsWebRequest.response.getHeader "X-Modification"
        firstModification += "5678"
        grailsWebRequest.response.setHeader "X-Modification", firstModification
    }

    @Override
    void processException(GrailsWebRequest grailsWebRequest, Throwable exception) {
        grailsWebRequest.response.setHeader("X-FirstException", exception.class.simpleName)
    }
}

class SecondMiddleware implements Middleware {
    @Override
    void processRequest(GrailsWebRequest grailsWebRequest) {
        def firstModification = grailsWebRequest.request.getAttribute "FirstModification"
        firstModification += "5678"
        grailsWebRequest.request.setAttribute "Modification", firstModification
    }

    @Override
    void processResponse(GrailsWebRequest grailsWebRequest) {
        grailsWebRequest.response.setHeader("X-Modification", "1234")
    }
}

class ExceptionMiddleware implements Middleware {
    @Override
    void processRequest(GrailsWebRequest grailsWebRequest) {
        throw new RuntimeException()
    }
}