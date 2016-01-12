package grails.middleware

import grails.artefact.middleware.Middleware
import grails.core.GrailsClass
import grails.plugins.Plugin
import grails.util.GrailsUtil
import org.grails.plugins.web.middleware.MiddlewareArtefactHandler
import org.springframework.boot.context.embedded.FilterRegistrationBean
import org.springframework.context.ApplicationContext
import org.springframework.core.Ordered

class GrailsMiddlewareGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.0.0 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def title = "Grails Middleware" // Headline display name of the plugin
    def author = "Your name"
    def authorEmail = ""
    def description = '''\
The Grails middleware plugin provides a convenient DSL to create an HTTP Request/Response Pipeline.
'''
    def profiles = ['web']

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/grails-middleware"

    def license = "APACHE"
    def developers = [[name: "Luis Duarte", email: "lduarte@luisduarte.net"]]
    def issueManagement = [url: "http://github.com/driverpt/grails-middleware/issues"]
    def scm = [url: "http://github.com/driverpt/grails-middleware"]

    def version = "0.0.2"
    def observe = ["interceptors"]
    def loadAfter = ["interceptors"]
    def dependsOn = [core: GrailsUtil.getGrailsVersion(), i18n: GrailsUtil.getGrailsVersion(), interceptors: GrailsUtil.getGrailsVersion()]

    def watchedResources = [
            "file:./grails-app/middleware/**/*Middleware.groovy",
            "file:./plugins/*/grails-app/middleware/**/*Middleware.groovy"
    ]

    def currentGrailsMiddlewareFilter

    Closure doWithSpring() {
        { ->
            log.trace "Injecting"

            GrailsClass[] middlewareChain = grailsApplication.getArtefacts(MiddlewareArtefactHandler.TYPE)

            for (GrailsClass i in middlewareChain) {
                "${i.propertyName}"(i.clazz) { bean ->
                    bean.autowire = 'byName'
                }
            }

            def order = grailsApplication.config.grails.middleware?.order

            if (order.isEmpty() || order == null) {
                return
            }

            log.trace "Instanciating Middleware Classes: $order"

            def chain = getBeanClasses()

            grailsMiddlewareFilter(FilterRegistrationBean) {
                filter = bean(GrailsMiddlewareFilter) {
                    middlewares = chain
                }
                urlPatterns = ['/*']
                // T
                order = Ordered.HIGHEST_PRECEDENCE + 40
            }
        }
    }

    void doWithDynamicMethods() {
    }

    void doWithApplicationContext() {
        if (applicationContext.containsBeanDefinition("grailsMiddlewareFilter")) {
            currentGrailsMiddlewareFilter = applicationContext.getBean("grailsMiddlewareFilter").filter
        }
    }

    void onChange(Map<String, Object> event) {
        if (!(event.source instanceof Class)) {
            return
        }

        def application = grailsApplication
        if (application.isArtefactOfType(MiddlewareArtefactHandler.TYPE, (Class)event.source)) {
            ApplicationContext context = applicationContext
            if (!context) {
                if (log.isDebugEnabled()) {
                    log.debug("Application context not found. Can't reload")
                }
                return
            }

            GrailsClass middlewareClass = application.addArtefact(MiddlewareArtefactHandler.TYPE, (Class)event.source)

            def middlewareFilter = this.currentGrailsMiddlewareFilter ?: applicationContext.getBean(GrailsMiddlewareFilter)
            beans {
                "${middlewareClass.propertyName}"(middlewareClass) { bean ->
                    bean.autowire = 'byName'
                }
            }

            def chain = getBeanClasses()
            middlewareFilter.setMiddlewares(chain)
        }
    }

    void onConfigChange(Map<String, Object> event) {
        log.trace "onConfigChange called"
    }

    void onShutdown(Map<String, Object> event) {
        log.trace "onShutdown called"
    }

    private List<? extends Middleware> getBeanClasses() {
        def order = grailsApplication.config.grails.middleware?.order

        def result = []

        if(order.size > 0 || order != null) {
            for (String orderElement in order) {
                def middlewareClass = this.class.classLoader.loadClass(orderElement)
                if (!Middleware.isAssignableFrom(middlewareClass)) {
                    throw new RuntimeException()
                }
                result.add(middlewareClass.newInstance())
            }
        }

        result
    }
}
