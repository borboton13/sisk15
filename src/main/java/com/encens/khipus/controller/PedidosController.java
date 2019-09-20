package com.encens.khipus.controller;

import com.encens.khipus.ejb.*;
import com.encens.khipus.model.*;
import com.encens.khipus.util.DateUtil;
import com.encens.khipus.util.DateUtils;
import com.encens.khipus.util.JSFUtil;
import com.encens.khipus.util.JSFUtil.PersistAction;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.SelectEvent;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("pedidosController")
@SessionScoped
public class PedidosController implements Serializable {

    /*@EJB
    private PedidosFacade ejbFacade;*/
    @EJB
    private PedidosFacade pedidosFacade;
    @EJB
    private InvArticulosFacade invArticulosFacade;
    @EJB
    private PersonasFacade personasFacade;
    @EJB
    private ArticulosPedidoFacade articulosPedidoFacade;
    @EJB
    private VentaarticuloFacade ventaarticuloFacade;
    @EJB
    private VentaclienteFacade ventaclienteFacade;
    @EJB
    SfTmpencFacade sfTmpencFacade;
    @Inject
    private ArticulosPedidoController articulosPedidoController;

    private List<String> etiquetas = Arrays.asList(new String[]{ "", "S.PRENATAL Y L.", "S. UNIVERSAL" });
    private String etiquetaSel;
    private String observacion;

    private List<ArticulosPedido> articulosPedidos = new ArrayList<>();
    private List<ArticulosPedido> articulosPedidosElegidos = new ArrayList<>();
    private List<InvArticulos> articulos;
    private List<Pedidos> reposiciones = new ArrayList<>();
    private List<Pedidos> items = null;
    private Pedidos selected;
    private List<Persona> personas;
    private Tipopedido tipopedido;
    private Integer importeTotal = 0;
    private InvArticulos articuloElegido;
    private List<Pedidos> pedidosFiltrado;
    private List<Pedidos> pedidosElegidos = new ArrayList<>();
    private List<String> estados;
    private Date fechaEntregaFiltro;
    private Date fechaPedidoFiltro;
    private Date fechaDesde;
    private Persona personaElegida;
    private Boolean conReposicion;
    private Boolean tieneReposicion;
    private Boolean reposicionesYaAgregadas = false;

    public PedidosController() {
    }

    @PostConstruct
    public void init() {
        //fechaDesde = new Date();
        //items = getFacade().findPedidosFrom(fechaDesde);
    }

    public Pedidos getSelected() {
        return selected;
    }

    public void setSelected(Pedidos selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
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
        for(Persona persona: personas) {

            if(persona.getNombreCompleto().toLowerCase().contains(query)) {
                clientesFiltrados.add(persona);
            }
        }

        return clientesFiltrados;
    }

    public void agregarArticuloEdit()
    {
        if(articuloElegido == null)
            return;
        Ventaarticulo ventaarticulo = ventaarticuloFacade.findByInvArticulo(articuloElegido);
        Double precio = ventaarticulo.getPrecio();
        if(personaElegida.getVentaclientes().size() >0)
        {
            for(Ventacliente ventacliente:personaElegida.getVentaclientes()){
                if(ventacliente.getInvArticulos().getInvArticulosPK().getCodArt().equals( articuloElegido.getInvArticulosPK().getCodArt()))
                {
                    precio = ventacliente.getPrecioespecial();
                }
            }
        }
        ArticulosPedido articulosPedido = new ArticulosPedido();
        articulosPedido.setInvArticulos(articuloElegido);
        articulosPedido.setPrecioInv(ventaarticulo.getPrecio());
        articulosPedido.setPrecio(precio);
        articulosPedido.setReposicion(0);
        articulosPedido.setPedidos(selected);
        articulosPedido.setTipo(ventaarticulo.getTipo());
        selected.getArticulosPedidos().add(articulosPedido);
        articulos.remove(articuloElegido);
        articuloElegido = null;
    }

    public void agregarArticulo()
    {
        if(articuloElegido == null)
        return;
        Ventaarticulo ventaarticulo = ventaarticuloFacade.findByInvArticulo(articuloElegido);
        Double precio = ventaarticulo.getPrecio();
        if(personaElegida.getVentaclientes().size() >0)
        {
            for(Ventacliente ventacliente:personaElegida.getVentaclientes()){
                if(ventacliente.getInvArticulos().getInvArticulosPK().getCodArt().equals( articuloElegido.getInvArticulosPK().getCodArt()))
                {
                    precio = ventacliente.getPrecioespecial();
                }
            }
        }
        ArticulosPedido articulosPedido = new ArticulosPedido();
        articulosPedido.setInvArticulos(articuloElegido);
        articulosPedido.setPrecioInv(ventaarticulo.getPrecio());
        articulosPedido.setPrecio(precio);
        articulosPedido.setReposicion(0);
        articulosPedido.setPedidos(selected);
        articulosPedido.setTipo(ventaarticulo.getTipo());
        articulosPedido.setUnitCost(articuloElegido.getCostoUni());
        articulosPedido.setCu(articuloElegido.getCu());
        articulosPedidosElegidos.add(articulosPedido);
        /** articulos.remove(articuloElegido); **/ // repetir articulos
        articuloElegido = null;
    }

    public void reponerArticuloYQuitarArticulo(ArticulosPedido item){
        //selected.getArticulosPedidos().remove(item);
        articulosPedidosElegidos.remove(item);
        // articulosPedidoFacade.remove(item);
        item.setPedidos(null);
        articulos.add(item.getInvArticulos());
    }

    public void reponerArticuloYQuitarArticuloActualizar(ArticulosPedido item){
        selected.getArticulosPedidos().remove(item);
        articulosPedidoFacade.remove(item);
        item.setPedidos(null);
        articulos.add(item.getInvArticulos());
    }

    private PedidosFacade getFacade() {
        return pedidosFacade;
    }

    public Pedidos prepareCreate() {
        selected = new Pedidos();
        personaElegida = new Persona();
        personas = personasFacade.findAllClientesPersonaInstitucion();
        articulos = invArticulosFacade.findAllInvArticulos();
        conReposicion = false;
        initializeEmbeddableKey();
        return selected;
    }

    public void prepareEdit(Pedidos ped) {
        selected = getFacade().findById(ped.getIdpedidos());
        personas = personasFacade.findAllClientesPersonaInstitucion();
        articulos = invArticulosFacade.findAllInvArticulos();
        conReposicion = ped.getConReposicion();
        tieneReposicion = ped.getConReposicion();
        reposicionesYaAgregadas = ped.getConReposicion();
        personaElegida = ped.getCliente();
        if(ped.getConReposicion())
        {
            reposiciones.addAll(selected.getPedidosConReposicion());
        }
        for(ArticulosPedido articulosPedido:ped.getArticulosPedidos())
        {
            articulos.remove(articulosPedido.getInvArticulos());
        }
    }

    public void registrar() throws IOException, JRException {
        selected.setEstado("PENDIENTE");
        selected.setObservacion(this.observacion);
        create();
    }

    public void create() {
        selected.getArticulosPedidos().addAll(articulosPedidosElegidos);
        if(validarCampos())
        {
            return;
        }
        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "loginBean");

        selected.setUsuario(loginBean.getUsuario());
        selected.setCliente(personaElegida);
        selected.setCodigo(new BigInteger(getFacade().getSiguienteCodigo()));
        selected.setFechaPedido(new Date());
        selected.setTieneFactura(true);
        selected.setConReposicion(conReposicion);
        if(conReposicion) {
            selected.setPedidosConReposicion(reposiciones);
            selected.setReposicion(selected);
        }
        persist(PersistAction.CREATE, "El pedido se registro correctamente.");
        sfTmpencFacade.restarInventario(selected); /** Resta inv_inventario **/
        invArticulosFacade.updateArticleSubtractTotalCost(selected); /** Actualiza Costo Total en inv_articulos **/
        //invArticulosFacade.outputProducts(selected);

        actualizarReposiciones();
        if (!JSFUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
            selected = new Pedidos();
            personaElegida = new Persona();
            personas = personasFacade.findAllClientesPersonaInstitucion();
            articulos = invArticulosFacade.findAllInvArticulos();
            reposiciones = new ArrayList<>();
        }
        articulosPedidosElegidos.clear();
        setObservacion(null);
    }

    private boolean validarCampos() {
        Boolean error = false;
        if(personaElegida.getPiId() == null)
        {
            JSFUtil.addErrorMessage("El cliente es requerido");
            error = true;
        }
        if(selected.getFechaEntrega() == null)
        {
            JSFUtil.addErrorMessage( "La Fecha de entrega es requerida");
            error = true;
        }
        if(selected.getArticulosPedidos().size() == 0)
        {
            JSFUtil.addErrorMessage("Es necesario registrar al menos un artículo.");
            error = true;
        }
        if(selected.getArticulosPedidos().size() > 0)
        {
            for(ArticulosPedido articulosPedido:selected.getArticulosPedidos())
            {
                if(articulosPedido.getImporte() == 0.0 && articulosPedido.getReposicion() == 0.0){
                    JSFUtil.addErrorMessage("Eror: El "+articulosPedido.getInvArticulos().getDescri()+" tiene el importe en cero.");
                    error = true;
                }

            }
        }
        return error;
    }

    public void cancel(){
        articulosPedidosElegidos = new ArrayList<>();
        reposiciones = new ArrayList<>();
        selected = null;
        tieneReposicion = false;
    }

    public void update() {
        if(validarCampos())
        {
            return;
        }
        if(validarReposicion())
            return;

        selected.setConReposicion(conReposicion);
        //actualizarReposiciones();
        if(conReposicion) {
            selected.setPedidosConReposicion(reposiciones);
            selected.setReposicion(selected);
        }
        persist(PersistAction.UPDATE, "El pedido se actualizo correctamente");
        items = null;
    }
    
    public void generalUpdate() {      
        getFacade().edit(selected);
    }

    public void generalUpdate(Pedidos pedido) {
        getFacade().edit(pedido);
    }

    private boolean validarReposicion() {
        Boolean error = false;
        if(selected.getArticulosPedidos().size() > 0)
        {
            for(ArticulosPedido articulosPedido:selected.getArticulosPedidos())
            {
                if( articulosPedido.getPorReponer() > articulosPedido.getCantidad()){
                    JSFUtil.addErrorMessage("Eror: El "+articulosPedido.getInvArticulos().getDescri()+" tiene mayor a la reposición que la cantidad.");
                    error = true;
                } 
            }
        }
        return error;
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("PedidosDeleted"));
        if (!JSFUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void actualizarReposiciones()
    {
        for(Pedidos pedido:reposiciones)
        for(ArticulosPedido articulosPedido:pedido.getArticulosPedidos())
        {
            articulosPedidoController.setSelected(articulosPedido);
            articulosPedidoController.update();
        }
    }

    public void setItems(List<Pedidos> items) {
        this.items = items;
    }

    public List<Pedidos> getItems() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "loginBean");

        if (loginBean.getUsuario().getUsuario().equals("cisc"))
            items = getFacade().findPedidosFromCisc(fechaDesde, loginBean.getUsuario());
        else
            items = getFacade().findPedidosFrom(fechaDesde);

        return items;
    }

    public void onDateSelect(SelectEvent dateFrom) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "PEDIDOS MOSTRADOS DESDE: ", format.format(dateFrom.getObject())));
        fechaEntregaFiltro = null;
        pedidosFiltrado = null;
        pedidosElegidos = null;

    }

    public void actualizar(){
        fechaEntregaFiltro = null;
        pedidosFiltrado = null;
        pedidosElegidos = null; //*
        //items = getFacade().findPedidosOrdDecs();
        items = getFacade().findPedidosFrom(this.fechaDesde);

    }
    
    public String reinit() {
        articuloElegido = new InvArticulos();
        return null;
    }
    //TODO: en caso de anular un pedio con estado entregado de reponerse los articulos al almacen
    //analizar en los demas casos
    public void anularPedido(){
        if(validarAnulacion())
        {
            return;
        }
        //if(selected.getEstado().equals("CONTABILIZADO")){
            pedidosFacade.anularAsiento(selected.getAsiento());
            //pedidosFacade.anularAsiento(selected.getAsientocv());
            pedidosFacade.sumarInventario(selected); /** inv_inventario **/
            /** Actualiza Costo Sum **/
            invArticulosFacade.updateArticleSumTotalCost(selected);
        //}
        selected.setEstado("ANULADO");
        /*if(selected.getFlagstock())
            pedidosFacade.revertirStock(selected);*/
            
        persist(PersistAction.UPDATE, "El pedido fue anulado con exito.");
        //invArticulosFacade.inputProducts(selected); // inv_product

        items = null;
    }

    private boolean validarAnulacion() {
        Boolean error = false;
        if(StringUtils.isEmpty(selected.getObservacion())){
            JSFUtil.addErrorMessage( "Es necesario agregar una descripción.");
            error = true;
        }
        return error;
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

    public Pedidos getPedidos(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Pedidos> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Pedidos> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public List<String> getEtiquetas() {
        return etiquetas;
    }

    public void setEtiquetas(List<String> etiquetas) {
        this.etiquetas = etiquetas;
    }

    public String getEtiquetaSel() {
        return etiquetaSel;
    }

    public void setEtiquetaSel(String etiquetaSel) {
        this.etiquetaSel = etiquetaSel;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    @FacesConverter(forClass = Pedidos.class)
    public static class PedidosControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PedidosController controller = (PedidosController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "pedidosController");
            return controller.getPedidos(getKey(value));
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
            if (object instanceof Pedidos) {
                Pedidos o = (Pedidos) object;
                return getStringKey(o.getIdpedidos());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Pedidos.class.getName()});
                return null;
            }
        }

    }

    public Tipopedido getTipopedido() {
        return tipopedido;
    }

    public void setTipopedido(Tipopedido tipopedido) {
        this.tipopedido = tipopedido;
    }

    public List<ArticulosPedido> getArticulosPedidos() {
        return articulosPedidos;
    }

    public void setArticulosPedidos(List<ArticulosPedido> articulosPedidos) {
        this.articulosPedidos = articulosPedidos;
    }

    public List<ArticulosPedido> getArticulosPedidosElegidos() {
        return articulosPedidosElegidos;
    }

    public void setArticulosPedidosElegidos(List<ArticulosPedido> articulosPedidosElegidos) {
        this.articulosPedidosElegidos = articulosPedidosElegidos;
    }

    public List<InvArticulos> getArticulos() {
        return articulos;
    }

    public void setArticulos(List<InvArticulos> articulos) {
        this.articulos = articulos;
    }

    public Integer getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(Integer importeTotal) {
        this.importeTotal = importeTotal;
    }

    public InvArticulos getArticuloElegido() {
        return articuloElegido;
    }

    public void setArticuloElegido(InvArticulos articuloElegido) {
        this.articuloElegido = articuloElegido;
    }

    public List<Pedidos> getPedidosFiltrado() {
        return pedidosFiltrado;
    }

    public void setPedidosFiltrado(List<Pedidos> pedidosFiltrado) {
        this.pedidosFiltrado = pedidosFiltrado;
    }

    public Date getFechaEntregaFiltro() {
        return fechaEntregaFiltro;
    }

    public void setFechaEntregaFiltro(Date fechaEntregaFiltro) {
        this.fechaEntregaFiltro = fechaEntregaFiltro;
    }

    public Date getFechaPedidoFiltro() {
        return fechaPedidoFiltro;
    }

    public void setFechaPedidoFiltro(Date fechaPedidoFiltro) {
        this.fechaPedidoFiltro = fechaPedidoFiltro;
    }

    public List<String> getEstados() {
        if(estados == null) {
            estados = new ArrayList<>();
            estados.add("PENDIENTE");
            estados.add("PREPARAR");
            estados.add("ENTREGADO");
            estados.add("ANULADO");
            estados.add("CONTABILIZADO");
        }
        return estados;
    }
    
    public List<String> getEstadosPedido() {
        if(estados == null) {
            estados = new ArrayList<>();
            estados.add("SI");
            estados.add("NO");
        }
        return estados;
    }

    public void setEstados(List<String> estados) {
        this.estados = estados;
    }

    public Persona getPersonaElegida() {
        return personaElegida;
    }

    public void setPersonaElegida(Persona personaElegida) {
        this.personaElegida = personaElegida;
    }

    /*public void setPersonaElegida(Persona personaElegida) {
        if(personaElegida == null)
            return;
        
        if(!personaElegida.getPiId().equals(this.personaElegida.getPiId()))
        {
            selected.getArticulosPedidos().clear();
            tieneReposicion = false;                
            conReposicion = false;
            reposicionesYaAgregadas= false;
        }else{
            return;
        }
        
        reposiciones.clear();
        reposiciones = getFacade().findReposicionesPorPersona(personaElegida);
        if(reposiciones.size() >0 && !conReposicion)
        {
            for(Pedidos pedidosConRepo:reposiciones)
            for(ArticulosPedido repo:pedidosConRepo.getArticulosPedidos())
            {
                if(repo.getEstado().equals("RECHAZADO")) {
                    ArticulosPedido articulo = new ArticulosPedido();
                    articulo.setPrecio(repo.getPrecio());
                    repo.setEstado("REPUESTO");
                    articulo.setReposicion(repo.getPorReponer());
                    articulo.setPedidos(selected);
                    articulo.setInvArticulos(repo.getInvArticulos());
                    articulo.setCantidad(0);
                    //selected.getArticulosPedidos().add(articulo);
                    articulosPedidosElegidos.add(articulo);
                    articulos.remove(repo.getInvArticulos());
                }
            }
            verificarArticulosRepetidos();
            tieneReposicion = true;
            conReposicion = true;
            reposicionesYaAgregadas = true;
        }
        this.personaElegida = personaElegida;
    }*/

    private void verificarArticulosRepetidos(){
        List<ArticulosPedido> resultado = new ArrayList<>();
        for(ArticulosPedido articulosPedido:articulosPedidosElegidos)
        {
            boolean band = true;
            for(ArticulosPedido resul:resultado)
            {
                if(articulosPedido.getInvArticulos().getInvArticulosPK().getCodArt().equals(resul.getInvArticulos().getInvArticulosPK().getCodArt()))
                {
                    resul.setReposicion(articulosPedido.getReposicion()+resul.getReposicion());
                    band = false;
                }
            }
            if(band)
            {
                resultado.add(articulosPedido);
            }
        }
       articulosPedidosElegidos.clear();
       articulosPedidosElegidos.addAll(resultado);
    }

    private List<ArticulosPedido> quitarRepetidos(List<ArticulosPedido> resultado,ArticulosPedido comparar){
        boolean band = true;
        for(ArticulosPedido articulosPedido:resultado)
        {
            if(articulosPedido.getInvArticulos().getInvArticulosPK().getCodArt().equals(comparar.getInvArticulos().getInvArticulosPK().getCodArt()))
            {
                articulosPedido.setCantidad(articulosPedido.getCantidad()+comparar.getCantidad());
                band = false;
            }
        }
        if(band)
        {
            resultado.add(comparar);
        }
        return resultado;
    }

    public void quitarReposicion(){
        for(Pedidos pedido:reposiciones){
            for(ArticulosPedido repo:pedido.getArticulosPedidos()){
                //selected.getArticulosPedidos().remove(repo);
                if(repo.getEstado().equals("REPUESTO"))
                repo.setEstado("RECHAZADO");
                articulos.add(repo.getInvArticulos());
                conReposicion = false;
                reposicionesYaAgregadas = false;
            }
        }
    }

    public void agregarReposicion(){

        for(Pedidos pedido:reposiciones)
        for(ArticulosPedido repo:pedido.getArticulosPedidos())
        {
                ArticulosPedido articulo = new ArticulosPedido();
                articulo.setPrecio(repo.getPrecio());
                if(repo.getEstado().equals("RECHAZADO"))
                repo.setEstado("REPUESTO");
                articulo.setReposicion(repo.getPorReponer());
                articulo.setPedidos(selected);
                articulo.setInvArticulos(repo.getInvArticulos());
                articulo.setCantidad(0);
                selected.getArticulosPedidos().add(articulo);
                reposicionesYaAgregadas = true;
        }
        conReposicion = true;
    }

    public Boolean getConReposicion() {
        return conReposicion;
    }

    public void setConReposicion(Boolean conReposicion) {
        if(conReposicion)
        {   if(!reposicionesYaAgregadas)
            agregarReposicion();
        }
        else
            quitarReposicion();
        this.conReposicion = conReposicion;
    }

    public Boolean getTieneReposicion() {
        return tieneReposicion;
    }

    public void setTieneReposicion(Boolean tieneReposicion) {
        this.tieneReposicion = tieneReposicion;
    }

    public List<Pedidos> getPedidosElegidos() {        
        return pedidosElegidos;
    }

    public void setPedidosElegidos(List<Pedidos> pedidosElegidos) {
        this.pedidosElegidos = pedidosElegidos;
    }

    public String formatoMoneda(Double valor){
        DecimalFormat df = new DecimalFormat("###,###.00");

        DecimalFormatSymbols custom=new DecimalFormatSymbols();
        custom.setDecimalSeparator('.');
        custom.setGroupingSeparator(',');
        df.setDecimalFormatSymbols(custom);
        return df.format(valor);
    }

    public Double getTotalimporte() {
        Double totalimporte = 0.0;
        if(articulosPedidosElegidos != null)
            for(ArticulosPedido articulosPedido:articulosPedidosElegidos)
            {
                totalimporte += articulosPedido.getImporte();
            }
        return totalimporte;
    }

    public Boolean isRoot() {

        Boolean result = false;

        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "loginBean");

        if (loginBean.getUsuario().getUsuario().equals("root"))
            result = true;

        return result;
    }

}
