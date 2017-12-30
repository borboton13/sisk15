package com.encens.khipus.controller;

import com.encens.khipus.ejb.ArcgmsFacade;
import com.encens.khipus.ejb.OperacionesFacade;
import com.encens.khipus.model.*;
import com.encens.khipus.util.JSFUtil;
import com.encens.khipus.util.JSFUtil.PersistAction;
import com.encens.khipus.ejb.SfConfencFacade;

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

@Named("sfConfencController")
@SessionScoped
public class SfConfencController implements Serializable {

    @EJB
    private SfConfencFacade ejbFacade;
    @EJB
    private ArcgmsFacade arcgmsFacade;
    @EJB
    private TipodocFacade tipodocFacade;
    @EJB
    private OperacionesFacade operacionesFacade;
    private List<SfConfenc> items = null;
    private SfConfenc selected;
    private Arcgms cuentaElegida;
    private List<Arcgms> cuentas= new ArrayList<>();
    private List<Tipodoc> tipodocs = new ArrayList<>();
    private List<Operaciones> operaciones = new ArrayList<>();

    public SfConfencController() {
    }

    public SfConfenc getSelected() {
        return selected;
    }

    public void setSelected(SfConfenc selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    public List<Arcgms> completarCuenta(String query) {
        List<Arcgms> clientesFiltrados = new ArrayList<>();
        for(Arcgms cuenta: cuentas) {

            if(cuenta.getCuenta().toLowerCase().contains(query)) {
                clientesFiltrados.add(cuenta);
            }
        }

        return clientesFiltrados;
    }

    public List<Arcgms> completarCuentaDescripcion(String query) {
        List<Arcgms> clientesFiltrados = new ArrayList<>();
        for(Arcgms cuenta: cuentas) {

            if(cuenta.getDescri().toLowerCase().contains(query)) {
                clientesFiltrados.add(cuenta);
            }
        }

        return clientesFiltrados;
    }

    public void reponerCuenta(SfConfdet sfConfdet){
        cuentas.add(sfConfdet.getCuenta());
    }

    public void agregarCuentaAlHaber(){
        SfConfdet sfConfdet = new SfConfdet();
        sfConfdet.setCuenta(cuentaElegida);
        sfConfdet.setSfConfenc(selected);
        sfConfdet.setTipomovimiento("HABER");
        cuentas.remove(cuentaElegida);
        selected.getAsientos().add(sfConfdet);
        cuentaElegida = new Arcgms();
    }

    public void agregarCuentaAlDebe(){
        SfConfdet sfConfdet = new SfConfdet();
        sfConfdet.setCuenta(cuentaElegida);
        sfConfdet.setSfConfenc(selected);
        sfConfdet.setTipomovimiento("DEBE");
        cuentas.remove(cuentaElegida);
        selected.getAsientos().add(sfConfdet);
        cuentaElegida = new Arcgms();
    }

    private SfConfencFacade getFacade() {
        return ejbFacade;
    }

    public SfConfenc prepareCreate() {
        selected = new SfConfenc();
        initializeEmbeddableKey();
        cuentas = arcgmsFacade.findAll();
        cuentaElegida = new Arcgms();
        tipodocs = tipodocFacade.findAll();
        operaciones = operacionesFacade.findAll();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE,"Se registro con exito la configuración");
        if(!getFacade().existeTipoDoc(selected.getTipoDoc()))
        getFacade().crearSecuencia(selected.getTipoDoc());
        if (!JSFUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
            selected = new SfConfenc();
            cuentas = arcgmsFacade.findAll();
            cuentaElegida = new Arcgms();
            tipodocs = tipodocFacade.findAll();
            operaciones = operacionesFacade.findAll();
        }
    }

    public void prepareEdit(){
        selected = getFacade().findByID(selected.getIdSfConfenc());
        cuentas = arcgmsFacade.findAll();
        cuentaElegida = new Arcgms();
        tipodocs = tipodocFacade.findAll();
        operaciones = operacionesFacade.findAll();
    }

    public void update() {
        persist(PersistAction.UPDATE, "Se actualizo con exito la configuración");
    }

    public void cancel(){
        selected = null;
    }

    public void destroy() {
        persist(PersistAction.DELETE, "Se elimino con exito la configuración");
        if (!JSFUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<SfConfenc> getItems() {
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

    public SfConfenc getSfConfenc(long id) {
        return getFacade().find(id);
    }

    public List<SfConfenc> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<SfConfenc> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = SfConfenc.class)
    public static class SfConfencControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SfConfencController controller = (SfConfencController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "sfConfencController");
            return controller.getSfConfenc(getKey(value));
        }

        long getKey(String value) {
            long key;
            key = Long.parseLong(value);
            return key;
        }

        String getStringKey(long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof SfConfenc) {
                SfConfenc o = (SfConfenc) object;
                return getStringKey(o.getIdSfConfenc());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), SfConfenc.class.getName()});
                return null;
            }
        }

    }

    public Arcgms getCuentaElegida() {
        return cuentaElegida;
    }

    public void setCuentaElegida(Arcgms cuentaElegida) {
        this.cuentaElegida = cuentaElegida;
    }

    public List<Arcgms> getCuentas() {
        return cuentas;
    }

    public void setCuentas(List<Arcgms> cuentas) {
        this.cuentas = cuentas;
    }

    public List<Tipodoc> getTipodocs() {
        return tipodocs;
    }

    public void setTipodocs(List<Tipodoc> tipodocs) {
        this.tipodocs = tipodocs;
    }

    public List<Operaciones> getOperaciones() {
        return operaciones;
    }

    public void setOperaciones(List<Operaciones> operaciones) {
        this.operaciones = operaciones;
    }
}
