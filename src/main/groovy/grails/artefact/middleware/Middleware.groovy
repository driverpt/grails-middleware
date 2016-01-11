package grails.artefact.middleware
import grails.web.api.ServletAttributes
import grails.web.api.WebAttributes
import grails.web.databinding.DataBinder
import org.grails.web.servlet.mvc.GrailsWebRequest
/**
 * Created by lduarte on 09/01/16.
 */
trait Middleware implements DataBinder, WebAttributes, ServletAttributes {
    def void processRequest(GrailsWebRequest grailsWebRequest) {}
    def void processResponse(GrailsWebRequest grailsWebRequest) {}
    def void processException(GrailsWebRequest grailsWebRequest, Throwable exception) {}
}