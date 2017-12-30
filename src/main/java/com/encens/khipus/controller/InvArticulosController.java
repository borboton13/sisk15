package com.encens.khipus.controller;

import com.encens.khipus.model.InvArticulos;
import com.encens.khipus.ejb.InvArticulosFacade;
import com.encens.khipus.util.JSFUtil;
import com.encens.khipus.util.JSFUtil.PersistAction;

import java.io.Serializable;
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

@Named("invArticulosController")
@SessionScoped
public class InvArticulosController implements Serializable {

    @EJB
    private InvArticulosFacade ejbFacade;
    private List<InvArticulos> items = null;
    private InvArticulos selected;

    public InvArticulosController() {
    }

    public InvArticulos getSelected() {
        return selected;
    }

    public void setSelected(InvArticulos selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
        selected.getInvArticulosPK().setNoCia(selected.getInvGrupos().getInvGruposPK().getNoCia());
    }

    protected void initializeEmbeddableKey() {
        selected.setInvArticulosPK(new com.encens.khipus.model.InvArticulosPK());
    }

    private InvArticulosFacade getFacade() {
        return ejbFacade;
    }

    public InvArticulos prepareCreate() {
        selected = new InvArticulos();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("InvArticulosCreated"));
        if (!JSFUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("InvArticulosUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("InvArticulosDeleted"));
        if (!JSFUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<InvArticulos> getItems() {
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

    public InvArticulos getInvArticulos(com.encens.khipus.model.InvArticulosPK id) {
        return getFacade().find(id);
    }

    public InvArticulos getInvArticulos(String codArt) {
        return getFacade().findByCodArt(codArt);
    }

    public List<InvArticulos> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<InvArticulos> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(value = "convertirArticulos",forClass = InvArticulos.class)
    public static class InvArticulosControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            InvArticulosController controller = (InvArticulosController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "invArticulosController");

            return controller.getInvArticulos(value);
        }

        com.encens.khipus.model.InvArticulosPK getKey(String value) {
            com.encens.khipus.model.InvArticulosPK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new com.encens.khipus.model.InvArticulosPK();
            key.setNoCia(values[0]);
            key.setCodArt(values[1]);
            return key;
        }

        String getStringKey(com.encens.khipus.model.InvArticulosPK value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getNoCia());
            sb.append(SEPARATOR);
            sb.append(value.getCodArt());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null ) {
                return null;
            }
            if (object instanceof InvArticulos) {
                InvArticulos o = (InvArticulos) object;
                if(o.getInvArticulosPK() == null)
                    return null;
                return o.getInvArticulosPK().getCodArt();
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), InvArticulos.class.getName()});
                return null;
            }
        }

    }

}
