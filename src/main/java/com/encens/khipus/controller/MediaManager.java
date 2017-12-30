package com.encens.khipus.controller;

import org.primefaces.component.media.Media;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.inject.Named;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by Diego on 28/05/2015.
 */

@ApplicationScoped
@Named("mediaManager")
public class MediaManager {
    public StreamedContent getStream() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

            return new DefaultStreamedContent();
    }

}