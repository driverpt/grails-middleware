package grails.middleware

import grails.artefact.middleware.Middleware
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.WebUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.filter.OncePerRequestFilter

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by lduarte on 10/01/16.
 */
@Slf4j
class GrailsMiddlewareFilter extends OncePerRequestFilter {

    private static final Log LOG = LogFactory.getLog(Interceptor)

    protected List<Middleware> chain = []
    protected List<Middleware> chainReversed = []

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, java.io.IOException {
        def GrailsWebRequest grailsWebRequest = WebUtils.retrieveGrailsWebRequest()

        try {
            processRequest(grailsWebRequest)
        } catch (Exception exception) {
            processException(grailsWebRequest, exception)
        }

        try {
            filterChain.doFilter(request, response)
        } catch (Exception exception) {
            processException(grailsWebRequest, exception)
        }

        processResponse(grailsWebRequest)
    }

    @Autowired(required = false)
    @CompileDynamic
    void setMiddlewares(List<Middleware> newMiddlewares) {
        chain = newMiddlewares

        // TODO: Improve - Either this or Reverse Iterator
        chainReversed = newMiddlewares.reverse()
    }

    private void processRequest(GrailsWebRequest grailsWebRequest) {
        for (middleware in chain) {
            middleware.processRequest(grailsWebRequest)
        }
    }

    private void processException(GrailsWebRequest grailsWebRequest, Throwable exception) {
        for (middleware in chainReversed) {
            middleware.processException(grailsWebRequest, exception)
        }
    }

    private void processResponse(GrailsWebRequest grailsWebRequest) {
        for (middleware in chainReversed) {
            middleware.processResponse(grailsWebRequest)
        }
    }
}
