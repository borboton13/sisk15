package com.encens.khipus.controller;

import com.encens.khipus.ejb.DistribuidorFacade;
import com.encens.khipus.ejb.PersonasFacade;
import com.encens.khipus.model.Distribuidor;
import com.encens.khipus.model.Persona;
import com.encens.khipus.model.Territoriotrabajo;
import com.encens.khipus.ejb.TerritoriotrabajoFacade;
import com.encens.khipus.util.JSFUtil;
import com.encens.khipus.util.JSFUtil.PersistAction;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("territoriotrabajoController")
@SessionScoped
public class TerritoriotrabajoController implements Serializable {

    @EJB
    private com.encens.khipus.ejb.TerritoriotrabajoFacade ejbFacade;
    @EJB
    private PersonasFacade personasFacade;

    private List<Territoriotrabajo> items = null;
    private Territoriotrabajo selected;
    private Persona distribuidorElegido;
    private List<Persona> distribuidores;

    public TerritoriotrabajoController() {
    }

    public Territoriotrabajo getSelected() {
        return selected;
    }

    public void setSelected(Territoriotrabajo selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    public List<Persona> completarDistribuidor(String query) {
        List<Persona> distribuidoresFiltrados = new ArrayList<>();
        for(Persona persona: distribuidores) {

            if(persona.getNombreCompleto().toLowerCase().contains(query)) {
                distribuidoresFiltrados.add(persona);
            }
        }

        return distribuidoresFiltrados;
    }

    private TerritoriotrabajoFacade getFacade() {
        return ejbFacade;
    }

    public Territoriotrabajo prepareCreate() {
        selected = new Territoriotrabajo();
        distribuidorElegido = new Distribuidor();
        distribuidores = personasFacade.findAlldistribuidores();
        initializeEmbeddableKey();
        items = null;
        return selected;
    }

    public void prepareEdit(){
        selected = getFacade().findById(selected.getIdterritoriotrabajo());
    }

    public void cancel(){
        selected = new Territoriotrabajo();
        items = null;
    }

    public void create() {
        if(validarCampos())
        {
            return;
        }
        selected.setDistribuidor(distribuidorElegido);
        persist(PersistAction.CREATE, "Se creo correctamente el territorio.");
        if (!JSFUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
            prepareCreate();
        }
    }

    public Boolean validarCampos()
    {
        Boolean error = false;
        if(distribuidorElegido == null)
        {
            JSFUtil.addErrorMessage( "El distribuidor es necesario");
            error = true;
        }
        if(selected.getNombre().isEmpty())
        {
            JSFUtil.addErrorMessage( "El nombre es requerido.");
            error = true;
        }

        return error;
    }

    public void update() {
        persist(PersistAction.UPDATE, "Se actualizo correctamente el territorio.");
    }

    public void destroy() {
        persist(PersistAction.DELETE, "Se elimino correctamente el territorio.");
        if (!JSFUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Territoriotrabajo> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JSFUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JSFUtil.addErrorMessage(msg);
                } else {
                    JSFUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JSFUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Territoriotrabajo getTerritoriotrabajo(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Territoriotrabajo> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Territoriotrabajo> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Territoriotrabajo.class)
    public static class TerritoriotrabajoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TerritoriotrabajoController controller = (TerritoriotrabajoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "territoriotrabajoController");
            return controller.getTerritoriotrabajo(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Territoriotrabajo) {
                Territoriotrabajo o = (Territoriotrabajo) object;
                return getStringKey(o.getIdterritoriotrabajo());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Territoriotrabajo.class.getName()});
                return null;
            }
        }

    }

    public Persona getDistribuidorElegido() {
        return distribuidorElegido;
    }

    public void setDistribuidorElegido(Persona distribuidorElegido) {
        this.distribuidorElegido = distribuidorElegido;
    }
}
