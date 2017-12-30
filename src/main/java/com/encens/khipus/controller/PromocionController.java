package com.encens.khipus.controller;

import com.encens.khipus.ejb.PromocionFacade;
import com.encens.khipus.ejb.VentaarticuloFacade;
import com.encens.khipus.model.ArticulosPromocion;
import com.encens.khipus.model.Promocion;
import com.encens.khipus.model.Ventaarticulo;
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

@Named("promocionController")
@SessionScoped
public class PromocionController implements Serializable {

    @EJB
    private PromocionFacade ejbFacade;
    @EJB
    private VentaarticuloFacade ventaarticuloFacade;

    private List<Promocion> items = null;
    private Promocion selected;
    private Ventaarticulo ventaarticuloElegido;
    private List<Ventaarticulo> articulos = new ArrayList<>();

    public PromocionController() {
    }

    public Promocion getSelected() {
        return selected;
    }

    public void setSelected(Promocion selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private PromocionFacade getFacade() {
        return ejbFacade;
    }

    public Promocion prepareCreate() {
        selected = new Promocion();
        initializeEmbeddableKey();
        articulos = ventaarticuloFacade.findAll();
        return selected;
    }

    public void reponerArticulo(ArticulosPromocion articulosPromocion){
        articulos.add(articulosPromocion.getVentaarticulo());
    }

    public void create() {
        persist(PersistAction.CREATE, "Se registró con exito la promoción.");
        if (!JSFUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
            articulos = null;
            selected = new Promocion();
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, "Se actualizó con exito la promoción.");
    }

    public void cancel(){
        articulos = null;
    }

    public void destroy() {
        persist(PersistAction.DELETE, "Se eliminó con exito la promoción.");
        if (!JSFUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Promocion> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    public void agregarArticulo()
    {
        if(ventaarticuloElegido == null)
            return;

        ArticulosPromocion articulosPromocion = new ArticulosPromocion();
        articulosPromocion.setCantidad(0);
        articulosPromocion.setPromocion(selected);
        articulosPromocion.setVentaarticulo(ventaarticuloElegido);
        selected.getArticulosPromocions().add(articulosPromocion);
        articulos.remove(ventaarticuloElegido);
        ventaarticuloElegido = null;
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

    public Promocion getPromocion(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Promocion> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Promocion> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Promocion.class)
    public static class PromocionControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PromocionController controller = (PromocionController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "promocionController");
            return controller.getPromocion(getKey(value));
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
            if (object instanceof Promocion) {
                Promocion o = (Promocion) object;
                return getStringKey(o.getIdpromocion());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Promocion.class.getName()});
                return null;
            }
        }

    }

    public List<Ventaarticulo> completarArticulo(String query) {
        List<Ventaarticulo> articulosFiltrados = new ArrayList<>();
        for(Ventaarticulo articulo:articulos) {

            if(articulo.getInvArticulos().getDescri().toLowerCase().contains(query)) {
                articulosFiltrados.add(articulo);
            }
        }

        return articulosFiltrados;
    }

    public void setItems(List<Promocion> items) {
        this.items = items;
    }

    public List<Ventaarticulo> getArticulos() {
        return articulos;
    }

    public void setArticulos(List<Ventaarticulo> articulos) {
        this.articulos = articulos;
    }

    public Ventaarticulo getVentaarticuloElegido() {
        return ventaarticuloElegido;
    }

    public void setVentaarticuloElegido(Ventaarticulo ventaarticuloElegido) {
        this.ventaarticuloElegido = ventaarticuloElegido;
    }
}
