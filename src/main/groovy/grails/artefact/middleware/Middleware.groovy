package grails.artefact.middleware
import grails.web.api.ServletAttributes
import grails.web.api.WebAttributes
import grails.web.databinding.DataBinder
import org.grails.web.servlet.mvc.GrailsWebRequest
/**
 * Created by lduarte on 09/01/16.
 */
trait Middleware implements DataBinder, WebAttributes, ServletAttributes {
    void processRequest(GrailsWebRequest grailsWebRequest) {}
    void processResponse(GrailsWebRequest grailsWebRequest) {}
    void processException(GrailsWebRequest grailsWebRequest, Throwable exception) {}
}