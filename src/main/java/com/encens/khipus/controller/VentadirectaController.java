package com.encens.khipus.controller;



import com.encens.khipus.ejb.*;
import com.encens.khipus.model.*;
import com.encens.khipus.util.*;
import com.encens.khipus.util.JSFUtil.PersistAction;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("ventadirectaController")
@SessionScoped
public class VentadirectaController implements Serializable {

    @EJB
    private PersonasFacade personasFacade;
    @EJB
    private InvArticulosFacade invArticulosFacade;
    @EJB
    private com.encens.khipus.ejb.VentadirectaFacade ventadirectaFacade;
    @EJB
    SfConfencFacade sfConfencFacade;
    @EJB
    private VentaarticuloFacade ventaarticuloFacade;
    @EJB
    private PedidosFacade pedidosFacade;
    @Inject
    private PedidosReportController pedidosReportController;
    @Inject
    private SfTmpdetController sfTmpdetController;
    @EJB
    private SfTmpencFacade sfTmpencFacade;
    @EJB
    DosificacionFacade dosificacionFacade;
    @Inject
    DosificacionController dosificacionController;
    @Inject
    private SfTmpencController sfTmpencController;

    private BarcodeRenderer barcodeRenderer;
    private MoneyUtil moneyUtil = new MoneyUtil();

    private List<Ventadirecta> items = null;
    private List<Ventadirecta> ventasElegidas = new ArrayList<>();
    private Ventadirecta selected;
    private Date fechaVentaFiltro;
    private List<String> estados;
    private List<Persona> personas;
    private List<InvArticulos> articulos;
    private Persona personaElegida;
    private Integer importeTotal = 0;
    private InvArticulos articuloElegido;
    private List<Ventadirecta> ventaDirectaFiltrado;
    private List<Ventadirecta> ventaDirectaElegidos = new ArrayList<>();
    private byte[] nota;
    private byte[] factura;
    private String codNota;
    private ControlCode controlCode = null;

    private String messageStock = "...";
    private Boolean flagStock = false;

    public VentadirectaController() {
        System.out.println("************* VENTADIRECTA CONTROLLER ()...");
    }

    public Ventadirecta getSelected() {
        return selected;
    }

    public void setSelected(Ventadirecta selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private VentadirectaFacade getFacade() {
        return ventadirectaFacade;
    }

    public Ventadirecta prepareCreate() {
        selected = new Ventadirecta();
        personaElegida = new Persona();
        personas = personasFacade.findAllClientesPersonaInstitucion();
        articulos = invArticulosFacade.findAllInvArticulos();
        initializeEmbeddableKey();
        return selected;
    }

    public void prepareEdit(Ventadirecta ventadirecta) {
        selected = ventadirecta;
        personas = personasFacade.findAllClientesPersonaInstitucion();
        articulos = invArticulosFacade.findAllInvArticulos();
        personaElegida = ventadirecta.getCliente();

        for(ArticulosPedido articulosPedido:ventadirecta.getArticulosPedidos())
        {
            articulos.remove(articulosPedido.getInvArticulos());
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

    public void reponerArticulo(ArticulosPedido item){
        articulos.add(item.getInvArticulos());
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

    public void agregarArticulo()
    {
        if(articuloElegido == null)
            return;

        Ventaarticulo ventaarticulo = ventaarticuloFacade.findByInvArticulo(articuloElegido);

        if (ventaarticulo == null){
            messageStock = "NO TIENE PRECIO DE VENTA !";
            return;
        }

        /** ----------------- **/
        BigDecimal value = BigDecimal.ZERO;
        if (ventaarticulo != null){

            value = getInventoryStock(ventaarticulo.getInvArticulos().getProductItemCode());

            if (value.doubleValue() <= 0.0 && ventaarticulo.getInvArticulos().getStockControl().equals("S")) {
                messageStock = "NO EXISTE SUFICIENTE INVENTARIO ! ! !";
                setFlagStock(false);
            }
            else {

                if (ventaarticulo.getInvArticulos().getStockControl().equals("N"))
                    messageStock = "Sin control de Inventario...";
                else
                    messageStock = "Inventario con existencia...";

                setFlagStock(true);
            }
        }


        if (!flagStock) return;

        /** ----------------- **/


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
        articulosPedido.setPrecioInv(ventaarticulo.getPrecio()); //articulosPedido.setPrecioInv(precio);
        articulosPedido.setPrecio(precio);
        articulosPedido.setReposicion(0);
        articulosPedido.setVentadirecta(selected);
        articulosPedido.setTipo(ventaarticulo.getTipo());
        articulosPedido.setCu(articuloElegido.getCu());
        
        selected.getArticulosPedidos().add(articulosPedido);
        articulos.remove(articuloElegido);
        articuloElegido = new InvArticulos();


    }

    public ActionListener createActionListener() throws IOException, JRException {
        //registrar();
        return new ActionListener() {
            @Override
            public void processAction(ActionEvent event) throws AbortProcessingException {
                try {
                    pedidosReportController.imprimirNotaEntrega(selected);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JRException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public void registrarImprimirNota() throws IOException, JRException {
        if(validarCampos())
        {
            return;
        }
        SfConfenc operacion= sfConfencFacade.getOperacion("VENTASINFACTURA");
        if(operacion == null)
        {
            JSFUtil.addErrorMessage("No se encuentra una operación registrada");
            return;
        }
        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "loginBean");
        selected.setUsuario(loginBean.getUsuario());
        selected.setCliente(personaElegida);
        selected.setCodigo(getFacade().getSiguienteNumeroVenta());
        selected.setEstado("PREPARAR");
        /*selected.setDocumento(pedidosReportController.generarNotaEntrega(selected));
        selected.setSucursal(loginBean.getUsuario().getSucursal());
        nota = selected.getDocumento();*/
        //codNota = "NOTA_"+selected.getCodigo().toString();
        String nomcliente = "";
        if(!StringUtils.isEmpty(selected.getCliente().getRazonsocial()))
            nomcliente = selected.getCliente().getRazonsocial();
        else
            nomcliente = selected.getCliente().getNombreCompleto();

        //crearAsientoNota(selected.getTotalimporte(), nomcliente,operacion);
        crearAsientoNota(selected, nomcliente,operacion);
        /** crearAsientoCostoVentas(selected, sfConfencFacade.getOperacion("COSTOVENTAS")); **/

        //selected.setDocumento(pedidosReportController.generarNotaEntrega(selected));
        selected.setSucursal(loginBean.getUsuario().getSucursal());
        //nota = selected.getDocumento();

        persist(PersistAction.CREATE, "La venta se registro correctamente.");

        sfTmpencFacade.restarInventario(selected); /** inv_inventario **/
        invArticulosFacade.updateArticleSubtractTotalCost(selected); /** Update inv_articulo **/
        //invArticulosFacade.outputProducts(selected);

        if (!JSFUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
            selected = new Ventadirecta();
            personaElegida = new Persona();
            personas = personasFacade.findAllClientesPersonaInstitucion();
            articulos = invArticulosFacade.findAllInvArticulos();
        }

    }

    //private void crearAsientoNota(Double totalimporte, String nomcliente, SfConfenc operacion) {
    private void crearAsientoNota(Ventadirecta ventadirecta, String nomcliente, SfConfenc operacion) {

        SfTmpenc sfTmpenc = new SfTmpenc();
        sfTmpenc.setNoCia("01");
        sfTmpenc.setFecha(new Date());
        String glosa = " " + ventadirecta.getCodigo().toString() + " " + nomcliente;
        sfTmpenc.setDescri(operacion.getGlosa() + glosa);
        sfTmpenc.setTipoDoc(operacion.getTipoDoc());
        sfTmpenc.setGlosa(operacion.getGlosa() + glosa);
        sfTmpenc.setEstado("PEN");
        sfTmpenc.setCliente(ventadirecta.getCliente());
        sfTmpenc.setDebe(ventadirecta.getTotalimporte());
        sfTmpenc.setNombreCliente(ventadirecta.getCliente().getNombreCompleto());
        sfTmpenc.setTipoDoc(operacion.getTipoDoc());
        sfTmpenc.setNoDoc(sfTmpencFacade.getSiguienteNumeroPorNombre(operacion.getTipoDoc()));
        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "loginBean");
        sfTmpenc.setUsuario(loginBean.getUsuario());
        String nroTrans = sfTmpencFacade.getSiguienteNumeroTransacccion();
        sfTmpenc.setNoTrans(nroTrans);

        List<SfConfdet> asientos = new ArrayList<>(operacion.getAsientos());
        //SfConfdet ventaProductos = asientos.get(0);
        SfConfdet cajaGeneral = asientos.get(0);

        //Asiento Caja General (Cta Activo)
        SfTmpdet asientoCajaGeneral = new SfTmpdet();
        asientoCajaGeneral.setNoCia("01");
        asientoCajaGeneral.setCuenta(cajaGeneral.getCuenta().getCuenta());
        asientoCajaGeneral.setNoTrans(nroTrans);
        setDebeOHaber(cajaGeneral, asientoCajaGeneral, ventadirecta.getTotalimporte());
        asientoCajaGeneral.setSfTmpenc(sfTmpenc);
        sfTmpenc.getAsientos().add(asientoCajaGeneral);

        // Asiento Venta de Productos (Cta Ingreso)
        SfConfdet ventaProductos = new SfConfdet();
        for ( ArticulosPedido articuloPedido : ventadirecta.getArticulosPedidos() ){
            ventaProductos = getConfDet(operacion, articuloPedido.getInvArticulos().getInvGrupos().getCuentaIngreso());
            break;
        }
        SfTmpdet asientoVentaProductos = new SfTmpdet();
        asientoVentaProductos.setNoCia("01");
    asientoVentaProductos.setCuenta(ventaProductos.getCuenta().getCuenta());
    asientoVentaProductos.setNoTrans(nroTrans);
    setDebeOHaber(ventaProductos, asientoVentaProductos, ventadirecta.getTotalimporte());
    asientoVentaProductos.setSfTmpenc(sfTmpenc);
    sfTmpenc.getAsientos().add(asientoVentaProductos);

    //sfTmpenc.getVentadirectas().add(ventadirecta);
    ventadirecta.setAsiento(sfTmpenc);

    //sfTmpencFacade.saveSFtmpenc(sfTmpenc); // quitar

}

    private void setDebeOHaber(SfConfdet ope,SfTmpdet asiento,Double monto){
        if(ope.getTipomovimiento().equals("DEBE")) {
            asiento.setDebe(new BigDecimal(monto));
            asiento.setHaber(BigDecimal.ZERO);
        }
        else {
            asiento.setHaber(new BigDecimal(monto));
            asiento.setDebe(BigDecimal.ZERO);
        }
    }

    /** TMP Para actualizar asientos CI **/
    public void actualizarAsiento(){
        System.out.println(".....Actualizando.....");
        sfTmpencFacade.actualizarCI("21248");
    }

    private void crearAsientoFactura(Double totalimporte, String nomcliente, Ventadirecta ventadirecta, SfConfenc operacion) {

        Double iva       = BigDecimalUtil.roundDouble(totalimporte * 0.13, 2);
        Double it        = BigDecimalUtil.roundDouble(totalimporte * 0.03, 2);
        Double importe87 = BigDecimalUtil.roundDouble(totalimporte * 0.87, 2);

        Double totalDebe  = totalimporte + it;
        Double totalHaber = importe87 + iva + it;

        /** Ajustar Diferencias **/
        importe87 = importe87 + (totalDebe - totalHaber);

        String glosa = " " + ventadirecta.getCodigo().toString() + " " + nomcliente;

        SfTmpenc sfTmpenc = new SfTmpenc();
        sfTmpenc.setAgregarCtaProv("SI");
        sfTmpenc.setNoCia("01");
        sfTmpenc.setFecha(ventadirecta.getFechaPedido());
        sfTmpenc.setDescri(operacion.getGlosa() + glosa);
        sfTmpenc.setTipoDoc(operacion.getTipoDoc());
        sfTmpenc.setFormulario(operacion.getTipoDoc());
        sfTmpenc.setGlosa(operacion.getGlosa() + glosa);
        sfTmpenc.setDescri(operacion.getGlosa() + glosa);
        sfTmpenc.setEstado("APR");
        sfTmpenc.setNoUsr("ADM");
        sfTmpenc.setCliente(ventadirecta.getCliente());

        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "loginBean");
        sfTmpenc.setUsuario(loginBean.getUsuario());

        //sfTmpenc.setCuenta(operacion.getCuenta().getCuenta());
        sfTmpenc.setNoDoc(sfTmpencFacade.getSiguienteNumeroPorNombre(operacion.getTipoDoc()));
        String nroTrans = sfTmpencFacade.getSiguienteNumeroTransacccion();
        sfTmpenc.setNoTrans(nroTrans);

        ///
        List<SfConfdet> asientos = new ArrayList<>(operacion.getAsientos());
        SfConfdet cajaGeneralMN = asientos.get(0);
        SfConfdet ctaITgasto = asientos.get(1);
        //SfConfdet ventaProductosILVA = asientos.get(1);
        //SfConfdet ventaProductosCISC = asientos.get(2);
        SfConfdet debitoFisical = asientos.get(5);
        SfConfdet impuestoTrasacciones = asientos.get(6);

        SfConfdet ventaProductos = new SfConfdet();
        for ( ArticulosPedido articuloPedido : ventadirecta.getArticulosPedidos() ){
            ventaProductos = getConfDet(operacion, articuloPedido.getInvArticulos().getInvGrupos().getCuentaIngreso());
            break;
        }

        ///
        SfTmpdet cajaGeneralMNAsiento = new SfTmpdet();
        operacion.getAsientos();
        cajaGeneralMNAsiento.setNoCia("01");
        cajaGeneralMNAsiento.setCuenta(cajaGeneralMN.getCuenta().getCuenta());
        setDebeOHaber(cajaGeneralMN, cajaGeneralMNAsiento, totalimporte);
        cajaGeneralMNAsiento.setNoTrans(nroTrans);
        cajaGeneralMNAsiento.setTc(new BigDecimal(1.0));
        cajaGeneralMNAsiento.setSfTmpenc(sfTmpenc);
        sfTmpenc.getAsientos().add(cajaGeneralMNAsiento);

        SfTmpdet itGastoAsiento = new SfTmpdet();
        operacion.getAsientos();
        itGastoAsiento.setNoCia("01");
        itGastoAsiento.setCuenta(ctaITgasto.getCuenta().getCuenta());
        setDebeOHaber(cajaGeneralMN, itGastoAsiento, it);
        itGastoAsiento.setNoTrans(nroTrans);
        itGastoAsiento.setTc(new BigDecimal(1.0));
        itGastoAsiento.setSfTmpenc(sfTmpenc);
        sfTmpenc.getAsientos().add(itGastoAsiento);

        SfTmpdet ventaDeProductosAsiento = new SfTmpdet();
        ventaDeProductosAsiento.setNoCia("01");
        ventaDeProductosAsiento.setCuenta(ventaProductos.getCuenta().getCuenta());
        ventaDeProductosAsiento.setNoTrans(nroTrans);
        setDebeOHaber(ventaProductos, ventaDeProductosAsiento, importe87);
        ventaDeProductosAsiento.setTc(new BigDecimal(1.0));
        ventaDeProductosAsiento.setSfTmpenc(sfTmpenc);
        sfTmpenc.getAsientos().add(ventaDeProductosAsiento);

        SfTmpdet debitoFisicalAsiento = new SfTmpdet();
        debitoFisicalAsiento.setNoCia("01");
        debitoFisicalAsiento.setCuenta(debitoFisical.getCuenta().getCuenta());
        debitoFisicalAsiento.setNoTrans(nroTrans);
        setDebeOHaber(debitoFisical, debitoFisicalAsiento, iva);
        debitoFisicalAsiento.setTc(new BigDecimal(1.0));
        debitoFisicalAsiento.setSfTmpenc(sfTmpenc);
        sfTmpenc.getAsientos().add(debitoFisicalAsiento);

        SfTmpdet impuestoTransaccionesAsiento = new SfTmpdet();
        impuestoTransaccionesAsiento.setNoCia("01");
        impuestoTransaccionesAsiento.setCuenta(impuestoTrasacciones.getCuenta().getCuenta());
        impuestoTransaccionesAsiento.setNoTrans(nroTrans);
        setDebeOHaber(impuestoTrasacciones, impuestoTransaccionesAsiento, it);
        impuestoTransaccionesAsiento.setTc(new BigDecimal(1.0));
        impuestoTransaccionesAsiento.setSfTmpenc(sfTmpenc);
        sfTmpenc.getAsientos().add(impuestoTransaccionesAsiento);

        //sfTmpenc.getVentadirectas().add(ventadirecta);
        ventadirecta.setAsiento(sfTmpenc);

        //sfTmpencFacade.saveSFtmpenc(sfTmpenc);          // quitar
        //sfTmpencFacade.mergeVentaContado(ventadirecta); // quitar
    }

    private void crearAsientoCostoVentas(Ventadirecta ventadirecta, SfConfenc operacion) {

        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "loginBean");

        SfTmpenc sfTmpenc1 = new SfTmpenc();
        sfTmpenc1.setAgregarCtaProv("SI");
        sfTmpenc1.setNoCia("01");
        sfTmpenc1.setFecha(ventadirecta.getFechaPedido());
        sfTmpenc1.setTipoDoc(operacion.getTipoDoc());
        sfTmpenc1.setFormulario(operacion.getTipoDoc());
        sfTmpenc1.setDescri(operacion.getGlosa() + " Nro " + ventadirecta.getCodigo() + " " + ventadirecta.getCliente().getRazonsocial());
        sfTmpenc1.setGlosa(operacion.getGlosa() + " Nro " + ventadirecta.getCodigo() + " " + ventadirecta.getCliente().getRazonsocial());
        sfTmpenc1.setEstado("PEN");
        sfTmpenc1.setNoUsr("ADM");
        sfTmpenc1.setCliente(ventadirecta.getCliente());
        sfTmpenc1.setUsuario(loginBean.getUsuario());

        sfTmpenc1.setNoDoc(sfTmpencFacade.getSiguienteNumeroPorNombre(operacion.getTipoDoc()));
        String nroTrans = sfTmpencFacade.getSiguienteNumeroTransacccion();
        sfTmpenc1.setNoTrans(nroTrans);

        SfConfdet confDetCosto = new SfConfdet();
        double totalCostoVentas = 0.0;
        for ( ArticulosPedido articuloPedido : ventadirecta.getArticulosPedidos() ){
            confDetCosto = getConfDet(operacion, articuloPedido.getInvArticulos().getCuentaArt());
            //totalCostoVentas = totalCostoVentas + (articuloPedido.getCantidad().doubleValue() * articuloPedido.getInvArticulos().getCostoUni());
            totalCostoVentas = totalCostoVentas + (articuloPedido.getCantidad().doubleValue() * articuloPedido.getInvArticulos().getCu());
        }

        SfTmpdet costoVentasAsiento = new SfTmpdet();
        costoVentasAsiento.setNoCia("01");
        costoVentasAsiento.setCuenta(confDetCosto.getCuenta().getCuenta());
        costoVentasAsiento.setNoTrans(nroTrans);
        setDebeOHaber(confDetCosto, costoVentasAsiento, totalCostoVentas );
        costoVentasAsiento.setTc(new BigDecimal(1.0));
        costoVentasAsiento.setSfTmpenc(sfTmpenc1);

        sfTmpenc1.getAsientos().add(costoVentasAsiento);

        SfConfdet cuentaAlm = new SfConfdet();
        for ( ArticulosPedido articuloPedido : ventadirecta.getArticulosPedidos() ){
            cuentaAlm = getConfDet(operacion, articuloPedido.getInvArticulos().getInvGrupos().getCuentaInv());
            break;
        }

        /** **/
        SfTmpdet costoInvAsiento = new SfTmpdet();
        costoInvAsiento.setNoCia("01");
        costoInvAsiento.setCuenta(cuentaAlm.getCuenta().getCuenta());
        costoInvAsiento.setNoTrans(nroTrans);
        setDebeOHaber(cuentaAlm, costoInvAsiento, totalCostoVentas);
        costoInvAsiento.setTc(new BigDecimal(1.0));
        costoInvAsiento.setSfTmpenc(sfTmpenc1);
        sfTmpenc1.getAsientos().add(costoInvAsiento);


        for ( ArticulosPedido articuloPedido : ventadirecta.getArticulosPedidos() ){
            double costoUniArticle = articuloPedido.getCantidad().doubleValue() * articuloPedido.getInvArticulos().getCostoUni();
            double cuArticle = articuloPedido.getCantidad().doubleValue() * articuloPedido.getInvArticulos().getCu() ;
            /*SfTmpdet costoInvAsiento = new SfTmpdet();
            costoInvAsiento.setNoCia("01");
            costoInvAsiento.setCuenta(cuentaAlm.getCuenta().getCuenta());
            costoInvAsiento.setNoTrans(nroTrans);
            setDebeOHaber(cuentaAlm, costoInvAsiento, cuArticle);
            costoInvAsiento.setTc(new BigDecimal(1.0));
            costoInvAsiento.setSfTmpenc(sfTmpenc1);
            sfTmpenc1.getAsientos().add(costoInvAsiento);*/

            /** Actualiza Costo **/
            invArticulosFacade.updateArticleSubtractTotalCost(articuloPedido.getInvArticulos().getProductItemCode(), cuArticle, costoUniArticle);

        }

        ventadirecta.setAsientocv(sfTmpenc1);
    }

    /*private void crearAsientoCostoVentas2(Ventadirecta ventadirecta, SfConfenc operacion) {

        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "loginBean");

        SfTmpenc sfTmpenc1 = new SfTmpenc();
        sfTmpenc1.setAgregarCtaProv("SI");
        sfTmpenc1.setNoCia("01");
        sfTmpenc1.setFecha(ventadirecta.getFechaPedido());
        sfTmpenc1.setDescri(operacion.getGlosa());
        sfTmpenc1.setTipoDoc(operacion.getTipoDoc());
        sfTmpenc1.setFormulario(operacion.getTipoDoc());
        sfTmpenc1.setGlosa(operacion.getGlosa() + ventadirecta.getCodigo() + " " + ventadirecta.getCliente().getRazonsocial());
        sfTmpenc1.setDescri(operacion.getGlosa() + ventadirecta.getCodigo() + " " + ventadirecta.getCliente().getRazonsocial());
        sfTmpenc1.setEstado("PEN");
        sfTmpenc1.setNoUsr("ADM");
        sfTmpenc1.setCliente(ventadirecta.getCliente());
        sfTmpenc1.setUsuario(loginBean.getUsuario());

        sfTmpenc1.setNoDoc(sfTmpencFacade.getSiguienteNumeroPorNombre(operacion.getTipoDoc()));
        String nroTrans = sfTmpencFacade.getSiguienteNumeroTransacccion();
        sfTmpenc1.setNoTrans(nroTrans);

        SfConfdet confDetCosto = new SfConfdet();
        double totalCostoVentas = 0.0;
        for ( ArticulosPedido articuloPedido : ventadirecta.getArticulosPedidos() ){
            confDetCosto = getConfDet(operacion, articuloPedido.getInvArticulos().getCuentaArt());
            totalCostoVentas = totalCostoVentas + (articuloPedido.getCantidad().doubleValue() * articuloPedido.getInvArticulos().getCostoUni());
        }

        SfTmpdet costoVentasAsiento = new SfTmpdet();
        costoVentasAsiento.setNoCia("01");
        costoVentasAsiento.setCuenta(confDetCosto.getCuenta().getCuenta());
        costoVentasAsiento.setNoTrans(nroTrans);
        setDebeOHaber(confDetCosto, costoVentasAsiento, totalCostoVentas );
        costoVentasAsiento.setTc(new BigDecimal(1.0));
        costoVentasAsiento.setSfTmpenc(sfTmpenc1);

        sfTmpenc1.getAsientos().add(costoVentasAsiento);

        SfConfdet cuentaAlm = new SfConfdet();
        for ( ArticulosPedido articuloPedido : ventadirecta.getArticulosPedidos() ){
            cuentaAlm = getConfDet(operacion, articuloPedido.getInvArticulos().getInvGrupos().getCuentaInv());
            break;
        }

        for ( ArticulosPedido articuloPedido : ventadirecta.getArticulosPedidos() ){
            double costoArt = articuloPedido.getCantidad().doubleValue() * articuloPedido.getInvArticulos().getCostoUni();
            SfTmpdet costoInvAsiento = new SfTmpdet();
            costoInvAsiento.setNoCia("01");
            costoInvAsiento.setCuenta(cuentaAlm.getCuenta().getCuenta());
            costoInvAsiento.setNoTrans(nroTrans);
            setDebeOHaber(cuentaAlm, costoInvAsiento, costoArt );
            costoInvAsiento.setTc(new BigDecimal(1.0));
            costoInvAsiento.setSfTmpenc(sfTmpenc1);
            sfTmpenc1.getAsientos().add(costoInvAsiento);
        }

        //sfTmpenc1.getVentadirectas().add(ventadirecta);
        selected.setAsientocv(sfTmpenc1);
        sfTmpencFacade.saveSFtmpenc(sfTmpenc1);
    }*/

    public SfConfdet getConfDet(SfConfenc operacion, String cuenta){
        SfConfdet result = null;
        for ( SfConfdet sfConfdet : operacion.getAsientos()){
            if (sfConfdet.getCuenta().getCuenta().equals(cuenta))
                result =  sfConfdet;
        }
        return  result;
    }

    public void registrarImprimirNotaFactura() throws IOException, JRException {
        if(validarCampos())
        {
            return;
        }
        SfConfenc operacion= sfConfencFacade.getOperacion("VENTACONFACTURA");
        if(operacion == null)
        {
            JSFUtil.addErrorMessage("No se encuentra una operación registrada");
            return;
        }
        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "loginBean");
        selected.setUsuario(loginBean.getUsuario());
        selected.setCliente(personaElegida);
        selected.setCodigo(getFacade().getSiguienteNumeroVenta());
        selected.setEstado("PREPARAR");
        //selected.setDocumento(pedidosReportController.generarFacturaNotaVentaDirecta(selected));
        selected.setDocumento(pedidosReportController.generarFacturaNotaVentaDirecta(selected, loginBean.getUsuario().getSucursal()));
        selected.setSucursal(loginBean.getUsuario().getSucursal());
        nota = selected.getDocumento();
        codNota = "NOTAFAC_"+selected.getCodigo().toString();
        String nomcliente = "";
        if(!StringUtils.isEmpty(selected.getCliente().getRazonsocial()))
            nomcliente = selected.getCliente().getRazonsocial();
        else
            nomcliente = selected.getCliente().getNombreCompleto();

        crearAsientoFactura(selected.getTotalimporte(), nomcliente, selected, operacion);
        persist(PersistAction.CREATE, "La venta se registro correctamente.");

        if (!JSFUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
            selected = new Ventadirecta();
            personaElegida = new Persona();
            personas = personasFacade.findAllClientesPersonaInstitucion();
            articulos = invArticulosFacade.findAllInvArticulos();
        }
    }

    public StreamedContent getNotaEntrega(){
        if(nota == null)
            return null;
        return new DefaultStreamedContent(new ByteArrayInputStream(nota),"application/pdf", codNota+".pdf");
    }

    public StreamedContent getNotaEntrega2(){
        if(factura == null)
            return null;
        return new DefaultStreamedContent(new ByteArrayInputStream(factura),"application/pdf", codNota+".pdf");
    }

    public void verImprimir() throws IOException, JRException {
        registrarImprimirNota();
        RequestContext.getCurrentInstance().openDialog("PdfDisplayRedirect");
    }

    public void create() {
        if(validarCampos())
        {
            return;
        }
        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "loginBean");
        selected.setUsuario(loginBean.getUsuario());
        selected.setCliente(personaElegida);
        selected.setCodigo(getFacade().getSiguienteNumeroVenta());
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("PedidosCreated"));
        if (!JSFUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
            selected = new Ventadirecta();
            personaElegida = new Persona();
            personas = personasFacade.findAllClientesPersonaInstitucion();
            articulos = invArticulosFacade.findAllInvArticulos();
        }

    }

    public boolean validarCampos() {
        Boolean error = false;
        if(personaElegida.getPiId() == null)
        {
            JSFUtil.addErrorMessage( ResourceBundle.getBundle("/Bundle").getString("ClienteRequerido"));
            error = true;
        }
        if(selected.getArticulosPedidos().size() == 0)
        {
            JSFUtil.addErrorMessage( ResourceBundle.getBundle("/Bundle").getString("ArticulosRequerido"));
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
        selected = null;
        nota = null;
    }

    public void anularVenta(){
        if(validarAnulacion())
        {
            return;
        }
        selected.setEstado("ANULADO");
        System.out.println("-----------> ANULAR VENTA 1: " + selected.getAsiento().getTipoDoc() + " - " + selected.getAsiento().getNoDoc());
        sfTmpencFacade.anularAsiento(selected.getAsiento());
        //sfTmpencFacade.anularAsiento(selected.getAsientocv());
        sfTmpencFacade.sumarInventario(selected); // inv_inventario

        invArticulosFacade.updateArticleSumTotalCost(selected);

        persist(PersistAction.UPDATE, "La venta fue anulada con exito.");
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

    public void generalUpdate() {
        getFacade().edit(selected);
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("VentadirectaUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("VentadirectaDeleted"));
        if (!JSFUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Ventadirecta> getItems() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "loginBean");
        System.out.println("LOGIN: " + loginBean.getUsuario().getIdusuario() + " " + loginBean.getUsuario().getUsuario());
        
        if (items == null) {
            items = getFacade().findItemsDesc(loginBean.getUsuario());
        }
        return items;
    }

    public void setItems(List<Ventadirecta> items) {
        this.items = items;
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

    public Ventadirecta getVentadirecta(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Ventadirecta> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Ventadirecta> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    /**
     * @return the controlCode
     */
    public ControlCode getControlCode() {
        return controlCode;
    }

    /**
     * @param controlCode the controlCode to set
     */
    public void setControlCode(ControlCode controlCode) {
        this.controlCode = controlCode;
    }

    public byte[] getFactura() {
        return factura;
    }

    public void setFactura(byte[] factura) {
        this.factura = factura;
    }

    public String getMessageInventory() {
        return messageInventory;
    }

    public void setMessageInventory(String messageInventory) {
        this.messageInventory = messageInventory;
    }

    public String getMessageStock() {
        return messageStock;
    }

    public void setMessageStock(String messageStock) {
        this.messageStock = messageStock;
    }

    public Boolean getFlagStock() {
        return flagStock;
    }

    public void setFlagStock(Boolean flagStock) {
        this.flagStock = flagStock;
    }

    @FacesConverter(forClass = Ventadirecta.class)
    public static class VentadirectaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            VentadirectaController controller = (VentadirectaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "ventadirectaController");
            return controller.getVentadirecta(getKey(value));
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
            if (object instanceof Ventadirecta) {
                Ventadirecta o = (Ventadirecta) object;
                return getStringKey(o.getIdventadirecta());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Ventadirecta.class.getName()});
                return null;
            }
        }

    }

    public List<String> getEstados() {
        if(estados == null) {
            estados = new ArrayList<>();
            estados.add("PENDIENTE");
            estados.add("PREPARAR");
            estados.add("ENTREGADO");
            estados.add("ANULADO");
        }
        return estados;
    }

    public void setEstados(List<String> estados) {
        this.estados = estados;
    }

    public List<Ventadirecta> getVentasElegidas() {
        return ventasElegidas;
    }

    public void setVentasElegidas(List<Ventadirecta> ventasElegidas) {
        this.ventasElegidas = ventasElegidas;
    }

    public List<Persona> getPersonas() {
        return personas;
    }

    public void setPersonas(List<Persona> personas) {
        this.personas = personas;
    }

    public List<InvArticulos> getArticulos() {
        return articulos;
    }

    public void setArticulos(List<InvArticulos> articulos) {
        this.articulos = articulos;
    }

    public Persona getPersonaElegida() {
        return personaElegida;
    }

    public void setPersonaElegida(Persona personaElegida) {
        this.personaElegida = personaElegida;
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

    public List<Ventadirecta> getVentaDirectaFiltrado() {
        return ventaDirectaFiltrado;
    }

    public void setVentaDirectaFiltrado(List<Ventadirecta> ventaDirectaFiltrado) {
        this.ventaDirectaFiltrado = ventaDirectaFiltrado;
    }

    public List<Ventadirecta> getVentaDirectaElegidos() {
        return ventaDirectaElegidos;
    }

    public void setVentaDirectaElegidos(List<Ventadirecta> ventaDirectaElegidos) {
        this.ventaDirectaElegidos = ventaDirectaElegidos;
    }

    public Date getFechaVentaFiltro() {
        return fechaVentaFiltro;
    }

    public void setFechaVentaFiltro(Date fechaVentaFiltro) {
        this.fechaVentaFiltro = fechaVentaFiltro;
    }

    public String getCodNota() {
        return codNota;
    }

    public void setCodNota(String codNota) {
        this.codNota = codNota;
    }

    public void obtenerDoc(Ventadirecta ventadirecta){
        this.factura = ventadirecta.getDocumento();
        this.codNota = ventadirecta.getCodigo().toString();
    }

    /**
     * Factura Especial venta contado
     */
    public void printFacturaEspecial(Ventadirecta ventadirecta) throws IOException, JRException {

        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "loginBean");

        //generarFacturaNotaVentaDirecta(ventadirecta, loginBean.getUsuario().getSucursal());
        Sucursal sucursal = loginBean.getUsuario().getSucursal();
        /***/
        Dosificacion dosificacion;
        barcodeRenderer = new BarcodeRenderer();
        dosificacion = dosificacionFacade.findByOffice(sucursal);
        Integer numeroFactura;
        moneyUtil = new MoneyUtil();
        BigInteger numberAuthorization = dosificacion.getNroautorizacion();
        String key = dosificacion.getLlave();
        if (ventadirecta.getMovimiento() == null) {
            numeroFactura = dosificacion.getNumeroactual();
            dosificacion.setNumeroactual(numeroFactura+1);

            ControlCode controlCode = generateCodControl(ventadirecta, numeroFactura, numberAuthorization, key,dosificacion.getNitEmpresa());
            JasperPrint jasperPrintNotaVenta = getJasperPrintNotaVenta(ventadirecta);
            JasperPrint jasperPrintFactura = getJasperPrintFacturaOriginalCopia(ventadirecta,controlCode,numeroFactura, dosificacion);
            jasperPrintNotaVenta.getPages().addAll(jasperPrintFactura.getPages());
            byte[] pdf = JasperExportManager.exportReportToPdf(jasperPrintNotaVenta);

            guardarMovimientoFactura(ventadirecta, controlCode, dosificacion);
            ventadirectaFacade.actualizarVentadirecta(ventadirecta);

        }

        /***/

        //ventadirecta.setDocumento(generarFacturaNotaVentaDirecta(ventadirecta, loginBean.getUsuario().getSucursal())); //**
        //nota = ventadirecta.getDocumento();

        //persist(PersistAction.CREATE, "La venta se registro correctamente.");

        //sfTmpencFacade.restarInventario(ventadirecta); /** inv_inventario **/
        //invArticulosFacade.updateArticleSubtractTotalCost(ventadirecta); /** Update inv_articulo **/
        //invArticulosFacade.outputProducts(ventadirecta); /** inv_product **/

        /*if (!JSFUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
            selected = new Ventadirecta();
            personaElegida = new Persona();
            personas = personasFacade.findAllClientesPersonaInstitucion();
            articulos = invArticulosFacade.findAllInvArticulos();
        }*/
    }

    /******************************************************************************************************************/
    public void registrarVentaFactura() throws IOException, JRException {
        if(validarCampos()) return;

        SfConfenc operacion = sfConfencFacade.getOperacion("VENTACONFACTURA");

        if(operacion == null){
            JSFUtil.addErrorMessage("No se encuentra una operación registrada");
            return;
        }

        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "loginBean");

        Ventadirecta ventadirecta = selected;
        ventadirecta.setUsuario(loginBean.getUsuario());
        ventadirecta.setCliente(personaElegida);
        ventadirecta.setCodigo(getFacade().getSiguienteNumeroVenta());
        ventadirecta.setEstado("PREPARAR");

        ventadirecta.setSucursal(loginBean.getUsuario().getSucursal());
        //nota = ventadirecta.getDocumento(
        // );
        codNota = "NOTAFAC_"+ventadirecta.getCodigo().toString();

        String nomcliente = "";
        if(!StringUtils.isEmpty(ventadirecta.getCliente().getRazonsocial()))
            nomcliente = ventadirecta.getCliente().getRazonsocial();
        else
            nomcliente = ventadirecta.getCliente().getNombreCompleto();

        crearAsientoFactura(ventadirecta.getTotalimporte(), nomcliente, ventadirecta, operacion);
        /** crearAsientoCostoVentas(ventadirecta, sfConfencFacade.getOperacion("COSTOVENTAS")); **/

         ventadirecta.setDocumento(generarFacturaNotaVentaDirecta(ventadirecta, loginBean.getUsuario().getSucursal())); //**  Descomentar
         nota = ventadirecta.getDocumento();

        persist(PersistAction.CREATE, "La venta se registro correctamente.");

        sfTmpencFacade.restarInventario(ventadirecta); /** inv_inventario **/
        invArticulosFacade.updateArticleSubtractTotalCost(ventadirecta); /** Update inv_articulo **/
        //invArticulosFacade.outputProducts(ventadirecta); /** inv_product **/

        if (!JSFUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
            selected = new Ventadirecta();
            personaElegida = new Persona();
            personas = personasFacade.findAllClientesPersonaInstitucion();
            articulos = invArticulosFacade.findAllInvArticulos();
        }
    }

    /** temp **/
    public void registrarCV(Ventadirecta ventadirecta){
        System.out.println(". . . .");
        System.out.println("...Registrando Costo Ventas: " + ventadirecta.getCodigo());
        //this.selected = ventadirecta;
        //crearAsientoCostoVentas2(ventadirecta, sfConfencFacade.getOperacion("COSTOVENTAS"));
        //persist(PersistAction.UPDATE, "CV OK");
    }

    public byte[] generarFacturaNotaVentaDirecta(Ventadirecta venta, Sucursal sucursal) throws IOException, JRException {
        Dosificacion dosificacion;
        barcodeRenderer = new BarcodeRenderer();
        dosificacion = dosificacionFacade.findByOffice(sucursal);
        Integer numeroFactura;
        moneyUtil = new MoneyUtil();
        BigInteger numberAuthorization = dosificacion.getNroautorizacion();
        String key = dosificacion.getLlave();
        if (venta.getMovimiento() == null) {
            numeroFactura = dosificacion.getNumeroactual();
            dosificacion.setNumeroactual(numeroFactura+1);
        }else{
            numeroFactura = venta.getMovimiento().getNrofactura();
        }
        ControlCode controlCode = generateCodControl(venta, numeroFactura, numberAuthorization, key,dosificacion.getNitEmpresa());
        JasperPrint jasperPrintNotaVenta = getJasperPrintNotaVenta(venta);
        JasperPrint jasperPrintFactura = getJasperPrintFacturaOriginalCopia(venta,controlCode,numeroFactura, dosificacion);
        jasperPrintNotaVenta.getPages().addAll(jasperPrintFactura.getPages());
        byte[] pdf = JasperExportManager.exportReportToPdf(jasperPrintNotaVenta);

        guardarMovimientoFactura(venta, controlCode, dosificacion);
        return pdf;
    }

    public void generarFacturaNotaVentaDirecta2(Ventadirecta venta, Sucursal sucursal) throws IOException, JRException {

        Dosificacion dosificacion;
        barcodeRenderer = new BarcodeRenderer();
        dosificacion = dosificacionFacade.findByOffice(sucursal);
        Integer numeroFactura;
        moneyUtil = new MoneyUtil();
        BigInteger numberAuthorization = dosificacion.getNroautorizacion();
        String key = dosificacion.getLlave();

        numeroFactura = dosificacion.getNumeroactual();
        dosificacion.setNumeroactual(numeroFactura+1);

        ControlCode controlCode = generateCodControl(venta, numeroFactura, numberAuthorization, key,dosificacion.getNitEmpresa());

        JasperPrint jasperPrintNotaVenta = getJasperPrintNotaVenta(venta);
        JasperPrint jasperPrintFactura = getJasperPrintFacturaOriginalCopia(venta,controlCode,numeroFactura, dosificacion);
        jasperPrintNotaVenta.getPages().addAll(jasperPrintFactura.getPages());
        //byte[] pdf = JasperExportManager.exportReportToPdf(jasperPrintNotaVenta);
        //guardarMovimientoFactura(venta, controlCode, dosificacion);
        System.out.println("......For export PDF....");
    }

    public void printFacturaNotaVentaDirecta(Ventadirecta ventadirecta) throws IOException, JRException {

        barcodeRenderer = new BarcodeRenderer();
        Movimiento mov = ventadirecta.getMovimiento();
        Dosificacion dosificacion = dosificacionFacade.findByAuthorization(mov.getNroAutorizacion());
        moneyUtil = new MoneyUtil();
        //BigInteger numberAuthorization = dosificacion.getNroautorizacion();
        //String key = dosificacion.getLlave();

        Integer numeroFactura = mov.getNrofactura();

        ControlCode controlCode = generateCodControl(ventadirecta, numeroFactura, dosificacion.getNroautorizacion(), dosificacion.getLlave(), dosificacion.getNitEmpresa());
        System.out.println("------------------------>printFacturaNotaVentaDirecta: " + ventadirecta);
        JasperPrint jasperPrintNotaVenta = getJasperPrintNotaVenta(ventadirecta);
        JasperPrint jasperPrintFactura = getJasperPrintFacturaOriginalCopia(ventadirecta,controlCode,numeroFactura, dosificacion);
        jasperPrintNotaVenta.getPages().addAll(jasperPrintFactura.getPages());
        //byte[] pdf = JasperExportManager.exportReportToPdf(jasperPrintNotaVenta);
        System.out.println("......For export PDF....22");
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        response.addHeader("Content-disposition", "attachment; filename=VentaContado.pdf");
        ServletOutputStream stream = response.getOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrintNotaVenta, stream);
        stream.flush();
        stream.close();
        FacesContext.getCurrentInstance().responseComplete();

    }


    /* test para exportar PDF directamente */
    public void generarFacturaNotaVenta(Ventadirecta venta, Sucursal sucursal) throws IOException, JRException {
        Dosificacion dosificacion;
        barcodeRenderer = new BarcodeRenderer();
        dosificacion = dosificacionFacade.findByOffice(sucursal);
        Integer numeroFactura;
        moneyUtil = new MoneyUtil();
        BigInteger numberAuthorization = dosificacion.getNroautorizacion();
        String key = dosificacion.getLlave();
        if (venta.getMovimiento() == null) {
            numeroFactura = dosificacion.getNumeroactual();
            dosificacion.setNumeroactual(numeroFactura+1);
        }else{
            numeroFactura = venta.getMovimiento().getNrofactura();
        }

        ControlCode controlCode = generateCodControl(venta, numeroFactura, numberAuthorization, key,dosificacion.getNitEmpresa());

        /*
        JasperPrint nota = generarNota(venta);
        JasperPrint facturaJasperPrint = generarFacturaOriginalCopia(venta,controlCode,numeroFactura, dosificacion);
        nota.getPages().addAll(facturaJasperPrint.getPages());
        byte[] pdf = JasperExportManager.exportReportToPdf(nota);
        */

        File jasperFile = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/reportes/factura.jasper"));
        HashMap parameters = new HashMap();

        String razonSocial = venta.getCliente().getRazonsocial();
        if (razonSocial.equals("") || razonSocial == null )
            razonSocial = venta.getCliente().getNom()+" "+venta.getCliente().getAp()+" "+venta.getCliente().getAm();

        parameters.putAll(getReportParamsFactura(razonSocial,
                                          numeroFactura,
                                          "ORIGINAL",
                                          controlCode.getCodigoControl(),
                                          controlCode.getKeyQR(),
                                          venta,
                                          dosificacion));
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperFile.getPath(),
                                                   parameters,
                                                   new JRBeanCollectionDataSource(venta.getArticulosPedidos()));

        /* Export PDF */
        /*HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        response.addHeader("Content-disposition", "attachment; filename=DOCUMENTOFACTU_ILVA.pdf");
        ServletOutputStream stream = response.getOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, stream);
        stream.flush();
        stream.close();
        FacesContext.getCurrentInstance().responseComplete();*/
        /* END Export PDF */
        guardarMovimientoFactura(venta, controlCode, dosificacion);
    }

    public JasperPrint getJasperPrintFacturaOriginalCopia(Ventadirecta venta,ControlCode controlCode,Integer numeroFactura, Dosificacion dosificacion) throws JRException {

        HashMap parameters = new HashMap();
        File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/reportes/factura.jasper"));

        String razonSocial = venta.getCliente().getRazonsocial();
        if (razonSocial.equals("") || razonSocial == null )
            razonSocial = venta.getCliente().getNom()+" "+venta.getCliente().getAp()+" "+venta.getCliente().getAm();

        razonSocial = razonSocial.toUpperCase();
        parameters.putAll(getReportParamsFactura(razonSocial, numeroFactura, "ORIGINAL", controlCode.getCodigoControl(), controlCode.getKeyQR(), venta, dosificacion));
        ////////////
        JasperPrint original = JasperFillManager.fillReport(jasper.getPath(), parameters, new JRBeanCollectionDataSource(venta.getArticulosPedidos()));
        HashMap parametersCopia = new HashMap();
        parametersCopia.putAll(getReportParamsFactura(razonSocial, numeroFactura, "COPIA", controlCode.getCodigoControl(), controlCode.getKeyQR(), venta, dosificacion));
        ////////////
        JasperPrint copia = JasperFillManager.fillReport(jasper.getPath(), parametersCopia, new JRBeanCollectionDataSource(venta.getArticulosPedidos()));
        original.getPages().addAll(copia.getPages());
        return original;
    }

    public JasperPrint getJasperPrintNotaVenta(Ventadirecta ventadirecta){
        HashMap parameters = new HashMap();
        moneyUtil = new MoneyUtil();
        File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/reportes/notaDeEntregaCI.jasper"));
        JasperPrint jasperPrint = new JasperPrint();
        parameters.putAll(getReportParamsVenta(ventadirecta));
        try {
            jasperPrint = JasperFillManager.fillReport(jasper.getPath(), parameters, new JRBeanCollectionDataSource(ventadirecta.getArticulosPedidos()));
        } catch (JRException e) {
            e.printStackTrace();
            //todo:mensaje de error
            return jasperPrint;
        }
        return jasperPrint;
    }

    private ControlCode generateCodControl(Ventadirecta venta, Integer numberInvoice, BigInteger numberAutorization, String key,String nitEmpresa) {
        Double importeBaseCreditFisical = venta.getTotalimporte();
        String nroDoc = venta.getCliente().getNroDoc();
        if(StringUtils.isNotEmpty(venta.getCliente().getNit()))
        {
            nroDoc = venta.getCliente().getNit();
        }
        venta.setImpuesto(venta.getTotalimporte() * 0.13);
        ControlCode controlCode = new ControlCode(nitEmpresa, numberInvoice, numberAutorization.toString(), venta.getFechaPedido(), venta.getTotalimporte(), importeBaseCreditFisical, nroDoc);
        moneyUtil.getLlaveQR(controlCode, key);
        controlCode.generarCodigoQR();
        return controlCode;
    }

    /**
     *
     * @param venta
     * @param controlCode
     * @param dosificacion
     */
    public void guardarMovimientoFactura(Ventadirecta venta, ControlCode controlCode, Dosificacion dosificacion){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "loginBean");
        if (venta.getMovimiento() == null) {
            Movimiento movimiento = new Movimiento();
            //movimiento.setCodestruct(dosificacion.getEstCod());
            movimiento.setEstado(InvoiceState.ACTIVE.getType());

            movimiento.setFecharegistro(new Date());
            //todo:virificar estos valores
            movimiento.setGlosa("");
            /*movimiento.setMoneda("BS");*/
            movimiento.setTipocambio(6.96);

            movimiento.setCodigoQR(controlCode.getKeyQR());
            movimiento.setFechaFactura(venta.getFechaPedido());
            movimiento.setNrofactura(controlCode.getNumberInvoice());
            movimiento.setNroAutorizacion(controlCode.getNumeroAutorizacion());

            String nitCliente = venta.getCliente().getNroDoc();
            if(StringUtils.isNotEmpty(venta.getCliente().getNit()))
                nitCliente = venta.getCliente().getNit();
            movimiento.setNitCliente(nitCliente);

            String rasonSocial = "";
            if(!StringUtils.isEmpty(venta.getCliente().getRazonsocial()))
                rasonSocial = venta.getCliente().getRazonsocial();
            else
                rasonSocial = venta.getCliente().getNombreCompleto();
            movimiento.setRazonSocial(rasonSocial.toUpperCase());
            movimiento.setImporteTotal(new BigDecimal(controlCode.getTotal()));
            movimiento.setImporteIceTasas(new BigDecimal(controlCode.getImporteICE()));
            movimiento.setExportExentas(new BigDecimal(0));
            movimiento.setVentasGrabTasaCero(new BigDecimal(controlCode.getImporteVentasGrabadas()));
            movimiento.setSubTotal(new BigDecimal(controlCode.getTotal())); //E = A - B - C - D
            movimiento.setDescuentos(new BigDecimal(Double.parseDouble(controlCode.getDescuentosBonificaciones())));
            movimiento.setImporteParaDebitoFiscal( new BigDecimal(controlCode.getTotal() - Double.parseDouble(controlCode.getDescuentosBonificaciones())));
            movimiento.setDebitoFiscal(new BigDecimal(movimiento.getImporteParaDebitoFiscal().doubleValue() * 0.13));
            movimiento.setCodigocontrol(controlCode.getCodigoControl());
            movimiento.setVentadirecta(venta);

            venta.setMovimiento(movimiento);

            Impresionfactura impresionfactura = new Impresionfactura();
            impresionfactura.setUsuario(loginBean.getUsuario());
            impresionfactura.setFechaimpresion(new Date());
            impresionfactura.setMovimiento(movimiento);
            //impresionfactura.setDosificacion(dosificacion);
            impresionfactura.setTipo("ORIGINAL");
            impresionfactura.setNroFactura(controlCode.getNumberInvoice());
            movimiento.getImpresionfacturaCollection().add(impresionfactura);
            venta.setMovimiento(movimiento);
            dosificacionController.setSelected(dosificacion);
            dosificacionController.update();
        }
    }

    /* GET REPORT PARAMETERS */
    private Map<String, Object> getReportParamsVenta(Ventadirecta ventadirecta) {
        String nroDoc = ventadirecta.getCliente().getNroDoc();
        if (ventadirecta.getCliente().getTipoPersona().equals("institucion")) {
            nroDoc = ventadirecta.getCliente().getNit();
        }

        Double totalPagar = ventadirecta.getTotalimporte();
        DecimalFormat df = new DecimalFormat("0.00");
        totalPagar = Double.parseDouble(df.format(totalPagar).replace(",", "."));

        Map<String, Object> paramMap = new HashMap<String, Object>();
        //paramMap.put("nroPedido", ventadirecta.getCodigo().toString());
        paramMap.put("nroPedido", ventadirecta.getAsiento().getNoDoc());
        paramMap.put("nit", nroDoc);
        paramMap.put("fechaEntrega", ventadirecta.getFechaPedido());
        paramMap.put("nombreClienteyTerritorio", ventadirecta.getCliente().getNombreCompleto() + "(" + ventadirecta.getCliente().getTerritoriotrabajo().getNombre() + ")");
        paramMap.put("totalLiteral", moneyUtil.Convertir(totalPagar.toString(), true));
        paramMap.put("totalImporte", ventadirecta.getTotalimporte());
        paramMap.put("REPORT_LOCALE", new java.util.Locale("en", "US"));
        return paramMap;
    }

    private Map<String, Object> getReportParamsFactura(String nameClient, long numfac, String etiqueta, String codControl, String keyQR, Ventadirecta venta, Dosificacion dosificacion) {

        String filePath = FileCacheLoader.i.getPath("/resources/reportes/qr_inv.png");
        String nroDoc = venta.getCliente().getNroDoc();
        DateUtil dateUtil = new DateUtil();
        if(StringUtils.isNotEmpty(venta.getCliente().getNit()))
        {
            nroDoc = venta.getCliente().getNit();
        }
        //todo:completar los datos de la tabla
        Calendar cal = Calendar.getInstance();
        cal.setTime(venta.getFechaPedido());
        int anio = cal.get(Calendar.YEAR);
        int mes = cal.get(Calendar.MONTH);
        int dia = cal.get(Calendar.DAY_OF_MONTH);
        /*if(venta.getMovimiento() == null)
            etiqueta = "ORIGINAL";*/

        String fecha = "Punata, "+dia+" de "+dateUtil.getMes(mes)+" de "+anio;
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("nitEmpresa", dosificacion.getNitEmpresa());
        paramMap.put("numFac", numfac);
        paramMap.put("numAutorizacion", dosificacion.getNroautorizacion().toString());
        //paramMap.put("numAutorizacion", venta.getMovimiento().getNroAutorizacion());
        paramMap.put("nitCliente", nroDoc);
        paramMap.put("fecha", fecha);
        paramMap.put("nombreCliente", nameClient);
        paramMap.put("fechaLimite", dosificacion.getFechavencimiento());
        paramMap.put("codigoControl", codControl);
        paramMap.put("tipoEtiqueta", etiqueta);
        paramMap.put("etiquetaEmpresa",dosificacion.getEtiquetaEmpresa());
        paramMap.put("etiquetaLey",dosificacion.getEtiquetaLey());
        //verificar por que no requiere el codigo de control
        paramMap.put("llaveQR", keyQR);
        paramMap.put("totalLiteral", moneyUtil.Convertir(venta.getTotalimporte().toString(), true));
        paramMap.put("total", venta.getTotalimporte());
        paramMap.put("valorComision", new Double("0"));
        paramMap.put("REPORT_LOCALE", new java.util.Locale("en", "US"));
        barcodeRenderer.generateQR(keyQR, filePath);
        try {
            BufferedImage img = ImageIO.read(new File(filePath));
            paramMap.put("imgQR", img);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return paramMap;
    }

    /* export pdf Venta*/
    public void exportarPDF(HashMap parametros, File jasper, Ventadirecta venta) throws JRException, IOException {

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasper.getPath(), parametros, new JRBeanCollectionDataSource(venta.getArticulosPedidos()));
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        response.addHeader("Content-disposition", "attachment; filename=DOCUMENTO_ILVA.pdf");
        ServletOutputStream stream = response.getOutputStream();

        JasperExportManager.exportReportToPdfStream(jasperPrint, stream);

        stream.flush();
        stream.close();
        FacesContext.getCurrentInstance().responseComplete();
    }

    public String getProductStock(String cod_art){
        String result = "";
        ProductInventory productInventory = invArticulosFacade.findProductInventoryByCode(cod_art);
        if (productInventory != null)
            result = productInventory.getQuantity().toString();

        return result;
    }

    // -- //

    public void contabilizar(){
        System.out.println(". . . CONTABILIZAR: " + this.ventaDirectaElegidos.size());

        for ( Ventadirecta ventadirecta:ventaDirectaElegidos ){

            if (ventadirecta.tieneFactura()){
                crearAsientoFactura(ventadirecta.getTotalimporte(), ventadirecta.getCliente().getNombreCompleto(), ventadirecta, sfConfencFacade.getOperacion("VENTACONFACTURA"));
                crearAsientoCostoVentas(ventadirecta, sfConfencFacade.getOperacion("COSTOVENTAS"));
            }else {
                crearAsientoNota(ventadirecta, ventadirecta.getCliente().getNombreCompleto(), sfConfencFacade.getOperacion("VENTASINFACTURA"));
                crearAsientoCostoVentas(ventadirecta, sfConfencFacade.getOperacion("COSTOVENTAS"));
            }

        }

    }

    public BigDecimal getInventoryStock(String cod_art){
        BigDecimal res = pedidosFacade.findInventoryByCode(cod_art);
        setMessageInventory(res.toString());
        return res;
    }

    private String messageInventory = "...";


    /**
     * -----------------------------------------------------------------------------------------------------------------
     * -----------------------------------------------------------------------------------------------------------------
     * Ariel Siles Encinas
     * -----------------------------------------------------------------------------------------------------------------
     * -----------------------------------------------------------------------------------------------------------------
     */

    public void registrarVentaDirectaCF() throws IOException, JRException {
        System.out.println("----------- REGISTRAR VENTA CF -----------");
        if(validarCampos()) return;
        SfConfenc operacion = sfConfencFacade.getOperacion("VENTACONFACTURA");

        if(operacion == null){
            JSFUtil.addErrorMessage("No se encuentra una operación registrada");
            return;
        }

        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "loginBean");

        Ventadirecta ventadirecta = selected;
        ventadirecta.setUsuario(loginBean.getUsuario());
        ventadirecta.setCliente(personaElegida);
        ventadirecta.setCodigo(getFacade().getSiguienteNumeroVenta());
        ventadirecta.setEstado("PREPARAR");
        ventadirecta.setSucursal(loginBean.getUsuario().getSucursal());

        //codNota = "NOTAFAC_" + ventadirecta.getCodigo().toString();

        String nomcliente = "";
        if(!StringUtils.isEmpty(ventadirecta.getCliente().getRazonsocial()))
            nomcliente = ventadirecta.getCliente().getRazonsocial();
        else
            nomcliente = ventadirecta.getCliente().getNombreCompleto();

        //crearAsientoFactura(ventadirecta.getTotalimporte(), nomcliente, ventadirecta, operacion);
        //ventadirecta.setDocumento(generarFacturaNotaVentaDirecta(ventadirecta, loginBean.getUsuario().getSucursal())); //***
        //generarFacturaNotaVentaDirecta2(ventadirecta, loginBean.getUsuario().getSucursal());
        //barcodeRenderer = new BarcodeRenderer();
        Dosificacion dosificacion    = dosificacionFacade.findByOffice(loginBean.getUsuario().getSucursal());
        //moneyUtil = new MoneyUtil();

        ControlCode controlCode = generateCodControl(
                ventadirecta,
                dosificacion.getNumeroactual(),
                dosificacion.getNroautorizacion(),
                dosificacion.getLlave(),
                dosificacion.getNitEmpresa());

        dosificacion.setNumeroactual(dosificacion.getNumeroactual()+1);
        guardarMovimientoFactura(ventadirecta, controlCode, dosificacion);
        crearAsientoFactura(ventadirecta.getTotalimporte(), nomcliente, ventadirecta, operacion);
        //nota = ventadirecta.getDocumento();
        persist(PersistAction.CREATE, "La venta se registro correctamente.");
        sfTmpencFacade.restarInventario(ventadirecta);                      // inv_inventario
        invArticulosFacade.updateArticleSubtractTotalCost(ventadirecta);    // Update inv_articulo
        //invArticulosFacade.outputProducts(ventadirecta);                    // inv_product

        if (!JSFUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
            selected = new Ventadirecta();
            personaElegida = new Persona();
            personas = personasFacade.findAllClientesPersonaInstitucion();
            articulos = invArticulosFacade.findAllInvArticulos();
        }

        //printFacturaNotaVentaDirecta(ventadirecta);
        System.out.println("............printFacturaNotaVentaDirecta(ventadirecta).......");

    }



}
