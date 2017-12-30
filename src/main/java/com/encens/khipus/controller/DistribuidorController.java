package com.encens.khipus.controller;

import com.encens.khipus.ejb.DistribuidorFacade;
import com.encens.khipus.model.ArticulosPedido;
import com.encens.khipus.model.Distribuidor;
import com.encens.khipus.util.JSFUtil;
import com.encens.khipus.util.JSFUtil.PersistAction;
import com.mysql.jdbc.StringUtils;

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

@Named("distribuidorController")
@SessionScoped
public class DistribuidorController implements Serializable {

    @EJB
    private DistribuidorFacade ejbFacade;
    private List<Distribuidor> items = null;
    private Distribuidor selected;

    public DistribuidorController() {
    }

    public Distribuidor getSelected() {
        return selected;
    }

    public void setSelected(Distribuidor selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private DistribuidorFacade getFacade() {
        return ejbFacade;
    }

    public Distribuidor prepareCreate() {
        selected = new Distribuidor();
        initializeEmbeddableKey();
        return selected;
    }

    public void prepareEdit(){
        selected = getFacade().findById(selected.getPiId());
    }

    public void create() {
        if(validarCampos())
        {
            return;
        }
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("DistribuidorCreated"));
        if (!JSFUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
            selected = new Distribuidor();
        }
    }

    public void update() {
        if(validarCampos())
        {
            return;
        }
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("DistribuidorUpdated"));
        items = null;
        selected = new Distribuidor();
    }

    private boolean validarCampos() {
        Boolean error = false;

            if(StringUtils.isNullOrEmpty(selected.getNom()))
            {
                JSFUtil.addErrorMessage("El nombre es requerido");
                error = true;
            }
            if(StringUtils.isNullOrEmpty(selected.getAp()))
            {
                JSFUtil.addErrorMessage("El apellido paterno es requerido");
                error = true;
            }
            if(StringUtils.isNullOrEmpty(selected.getAm()))
            {
                JSFUtil.addErrorMessage("El apellido materno es requerido");
                error = true;
            }
            if(StringUtils.isNullOrEmpty(selected.getNroDoc()))
            {
                JSFUtil.addErrorMessage("El C.I. es requerido");
                error = true;
            }

        return error;
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("DistribuidorDeleted"));
        if (!JSFUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void cancel()
    {
        selected = null; // Remove selection
    }

    public List<Distribuidor> getItems() {
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

    public Distribuidor getDistribuidor(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Distribuidor> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Distribuidor> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Distribuidor.class)
    public static class DistribuidorControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            DistribuidorController controller = (DistribuidorController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "distribuidorController");
            return controller.getDistribuidor(getKey(value));
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
            if (object instanceof Distribuidor) {
                Distribuidor o = (Distribuidor) object;
                return getStringKey(o.getPiId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Distribuidor.class.getName()});
                return null;
            }
        }

    }

}
