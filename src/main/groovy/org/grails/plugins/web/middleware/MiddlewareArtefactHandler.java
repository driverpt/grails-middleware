package org.grails.plugins.web.middleware;

import grails.artefact.middleware.Middleware;
import grails.core.ArtefactHandlerAdapter;
import grails.core.DefaultGrailsClass;
import grails.core.GrailsClass;

/**
 * Created by lduarte on 11/01/16.
 */
public class MiddlewareArtefactHandler extends ArtefactHandlerAdapter {
    public static final String TYPE = Middleware.class.getSimpleName();
    public static final String PLUGIN_NAME = "middleware";

    public MiddlewareArtefactHandler() {
        super(TYPE, GrailsClass.class, DefaultGrailsClass.class, TYPE);
    }

    @Override
    public String getPluginName() {
        return PLUGIN_NAME;
    }

}
