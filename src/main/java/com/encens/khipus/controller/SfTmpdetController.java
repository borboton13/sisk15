package com.encens.khipus.controller;

import com.encens.khipus.model.SfTmpdet;
import com.encens.khipus.ejb.SfTmpdetFacade;
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

@Named("sfTmpdetController")
@SessionScoped
public class SfTmpdetController implements Serializable {

    @EJB
    private com.encens.khipus.ejb.SfTmpdetFacade ejbFacade;
    private List<SfTmpdet> items = null;
    private SfTmpdet selected;

    public SfTmpdetController() {
    }

    public SfTmpdet getSelected() {
        return selected;
    }

    public void setSelected(SfTmpdet selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private SfTmpdetFacade getFacade() {
        return ejbFacade;
    }

    public SfTmpdet prepareCreate() {
        selected = new SfTmpdet();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("SfTmpdetCreated"));
        if (!JSFUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("SfTmpdetUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("SfTmpdetDeleted"));
        if (!JSFUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<SfTmpdet> getItems() {
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

    public SfTmpdet getSfTmpdet(Long id) {
        return getFacade().find(id);
    }

    public List<SfTmpdet> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<SfTmpdet> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public void createGeneral() {
        getFacade().create(selected);
    }

    @FacesConverter(forClass = SfTmpdet.class)
    public static class SfTmpdetControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SfTmpdetController controller = (SfTmpdetController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "sfTmpdetController");
            return controller.getSfTmpdet(getKey(value));
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
            if (object instanceof SfTmpdet) {
                SfTmpdet o = (SfTmpdet) object;
                return getStringKey(o.getIdTmpdet());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), SfTmpdet.class.getName()});
                return null;
            }
        }

    }

}
