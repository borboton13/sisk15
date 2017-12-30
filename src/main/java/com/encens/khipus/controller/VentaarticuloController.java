package com.encens.khipus.controller;

import com.encens.khipus.ejb.InvArticulosFacade;
import com.encens.khipus.model.InvArticulos;
import com.encens.khipus.model.Ventaarticulo;
import com.encens.khipus.ejb.VentaarticuloFacade;
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

@Named("ventaarticuloController")
@SessionScoped
public class VentaarticuloController implements Serializable {

    @EJB
    private com.encens.khipus.ejb.VentaarticuloFacade ejbFacade;
    @EJB
    private InvArticulosFacade invArticulosFacade;
    private List<Ventaarticulo> items = null;
    private Ventaarticulo selected;
    private List<InvArticulos> articulos;

    public VentaarticuloController() {
    }

    public Ventaarticulo getSelected() {
        return selected;
    }

    public void setSelected(Ventaarticulo selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private VentaarticuloFacade getFacade() {
        return ejbFacade;
    }

    public Ventaarticulo prepareCreate() {
        List<Ventaarticulo> yaRegistrados = getFacade().findAll();
        selected = new Ventaarticulo();
        articulos = invArticulosFacade.findAllInvArticulos();
        for(Ventaarticulo ventaarticulo:yaRegistrados)
        {
            articulos.remove(ventaarticulo.getInvArticulos());
        }
        initializeEmbeddableKey();
        return selected;
    }

    public void prepareEdit(){
        selected = getFacade().findById(selected.getIdventaarticulo());
    }

    public void create() {
        persist(PersistAction.CREATE, "El producto se registro correctamente.");
        if (!JSFUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
            actualizaListadoarticulos();
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, "El producto se actualizo registro correctamente.");
        actualizaListadoarticulos();
    }

    private void actualizaListadoarticulos(){
        List<Ventaarticulo> yaRegistrados = getFacade().findAll();
        articulos = invArticulosFacade.findAllInvArticulos();
        for(Ventaarticulo ventaarticulo:yaRegistrados)
        {
            articulos.remove(ventaarticulo.getInvArticulos());
        }
    }

    public void destroy() {
        persist(PersistAction.DELETE, "El producto se elimino correctamente.");
        if (!JSFUtil.isValidationFailed()) {
            actualizaListadoarticulos();
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Ventaarticulo> getItems() {
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

    public List<InvArticulos> completarArticulo(String query) {
        List<InvArticulos> articulosFiltrados = new ArrayList<>();
        for(InvArticulos articulo:articulos) {

            if(articulo.getDescri().toLowerCase().contains(query)) {
                articulosFiltrados.add(articulo);
            }
        }

        return articulosFiltrados;
    }

    public Ventaarticulo getVentaarticulo(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Ventaarticulo> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Ventaarticulo> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(value = "convertirVentaArticulo",forClass = Ventaarticulo.class)
    public static class VentaarticuloControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            VentaarticuloController controller = (VentaarticuloController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "ventaarticuloController");
            return controller.getVentaarticulo(getKey(value));
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
            if (object instanceof Ventaarticulo) {
                Ventaarticulo o = (Ventaarticulo) object;
                return getStringKey(o.getIdventaarticulo());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Ventaarticulo.class.getName()});
                return null;
            }
        }

    }

}
