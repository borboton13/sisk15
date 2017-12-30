package com.encens.khipus.controller;

import com.encens.khipus.model.InvGrupos;
import com.encens.khipus.ejb.InvGruposFacade;
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

@Named("invGruposController")
@SessionScoped
public class InvGruposController implements Serializable {

    @EJB
    private InvGruposFacade ejbFacade;
    private List<InvGrupos> items = null;
    private InvGrupos selected;

    public InvGruposController() {
    }

    public InvGrupos getSelected() {
        return selected;
    }

    public void setSelected(InvGrupos selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
        selected.setInvGruposPK(new com.encens.khipus.model.InvGruposPK());
    }

    private InvGruposFacade getFacade() {
        return ejbFacade;
    }

    public InvGrupos prepareCreate() {
        selected = new InvGrupos();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("InvGruposCreated"));
        if (!JSFUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("InvGruposUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("InvGruposDeleted"));
        if (!JSFUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<InvGrupos> getItems() {
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

    public InvGrupos getInvGrupos(com.encens.khipus.model.InvGruposPK id) {
        return getFacade().find(id);
    }

    public List<InvGrupos> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<InvGrupos> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = InvGrupos.class)
    public static class InvGruposControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            InvGruposController controller = (InvGruposController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "invGruposController");
            return controller.getInvGrupos(getKey(value));
        }

        com.encens.khipus.model.InvGruposPK getKey(String value) {
            com.encens.khipus.model.InvGruposPK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new com.encens.khipus.model.InvGruposPK();
            key.setNoCia(values[0]);
            key.setCodGru(values[1]);
            return key;
        }

        String getStringKey(com.encens.khipus.model.InvGruposPK value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getNoCia());
            sb.append(SEPARATOR);
            sb.append(value.getCodGru());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof InvGrupos) {
                InvGrupos o = (InvGrupos) object;
                return getStringKey(o.getInvGruposPK());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), InvGrupos.class.getName()});
                return null;
            }
        }

    }

}
