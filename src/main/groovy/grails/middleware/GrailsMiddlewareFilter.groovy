package grails.middleware

import grails.artefact.middleware.Middleware
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
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
class GrailsMiddlewareFilter extends OncePerRequestFilter {

    private static final Log LOG = LogFactory.getLog(Interceptor)

    protected List<Middleware> chain = []
    protected List<Middleware> chainReversed = []

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, java.io.IOException {
        GrailsWebRequest grailsWebRequest = WebUtils.retrieveGrailsWebRequest()

        Throwable exception = null

        try {
            for (middleware in chain) {
                middleware.processRequest(grailsWebRequest)
            }
        } catch(Exception e) {
            exception = e
        }

        if(!exception) {
            try {
                filterChain.doFilter(request, response)
            } catch (Exception e) {
                exception = e
            }
        }

        if(exception) {
            processException(grailsWebRequest, exception)
        } else {
            processResponse(grailsWebRequest)
        }
    }

    @Autowired(required = false)
    @CompileDynamic
    void setMiddlewares(List<Middleware> newMiddlewares) {
        chain = newMiddlewares

        // TODO: Improve - Either this or Reverse Iterator
        chainReversed = newMiddlewares.reverse()
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
