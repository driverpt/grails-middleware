package grails.compiler.traits

import grails.artefact.middleware.Middleware
import groovy.transform.CompileStatic

/**
 * Created by lduarte on 09/01/16.
 */
@CompileStatic
class MiddlewareTraitInjector implements TraitInjector {
    @Override
    Class getTrait() {
        Middleware
    }

    @Override
    String[] getArtefactTypes() {
        ['Middleware'] as String[]
    }
}

