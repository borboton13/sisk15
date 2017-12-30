package com.encens.khipus.controller;

import com.encens.khipus.ejb.*;
import com.encens.khipus.model.*;
import com.encens.khipus.util.JSFUtil;
import com.encens.khipus.util.JSFUtil.PersistAction;
import com.mysql.jdbc.StringUtils;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("personaController")
@SessionScoped
public class PersonaController implements Serializable {

    @EJB
    private PersonasFacade ejbFacade;    
    @EJB
    private ClienteFacade clienteFacade;
    @EJB
    private InstitucionFacade institucionFacade;
    @EJB
    private InvArticulosFacade invArticulosFacade;
    @EJB
    private VentaarticuloFacade ventaarticuloFacade;
    @EJB
    private VentaclienteFacade ventaclienteFacade;

    private List<Persona> items = null;
    private Persona selected;
    private Boolean esPersona;
    private Boolean tieneComision;
    private Boolean tieneGarantia;
    private Boolean confacura;
    private InvArticulos articuloElegido;
    private List<InvArticulos> articulos;

    public PersonaController() {
    }

    public Persona getSelected() {
        /*if(selected == null)
            selected = items.get(0);*/
        //initEstados();
        return selected;
    }

    public void initEstados(){
        esPersona = false;
        if(selected.getRazonsocial() != null)
            esPersona = true;
        if(selected.getPorcentajeComision() > 0.0)
            tieneComision = false;
        else
            tieneComision = true;
        if(selected.getPorcentajeGarantia() > 0.0)
            tieneGarantia = false;
        else
            tieneGarantia = true;
    }

    public void setSelected(Persona selected) {
       // prepareEdit(selected);
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private PersonasFacade getFacade() {
        return ejbFacade;
    }

    public Persona prepareCreate() {
        selected = new Persona();
        articulos = invArticulosFacade.findAllInvArticulos();
        initializeEmbeddableKey();
        esPersona = true;
        tieneComision = false;
        tieneGarantia = false;
        confacura = true;
        return selected;
    }

    public void prepareEdit(Persona persona) {
        System.out.println("======> prepareEdit: " + persona);
        selected = getFacade().findById(persona);
        System.out.println("======> prepareEdit selected: " + selected);
        articulos = invArticulosFacade.findAllInvArticulos();
        confacura = persona.getConfactura();
        if(selected.getVentaclientes().size() >0){
            for(Ventacliente ventacliente: selected.getVentaclientes())
            {
              articulos.remove(ventacliente.getInvArticulos());
            }
        }
        if(selected != null  ) {
            esPersona = selected.getEspersona();
            if(selected.getPorcentajeComision() != null)
                tieneComision = selected.getPorcentajeComision() > 0.0;
            if(selected.getPorcentajeGarantia() != null)
                tieneGarantia = selected.getPorcentajeGarantia() > 0.0;
        }
        System.out.println("======> esPersona: " + esPersona);
    }

    private boolean validarCampos() {
        Boolean error = false;
      if(!esPersona)
      {
          if(StringUtils.isNullOrEmpty(selected.getRazonsocial()))
          {
              JSFUtil.addErrorMessage("La razon social es requerida");
              error = true;
          }
          if(StringUtils.isNullOrEmpty(selected.getNit()))
          {
              JSFUtil.addErrorMessage("El nit de la institución es requerida");
              error = true;
          }
      }else{
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
      }
        return error;
    }

    public void reponerArticuloYQuitarArticulo(Ventacliente item){
        selected.getVentaclientes().remove(item);
        ventaclienteFacade.remove(item);
        item.setPersona(null);
        articulos.add(item.getInvArticulos());
    }

    public void create() {

        if(validarCampos())
        {
            return;
        }

        if(esPersona) {
            Cliente cliente = new Cliente();
            cliente.setAm(selected.getAm());
            cliente.setAp(selected.getAp());
            cliente.setNom(selected.getNom());
            cliente.setRazonsocial(selected.getRazonsocial());
            cliente.setPorcentajeComision(selected.getPorcentajeComision());
            cliente.setPorcentajeGarantia(selected.getPorcentajeGarantia());
            cliente.setNroDoc(selected.getNroDoc());
            cliente.setDireccion(selected.getDireccion());
            cliente.setNit(selected.getNit());
            cliente.setSexo(selected.getSexo());
            cliente.setTelefono(selected.getTelefono());
            cliente.setTipocliente(selected.getTipocliente());
            cliente.setTerritoriotrabajo(selected.getTerritoriotrabajo());
            cliente.setConfactura(confacura);
            cliente.getVentaclientes().addAll(selected.getVentaclientes());

            cliente.setEspersona(true);
            for(Ventacliente ventacliente:cliente.getVentaclientes())
            {
                ventacliente.setPersona(cliente);
            }
            clienteFacade.create(cliente);
        }
        else {
            Institucion institucion = new Institucion();
            institucion.setAm("");
            institucion.setAp("");
            institucion.setSexo("");
            institucion.setRazonsocial(selected.getRazonsocial());
            institucion.setNom(selected.getNom());
            institucion.setEspersona(false);
            institucion.setTerritoriotrabajo(selected.getTerritoriotrabajo());
            if(tieneComision)
                institucion.setPorcentajeComision(selected.getPorcentajeComision());
            else
                institucion.setPorcentajeComision(0.0);
            if(tieneGarantia)
                institucion.setPorcentajeGarantia(selected.getPorcentajeGarantia());
            else
                institucion.setPorcentajeGarantia(0.0);
            institucion.setDireccion(selected.getDireccion());
            institucion.setNit(selected.getNit());
            institucion.setTelefono(selected.getTelefono());
            institucion.setTipocliente(selected.getTipocliente());
            institucion.setConfactura(confacura);
            institucion.getVentaclientes().addAll(selected.getVentaclientes());
            for(Ventacliente ventacliente:institucion.getVentaclientes())
            {
                ventacliente.setPersona(institucion);
            }
            institucionFacade.create(institucion);
        }
        JSFUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ClienteCreated"));
        if (!JSFUtil.isValidationFailed()) {
            prepareCreate();
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void cancel(){
        articuloElegido = null;
        selected = null;
    }

    public void update() {
        System.out.println("Precio especial: ");
        for (Ventacliente vc : selected.getVentaclientes()){
            System.out.println(vc.getInvArticulos().getDescri() + " - " + vc.getPrecioespecial());
        }
        if(!tieneComision)
            selected.setPorcentajeComision(0.0);
        if(!tieneGarantia)
            selected.setPorcentajeGarantia(0.0);

        if(esPersona) {
            selected.setTipoPersona("cliente");            
        }
        else {
            selected.setTipoPersona("institucion");
            selected.setAp("");
            selected.setAm("");
        }
        selected.setConfactura(confacura);
        ventaclienteFacade.mergeItems(selected.getVentaclientes());
        persist(PersistAction.UPDATE, "El cliente se actualizó correctamente.");
        items = null;
    }

    public void destroy(Persona persona) {
        selected = persona;
        persist(PersistAction.DELETE, "La persona se elimino correctamente.");
        if (!JSFUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
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

    public List<Persona> completarCliente(String query) {
        List<Persona> clientesFiltrados = new ArrayList<>();
        List<Persona> personas = getFacade().findAllClientesPersonaInstitucion();
        for(Persona persona: personas) {

            if(persona.getNombreCompleto().toLowerCase().contains(query)) {
                clientesFiltrados.add(persona);
            }
        }

        return clientesFiltrados;
    }

    public void agregarArticulo()
    {
        if(articuloElegido == null)
            return;
        Ventaarticulo ventaarticulo = ventaarticuloFacade.findByInvArticulo(articuloElegido);
        Ventacliente ventacliente = new Ventacliente();
        ventacliente.setInvArticulos(articuloElegido);
        ventacliente.setPersona(selected);
        ventacliente.setPrecioespecial(ventaarticulo.getPrecio());
        selected.getVentaclientes().add(ventacliente);
        articulos.remove(articuloElegido);
        articuloElegido = null;
    }

    public List<Persona> getItems() {
        if (items == null) {
            items = getFacade().findAllClientesPersonaInstitucion();
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

    public Persona getPersonas(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Persona> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Persona> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(value = "convertirPersona",forClass = Persona.class)
    public static class PersonasControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
                if (value == null || value.length() == 0) {
                return null;
            }
            PersonaController controller = (PersonaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "personaController");
            return controller.getPersonas(getKey(value));
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
            if (object instanceof Persona) {
                Persona o = (Persona) object;
                if(o.getPiId() == null)
                    return null;
                return getStringKey(o.getPiId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Persona.class.getName()});
                return null;
            }
        }

    }

    public Boolean getEsPersona() {
        return esPersona;
    }

    public void setEsPersona(Boolean esPersona) {
        this.esPersona = esPersona;
    }

    public Boolean getTieneComision() {
       /* if(selected.getPorcentajeComision() != null)
            tieneComision = true;*/
        return tieneComision;
    }

    public void setTieneComision(Boolean tieneComision) {
        this.tieneComision = tieneComision;
    }

    public Boolean getTieneGarantia() {
       /* if(selected.getPorcentajeGarantia() != null)
            tieneGarantia = true;*/
        return tieneGarantia;
    }

    public void setTieneGarantia(Boolean tieneGarantia) {
        this.tieneGarantia = tieneGarantia;
    }

    public Boolean getConfacura() {
        return confacura;
    }

    public void setConfacura(Boolean confacura) {
        this.confacura = confacura;
    }

    public InvArticulos getArticuloElegido() {
        return articuloElegido;
    }

    public void setArticuloElegido(InvArticulos articuloElegido) {
        this.articuloElegido = articuloElegido;
    }

    public List<InvArticulos> getArticulos() {
        return articulos;
    }

    public void setArticulos(List<InvArticulos> articulos) {
        this.articulos = articulos;
    }

}

