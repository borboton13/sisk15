package com.encens.khipus.controller;

import com.encens.khipus.model.ArticulosPaquete;
import com.encens.khipus.ejb.ArticulosPaqueteFacade;
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

@Named("articulosPaqueteController")
@SessionScoped
public class ArticulosPaqueteController implements Serializable {

    @EJB
    private ArticulosPaqueteFacade ejbFacade;
    private List<ArticulosPaquete> items = null;
    private ArticulosPaquete selected;

    public ArticulosPaqueteController() {
    }

    public ArticulosPaquete getSelected() {
        return selected;
    }

    public void setSelected(ArticulosPaquete selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ArticulosPaqueteFacade getFacade() {
        return ejbFacade;
    }

    public ArticulosPaquete prepareCreate() {
        selected = new ArticulosPaquete();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, "");
        if (!JSFUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, "");
    }

    public void destroy() {
        persist(PersistAction.DELETE, "");
        if (!JSFUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<ArticulosPaquete> getItems() {
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

    public ArticulosPaquete getArticulosPaquete(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<ArticulosPaquete> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<ArticulosPaquete> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = ArticulosPaquete.class)
    public static class ArticulosPaqueteControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ArticulosPaqueteController controller = (ArticulosPaqueteController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "articulosPaqueteController");
            return controller.getArticulosPaquete(getKey(value));
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
            if (object instanceof ArticulosPaquete) {
                ArticulosPaquete o = (ArticulosPaquete) object;
                return getStringKey(o.getIdarticulospaquete());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), ArticulosPaquete.class.getName()});
                return null;
            }
        }

    }

}
