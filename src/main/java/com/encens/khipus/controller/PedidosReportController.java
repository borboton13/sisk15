/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.controller;

import com.encens.khipus.ejb.*;
import com.encens.khipus.model.*;
import com.encens.khipus.util.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.lang.StringUtils;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.text.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author Diego
 */
@Named(value = "pedidosReportController")
@SessionScoped
public class PedidosReportController implements Serializable {

    /**
     * Creates a new instance of PedidosReportController
     */
    @Inject
    GestorImpresion gestorImpresion;
    @EJB
    DosificacionFacade dosificacionFacade;
    @EJB
    SfConfencFacade sfConfencFacade;
    @EJB
    SfTmpencFacade sfTmpencFacade;
    @EJB
    private MovimientoFacade movimientoFacade;
    @EJB
    private InvArticulosFacade invArticulosFacade;
    @EJB
    private PedidosFacade pedidosFacade;
    @Inject
    PedidosController pedidosController;
    @Inject
    SfTmpencController sfTmpencController;
    @Inject
    VentadirectaController ventadirectaController;
    @Inject
    MovimientoController movimientoController;
    @Inject
    DosificacionController dosificacionController;
    @Inject
    ImpresionfacturaController impresionfacturaController;

    private MoneyUtil moneyUtil;
    private BarcodeRenderer barcodeRenderer;
    private Dosificacion dosificacion;
    private Pedidos pedido;
    private Ventadirecta ventadirecta;
    private List<Pedidos> pedidosElegidos = new ArrayList<>();
    private List<Ventadirecta> ventaDirectaElegidos = new ArrayList<>();
    private String tipoEtiquetaFactura = "ORIGINAL";
    private Boolean esCopia = false;
    private String erroresDeContabilizacion;

    public void imprimirNotaEntrega(Pedidos pedido) throws IOException, JRException {
        if(pedido.getEstado().equals("ANULADO"))
        {
            return;
        }
        this.pedido = pedido;
        HashMap parameters = new HashMap();
        moneyUtil = new MoneyUtil();
        parameters.putAll(getReportParams(pedido));
        File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/reportes/notaDeEntrega.jasper"));
        exportarPDF(parameters, jasper);
        if(pedido.getEstado().equals("PENDIENTE"))
        {
            pedido.setEstado("PREPARAR");
            pedidosController.setSelected(pedido);
            pedidosController.generalUpdate();
            pedidosController.setItems(null);
        }
        
    }

    private void contabilizarPedidoSinfactura(SfConfenc operacion, Pedidos pedido) {

        pedido.setContabilizado(true);
        pedido.setEstado("CONTABILIZADO");
        SfTmpenc sfTmpenc = new SfTmpenc();
        String nroTrans = sfTmpencFacade.getSiguienteNumeroTransacccion();
        sfTmpenc.setNoTrans(nroTrans);
        sfTmpenc.setDescri(operacion.getGlosa() + " " + pedido.getCodigo().toString() + " " + pedido.getCliente().getNombreCompleto());
        sfTmpenc.setGlosa(operacion.getGlosa() + " " + pedido.getCodigo().toString() + " " + pedido.getCliente().getNombreCompleto());
        sfTmpenc.setFecha(pedido.getFechaEntrega());
        sfTmpenc.setTipoDoc(operacion.getTipoDoc());
        sfTmpenc.setCliente(pedido.getCliente());
        sfTmpenc.setNombreCliente(pedido.getCliente().getNombreCompleto());
        sfTmpenc.setNoDoc(sfConfencFacade.getSiguienteNumeroDocumento(operacion.getTipoDoc()));
        sfTmpenc.setEstado("APR");

        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "loginBean");

        pedido.setSucursal(loginBean.getUsuario().getSucursal());
        sfTmpenc.setUsuario(loginBean.getUsuario());

        List<SfConfdet> asientos = new ArrayList<>(operacion.getAsientos());
        SfConfdet cuentasPorCobrar = asientos.get(0);
        SfConfdet ventaDeProductos = asientos.get(1);
        //SfConfdet ctaMerma = asientos.get(2);
        //SfConfdet ctaPromo = asientos.get(3);
        //SfConfdet ctaAlmPT = asientos.get(4);

        BigDecimal importeReposicion = calcularImporteReposicion(pedido);
        BigDecimal importePromocion  = calcularImportePromocion(pedido);

        Double totalImporte = pedido.getTotalimporte();

        ////
        SfTmpdet asientoCuentasPorCobrar = new SfTmpdet();
        asientoCuentasPorCobrar.setCuenta(cuentasPorCobrar.getCuenta().getCuenta());
        asientoCuentasPorCobrar.setNoTrans(nroTrans);
        setMonto(pedido, cuentasPorCobrar, asientoCuentasPorCobrar, false);
        setDebeOHaber(cuentasPorCobrar, asientoCuentasPorCobrar, totalImporte);
        asientoCuentasPorCobrar.setSfTmpenc(sfTmpenc);
        sfTmpenc.getAsientos().add(asientoCuentasPorCobrar);
        /////

        //////
        SfTmpdet asientoVentaDeProductos = new SfTmpdet();
        asientoVentaDeProductos.setCuenta(ventaDeProductos.getCuenta().getCuenta());
        asientoVentaDeProductos.setNoTrans(nroTrans);

        setDebeOHaber(ventaDeProductos, asientoVentaDeProductos, totalImporte);
        asientoVentaDeProductos.setSfTmpenc(sfTmpenc);
        sfTmpenc.getAsientos().add(asientoVentaDeProductos);

        /** Si existe Reposiciones (Mermas) **/
        /*if (importeReposicion.doubleValue() > 0){

            SfTmpdet asientoMerma = new SfTmpdet();
            asientoMerma.setCuenta(ctaMerma.getCuenta().getCuenta());
            asientoMerma.setNoTrans(nroTrans);
            setDebeOHaber(ctaMerma, asientoMerma, importeReposicion);
            asientoMerma.setSfTmpenc(sfTmpenc);
            sfTmpenc.getAsientos().add(asientoMerma);
        }*/
        /** Si existe Promocion **/
        /*if (importePromocion.doubleValue() > 0){

            SfTmpdet asientoPromo = new SfTmpdet();
            asientoPromo.setCuenta(ctaPromo.getCuenta().getCuenta());
            asientoPromo.setNoTrans(nroTrans);
            setDebeOHaber(ctaPromo, asientoPromo, importePromocion);
            asientoPromo.setSfTmpenc(sfTmpenc);
            sfTmpenc.getAsientos().add(asientoPromo);
        }*/
        /** Cta. Almacen **/
        /*if (importeReposicion.doubleValue() > 0 || importePromocion.doubleValue() > 0){
            SfTmpdet asientoCtaAlmPT = new SfTmpdet();
            asientoCtaAlmPT.setCuenta(ctaAlmPT.getCuenta().getCuenta());
            asientoCtaAlmPT.setNoTrans(nroTrans);
            setDebeOHaber(ctaAlmPT, asientoCtaAlmPT, BigDecimalUtil.sum(importeReposicion, importePromocion));
            asientoCtaAlmPT.setSfTmpenc(sfTmpenc);
            sfTmpenc.getAsientos().add(asientoCtaAlmPT);
        }*/

        sfTmpenc.getPedidos().add(pedido);
        pedido.setAsiento(sfTmpenc);

      /*  sfTmpencController.setSelected(sfTmpenc);
        sfTmpencController.createGeneral();*/

    }

    public void contabilzarPedidos(){
        if(pedidosElegidos.size() == 0){
            JSFUtil.addWarningMessage("No hay ningun pedido elegido.");
            return;
        }
        SfConfenc operacionPedidoConFactura = sfConfencFacade.getOperacion("PEDIDOCONFACTURA");

        if(operacionPedidoConFactura == null){
            JSFUtil.addWarningMessage("No se encuentra una operacion registrada para generar el pedido con factura\n");
            return;
        }
        SfConfenc operacionPedidoConFacturaComision = sfConfencFacade.getOperacion("PEDIDOCONFACTURACOMISION");
        if(operacionPedidoConFacturaComision == null){
            JSFUtil.addWarningMessage("No se encuentra una operacion registrada para generar el pedido con factura comision\n");
            return;
        }
        SfConfenc operacionPedidoSinFactura= sfConfencFacade.getOperacion("PEDIDOSINFACTURA");
        if(operacionPedidoSinFactura == null){
            JSFUtil.addWarningMessage("No se encuentra una operacion registrada para generar el pedido sin factura\n");
            return;
        }
        SfConfenc operacionPedidoDegRefRep = sfConfencFacade.getOperacion("DEG_REF_REP");
        SfConfenc operacionVentaCreditoVet = sfConfencFacade.getOperacion("VENTACREDITOVETERINARIO");

        boolean band = false;
        for(Pedidos pedidos:pedidosElegidos){
            if(pedidos.getTieneFactura() == null){
                JSFUtil.addWarningMessage("El pedido tiene datos inconsistentes contactese con el administrador "+pedidos.getCodigo()+"\n");
                band = true;
            }
        }
        if(band){
            return;
        }

        quitarAnulados();
        quitarContabilizados();

        boolean insert = true;

        for(Pedidos pedido:pedidosElegidos)
        {

            if (pedido.getIdtipopedido().getNombre().equals("DESC_LACTEOS") || pedido.getIdtipopedido().getNombre().equals("DESC_VETERINARIO")  )
                insert = pedidosFacade.insertarDescuento(pedido);

            if (insert) { // Se inserto el descuento
                if (pedido.getIdtipopedido().getNombre().equals("NORMAL") ||
                        pedido.getIdtipopedido().getNombre().equals("DESC_LACTEOS") ||
                        pedido.getIdtipopedido().getNombre().equals("DESC_VETERINARIO")) {

                    if (pedido.getTieneFactura()) {
                        if (pedido.getValorComision() > 0)
                            contabilizarPedidoConfacturaComision(operacionPedidoConFacturaComision, pedido);
                        else {
                            if (pedido.getUsuario().getUsuario().equals("cisc")) { /** MODIFYID **/
                                contabilizarPedidoConfacturaCisc(operacionVentaCreditoVet, pedido);

                            } else {
                                contabilizarPedidoConfactura(operacionPedidoConFactura, pedido);

                            }

                        }
                    } else
                        contabilizarPedidoSinfactura(operacionPedidoSinFactura, pedido);
                } else {
                    contabilizarPedidoDegRefRep(operacionPedidoDegRefRep, pedido); /** Contabiliza degustacion, refrigerio, reposicion **/
                }

                /** crearAsientoCostoVentas(pedido, sfConfencFacade.getOperacion("COSTOVENTAS")); **/
                pedidosController.generalUpdate(pedido);
                //if (pedido.getMovimiento() != null) pedidosController.generalUpdate(pedido.getMovimiento()); Error Duplica las NE

            }else
                break;
        }

        if (insert)
            JSFUtil.addWarningMessage("SE CONTABILIZO CON EXITO.");
        else
            JSFUtil.addErrorMessage("ERROR DESC, NO SE CONTABILIZO COMPLETO!");


        pedidosElegidos.clear();
    }


    public void contabilzarPedidosAgain(){

        SfConfenc operacionPedidoConFactura = sfConfencFacade.getOperacion("PEDIDOCONFACTURA");
        SfConfenc operacionPedidoConFacturaComision = sfConfencFacade.getOperacion("PEDIDOCONFACTURACOMISION");
        SfConfenc operacionPedidoSinFactura= sfConfencFacade.getOperacion("PEDIDOSINFACTURA");

        SfConfenc operacionPedidoDegRefRep = sfConfencFacade.getOperacion("DEG_REF_REP");
        SfConfenc operacionVentaCreditoVet = sfConfencFacade.getOperacion("VENTACREDITOVETERINARIO");

        boolean band = false;
        for(Pedidos pedidos:pedidosElegidos){
            if(pedidos.getTieneFactura() == null){
                JSFUtil.addWarningMessage("El pedido tiene datos inconsistentes contactese con el administrador "+pedidos.getCodigo()+"\n");
                band = true;
            }
        }
        if(band){
            return;
        }

        quitarAnulados();
        quitarContabilizados();

        List<Pedidos> pedidosAgain = pedidosFacade.findPedidosAgain();
        int p = 1;
        for(Pedidos pedido:pedidosAgain)
        {
            if(     pedido.getIdtipopedido().getNombre().equals("NORMAL") ||
                    pedido.getIdtipopedido().getNombre().equals("DESC_LACTEOS") ||
                    pedido.getIdtipopedido().getNombre().equals("DESC_VETERINARIO")  ) {

                if (pedido.getTieneFactura()) {
                    if (pedido.getValorComision() > 0)
                        contabilizarPedidoConfacturaComision(operacionPedidoConFacturaComision, pedido);
                    else {
                        if (pedido.getUsuario().getUsuario().equals("cisc")) /** todo **/
                            contabilizarPedidoConfacturaCisc(operacionVentaCreditoVet, pedido);
                        else
                            contabilizarPedidoConfactura(operacionPedidoConFactura, pedido);
                    }
                } else
                    contabilizarPedidoSinfactura(operacionPedidoSinFactura, pedido);
            } else{
                contabilizarPedidoDegRefRep(operacionPedidoDegRefRep, pedido); /** Contabiliza Degustacion, Refrigerio, Reposicion **/
            }

            pedido.setDescripcion("CONTAB");
            pedidosController.generalUpdate(pedido);
            System.out.println("Contabilizando AGAIN... " + p++);
        }
        pedidosAgain.clear();
        JSFUtil.addWarningMessage("SE CONTABILIZO CON EXITO.");
    }

    private void setMonto(Pedidos pedido,SfConfdet detConf,SfTmpdet asiento,Boolean conRepo)
    {   Double monto = 0.0;
        Double montoRepo = 0.0;
        Double valor = 0.0;
        for(ArticulosPedido articulosPedido:pedido.getArticulosPedidos()){
            if(articulosPedido.getReposicion() > 0)
                montoRepo +=  articulosPedido.getImporte();
            else
                monto +=  articulosPedido.getImporte();
        }

        if(conRepo)
        {
            valor = montoRepo;
        }else{
            valor = monto;
        }

        if(detConf.getTipomovimiento().equals("DEBE"))
        {
            asiento.setDebe(new BigDecimal(valor));
        }else {
            asiento.setHaber(new BigDecimal(valor));
        }
    }

    public void imprimirNotaEntrega(Ventadirecta venta) throws IOException, JRException {
        this.ventadirecta = venta;
        HashMap parameters = new HashMap();
        moneyUtil = new MoneyUtil();
        parameters.putAll(getReportParams(ventadirecta));
        File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/reportes/notaDeEntregaCI.jasper"));
        exportarPDF(parameters, jasper,venta);
    }

    public byte[] generarNotaEntrega(Ventadirecta venta) throws IOException, JRException {
        this.ventadirecta = venta;
        HashMap parameters = new HashMap();
        moneyUtil = new MoneyUtil();
        parameters.putAll(getReportParams(ventadirecta));
        File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/reportes/notaDeEntregaCI.jasper"));
        return exportarPDFToString(parameters, jasper,venta);
    }

    private Map<String, Object> getReportParams(Pedidos pedido) {
        String nroDoc = pedido.getCliente().getNroDoc();
        if (pedido.getCliente().getTipoPersona().equals("institucion")) {
            nroDoc = pedido.getCliente().getNit();
        }

        Movimiento movimiento = pedido.getMovimiento();
        String observacion = "";
        if (movimiento != null)
            observacion = "F." + movimiento.getNrofactura().toString();

        observacion += " " + pedido.getObservacion();

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("nroPedido", pedido.getCodigo().toString());
        paramMap.put("nit", nroDoc);
        paramMap.put("fechaEntrega", pedido.getFechaEntrega());
        paramMap.put("nombreClienteyTerritorio", pedido.getCliente().getNombreCompleto() + "(" + pedido.getCliente().getTerritoriotrabajo().getNombre() + ")");
        paramMap.put("totalImporte", pedido.getTotalimporte());
        paramMap.put("porcentajeComision", pedido.getPorcentajeComision());
        paramMap.put("valorComision", pedido.getValorComision());
        
        Double totalPagar = pedido.getTotalimporte()-pedido.getValorComision();
        DecimalFormat df = new DecimalFormat("0.00");
        totalPagar = Double.parseDouble(df.format(totalPagar).replace(",", "."));
        
        paramMap.put("totalLiteral", moneyUtil.Convertir( totalPagar.toString() , true));
        paramMap.put("totalPagar", totalPagar);
        paramMap.put("observacion", observacion);
        paramMap.put("REPORT_LOCALE", new java.util.Locale("en", "US"));
        
        return paramMap;
    }

    private Map<String, Object> getReportParams(Ventadirecta ventadirecta) {

        String observacion = "";
        if (ventadirecta.getMovimiento() != null)
            observacion = observacion + "F." + ventadirecta.getMovimiento().getNrofactura() + " ";

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
        paramMap.put("observacion", observacion);
        paramMap.put("REPORT_LOCALE", new java.util.Locale("en", "US"));
        return paramMap;
    }
    //return getReportParams(pedido.getCliente().getNombreCompleto(), numeroFactura, tipoEtiquetaFactura, controlCode.getCodigoControl(), controlCode.getKeyQR(), pedido);
    //return getReportParams(pedido, controlCode, dosifica);
    private Map<String, Object> getReportParams(Pedidos pedido, ControlCode controlCode, Dosificacion dosifica) {

        String filePath = FileCacheLoader.i.getPath("/resources/reportes/qr_inv.png");
        String nroDoc = pedido.getCliente().getNroDoc();
        DateUtil dateUtil = new DateUtil();
        if(StringUtils.isNotEmpty(pedido.getCliente().getNit()))
        {
            nroDoc = pedido.getCliente().getNit();
        }
        //todo:completar los datos de la tabla
        Calendar cal = Calendar.getInstance();
        cal.setTime(pedido.getFechaEntrega());
        int anio = cal.get(Calendar.YEAR);
        int mes = cal.get(Calendar.MONTH);
        int dia = cal.get(Calendar.DAY_OF_MONTH);
        if(pedido.getMovimiento() == null)
            tipoEtiquetaFactura = "ORIGINAL";
        
        String razonSocial = pedido.getCliente().getRazonsocial();
        if (razonSocial.equals("") || razonSocial == null )
            razonSocial = pedido.getCliente().getNom()+" "+pedido.getCliente().getAp()+" "+pedido.getCliente().getAm();

        String fecha = "Punata, "+dia+" de "+dateUtil.getMes(mes)+" de "+anio;
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("nitEmpresa", dosifica.getNitEmpresa());
        paramMap.put("numFac", controlCode.getNumberInvoice().longValue());
        paramMap.put("numAutorizacion", dosifica.getNroautorizacion().toString());
        paramMap.put("nitCliente", nroDoc);
        paramMap.put("fecha", fecha);
        paramMap.put("nombreCliente", razonSocial.toUpperCase());
        paramMap.put("fechaLimite", dosifica.getFechavencimiento());
        paramMap.put("codigoControl", controlCode.getCodigoControl());
        paramMap.put("tipoEtiqueta", tipoEtiquetaFactura);
        paramMap.put("etiquetaEmpresa",dosifica.getEtiquetaEmpresa());
        paramMap.put("etiquetaLey",dosifica.getEtiquetaLey());
        //verificar por que no requiere el codigo de control
        paramMap.put("llaveQR", controlCode.getKeyQR());
        
        Double totalPagar = pedido.getTotalimporte() - pedido.getValorComision();
        DecimalFormat df = new DecimalFormat("0.00");
        totalPagar = Double.parseDouble(df.format(totalPagar).replace(",", "."));
        paramMap.put("totalLiteral", moneyUtil.Convertir(totalPagar.toString(), true));
        paramMap.put("total", pedido.getTotalimporte());
        paramMap.put("valorComision", pedido.getValorComision());
        paramMap.put("REPORT_LOCALE", new java.util.Locale("en", "US"));
        barcodeRenderer.generateQR(controlCode.getKeyQR(), filePath);
        try {
            BufferedImage img = ImageIO.read(new File(filePath));
            paramMap.put("imgQR", img);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return paramMap;
    }
    /*
    private Map<String, Object> getReportParams(String nameClient, long numfac, String etiqueta, String codControl, String keyQR, Pedidos pedido) {

        String filePath = FileCacheLoader.i.getPath("/resources/reportes/qr_inv.png");
        String nroDoc = pedido.getCliente().getNroDoc();
        DateUtil dateUtil = new DateUtil();
        if(StringUtils.isNotEmpty(pedido.getCliente().getNit()))
        {
            nroDoc = pedido.getCliente().getNit();
        }
        //todo:completar los datos de la tabla
        Calendar cal = Calendar.getInstance();
        cal.setTime(pedido.getFechaEntrega());
        int anio = cal.get(Calendar.YEAR);
        int mes = cal.get(Calendar.MONTH);
        int dia = cal.get(Calendar.DAY_OF_MONTH);
        if(pedido.getMovimiento() == null)
        etiqueta = "ORIGINAL";

        String fecha = "Cochambamba, "+dia+" de "+dateUtil.getMes(mes)+" de "+anio;
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("nitEmpresa", dosificacion.getNitEmpresa());
        paramMap.put("numFac", numfac);
        paramMap.put("numAutorizacion", dosificacion.getNroautorizacion().intValue());
        paramMap.put("nitCliente", nroDoc);
        paramMap.put("fecha", fecha);
        paramMap.put("nombreCliente", nameClient);
        paramMap.put("fechaLimite", dosificacion.getFechavencimiento());
        paramMap.put("codigoControl", codControl);
        paramMap.put("tipoEtiqueta", etiqueta);
        paramMap.put("etiquetaEmpresa",dosificacion.getEtiquetaEmpresa());
        //verificar por que no requiere el codigo de control
        paramMap.put("llaveQR", keyQR);
        
        Double totalPagar = pedido.getTotalimporte() - pedido.getValorComision();
        DecimalFormat df = new DecimalFormat("0.00");
        totalPagar = Double.parseDouble(df.format(totalPagar).replace(",", "."));
        
        paramMap.put("totalLiteral", moneyUtil.Convertir(totalPagar.toString(), true));
        paramMap.put("total", pedido.getTotalimporte());
        paramMap.put("valorComision", pedido.getValorComision());
        paramMap.put("REPORT_LOCALE", new java.util.Locale("en", "US"));
        barcodeRenderer.generateQR(keyQR, filePath);
        try {
            BufferedImage img = ImageIO.read(new File(filePath));
            paramMap.put("imgQR", img);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return paramMap;
    }*/

    private Map<String, Object> getReportParams(String nameClient, long numfac, String etiqueta, String codControl, String keyQR, Ventadirecta venta) {

        String filePath = FileCacheLoader.i.getPath("/resources/reportes/qr_inv.png");
        String filePathLogo = FileCacheLoader.i.getPath("/resources/reportes/logo01.png");
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

        //Punata desde 12/01/2015 Fact 334
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

            BufferedImage imgLogo = ImageIO.read(new File(filePathLogo));
            paramMap.put("imgLogo", imgLogo);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return paramMap;
    }

    //Desde ImprimirFactura.xhtml
    /*public void imprimirFactura() throws IOException, JRException {
        dosificacion = dosificacionFacade.findByPeriodo(new Date());
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        if(dosificacion.getFechaControl().compareTo(pedido.getFechaEntrega()) != 0)
        {
            JSFUtil.addWarningMessage("No se puede generar la factura, por que la fecha de entrega " + df.format(pedido.getFechaEntrega()) + "es diferente a " + df.format(dosificacion.getFechaControl()));
            return;
        }
        if(pedido == null)
        {
            JSFUtil.addWarningMessage("No hay ningun pedido elegido.");
            return;
        }

        if(pedido.getEstado().equals("ANULADO"))
        {
            JSFUtil.addWarningMessage("El pedido fuÃƒÆ’Ã‚Â© anulado.");
            return;
        }

        HashMap parameters = new HashMap();
        moneyUtil = new MoneyUtil();
        barcodeRenderer = new BarcodeRenderer();
        dosificacion = dosificacionFacade.findByPeriodo(new Date());
        BigInteger numberAuthorization = dosificacion.getNroautorizacion();
        String key = dosificacion.getLlave();
        File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/reportes/factura.jasper"));
        Integer numeroFactura;
        if (pedido.getMovimiento() == null) {
            numeroFactura = Integer.parseInt(dosificacionFacade.getSiguienteNumeroFactura());
            dosificacion.setNumeroactual(numeroFactura);
        }else{
            numeroFactura = pedido.getMovimiento().getNrofactura();
        }

        
        ControlCode controlCode = generateCodControl(pedido, numeroFactura, numberAuthorization, key,dosificacion.getNitEmpresa());
        parameters.putAll(
                getReportParams(
                        pedido.getCliente().getNombreCompleto(), numeroFactura, tipoEtiquetaFactura, controlCode.getCodigoControl(), controlCode.getKeyQR(), pedido));

        guardarFactura(pedido, controlCode.getCodigoControl(),controlCode.getKeyQR());
        exportarPDF(parameters, jasper);
    }*/

    /** With DOUBLE X **/
    private void setDebeOHaber(SfConfdet ope,SfTmpdet asiento, Double monto){
        if(ope.getTipomovimiento().equals("DEBE")) {
            asiento.setDebe(new BigDecimal(monto));
            asiento.setHaber(BigDecimal.ZERO);
        } else {
            asiento.setHaber(new BigDecimal(monto));
            asiento.setDebe(BigDecimal.ZERO);
        }
    }

    /** With BIGDECIMAL ok **/
    private void setDebeOHaber(SfConfdet ope,SfTmpdet asiento, BigDecimal monto){
        if(ope.getTipomovimiento().equals("DEBE")) {
            asiento.setDebe(monto);
            asiento.setHaber(BigDecimal.ZERO);
        } else {
            asiento.setHaber(monto);
            asiento.setDebe(BigDecimal.ZERO);
        }
    }

    private void contabilizarPedidoConfactura(SfConfenc operacion, Pedidos pedido) {
        pedido.setContabilizado(true);
        pedido.setEstado("CONTABILIZADO");
        SfTmpenc sfTmpenc = new SfTmpenc();
        String nroTrans = sfTmpencFacade.getSiguienteNumeroTransacccion();
        sfTmpenc.setNoTrans(nroTrans);
        sfTmpenc.setDescri(operacion.getGlosa() + " " + pedido.getCodigo().toString() + " " + pedido.getCliente().getNombreCompleto());
        sfTmpenc.setGlosa(operacion.getGlosa() + " " + pedido.getCodigo().toString() + " " + pedido.getCliente().getNombreCompleto());
        sfTmpenc.setFecha(pedido.getFechaEntrega());
        sfTmpenc.setTipoDoc(operacion.getTipoDoc());
        sfTmpenc.setCliente(pedido.getCliente());
        sfTmpenc.setNombreCliente(pedido.getCliente().getNombreCompleto());
        sfTmpenc.setNoDoc(sfConfencFacade.getSiguienteNumeroDocumento(operacion.getTipoDoc()));
        sfTmpenc.setEstado("APR");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "loginBean");
        sfTmpenc.setUsuario(loginBean.getUsuario());
        pedido.setSucursal(loginBean.getUsuario().getSucursal());

        List<SfConfdet> asientos = new ArrayList<>(operacion.getAsientos());
        SfConfdet cuentasPorCobrar = asientos.get(0);
        SfConfdet ctaITgasto       = asientos.get(1);
        SfConfdet ventaDeProductos = asientos.get(2);
        SfConfdet debitoFiscalIVA  = asientos.get(3);
        SfConfdet impuestoALasTransacciones = asientos.get(4);

        BigDecimal importeReposicion = calcularImporteReposicion(pedido);
        BigDecimal importePromocion  = calcularImportePromocion(pedido);

        BigDecimal IT_VALUE  = new BigDecimal("0.03");
        BigDecimal IVA_VALUE = new BigDecimal("0.13");

        BigDecimal totalImporte = BigDecimalUtil.toBigDecimal(pedido.getTotalimporte());

        BigDecimal iva = BigDecimalUtil.multiply(totalImporte, IVA_VALUE, 2);
        BigDecimal it = BigDecimalUtil.multiply(totalImporte, IT_VALUE, 2);

        BigDecimal importe87 = BigDecimalUtil.subtract(totalImporte, iva, 2);

        BigDecimal totalD = BigDecimalUtil.sum(totalImporte, it, 2);
        BigDecimal totalH = BigDecimalUtil.sum(importe87, iva, 2);
                   totalH = BigDecimalUtil.sum(totalH   , it , 2);

        /** Diferencia en totales **/
        if ( BigDecimalUtil.compareTo(totalD, totalH) != 0)
            importe87 = BigDecimalUtil.subtract(totalD, totalH, 2);

        /** 1. Clientes **/
        SfTmpdet asientoCuentasPorCobrar = new SfTmpdet();
        asientoCuentasPorCobrar.setCuenta(cuentasPorCobrar.getCuenta().getCuenta());
        asientoCuentasPorCobrar.setNoTrans(nroTrans);
        setDebeOHaber(cuentasPorCobrar, asientoCuentasPorCobrar, totalImporte);
        asientoCuentasPorCobrar.setSfTmpenc(sfTmpenc);
        asientoCuentasPorCobrar.setClient(pedido.getCliente());
        sfTmpenc.getAsientos().add(asientoCuentasPorCobrar);

        SfTmpdet itGastoAsiento = new SfTmpdet();
        itGastoAsiento.setCuenta(ctaITgasto.getCuenta().getCuenta());
        itGastoAsiento.setNoTrans(nroTrans);
        setDebeOHaber(cuentasPorCobrar, itGastoAsiento, it);
        itGastoAsiento.setSfTmpenc(sfTmpenc);
        sfTmpenc.getAsientos().add(itGastoAsiento);

        /////
        SfTmpdet asientoVentaDeProductos = new SfTmpdet();
        asientoVentaDeProductos.setCuenta(ventaDeProductos.getCuenta().getCuenta());
        asientoVentaDeProductos.setNoTrans(nroTrans);
        setDebeOHaber(ventaDeProductos, asientoVentaDeProductos, importe87);
        asientoVentaDeProductos.setSfTmpenc(sfTmpenc);
        sfTmpenc.getAsientos().add(asientoVentaDeProductos);
        ////
        SfTmpdet asientoIVA = new SfTmpdet();
        asientoIVA.setCuenta(debitoFiscalIVA.getCuenta().getCuenta());
        asientoIVA.setNoTrans(nroTrans);
        setDebeOHaber(debitoFiscalIVA, asientoIVA, iva);
        asientoIVA.setSfTmpenc(sfTmpenc);
        asientoIVA.setMovimiento(pedido.getMovimiento());
        sfTmpenc.getAsientos().add(asientoIVA);
        ////
        SfTmpdet asientoIT = new SfTmpdet();
        asientoIT.setCuenta(impuestoALasTransacciones.getCuenta().getCuenta());
        asientoIT.setNoTrans(nroTrans);
        setDebeOHaber(impuestoALasTransacciones, asientoIT, it);
        asientoIT.setSfTmpenc(sfTmpenc);
        sfTmpenc.getAsientos().add(asientoIT);
        sfTmpenc.getAsientos().add(asientoIVA);

        sfTmpenc.getPedidos().add(pedido);
        sfTmpenc.setMovimiento(pedido.getMovimiento());
        if (pedido.getMovimiento() != null) {
            sfTmpenc.setNrofactura(pedido.getMovimiento().getNrofactura());
            //pedido.getMovimiento().setSfTmpdet(asientoIVA); Err
        }
        pedido.setAsiento(sfTmpenc);
       /* sfTmpencController.setSelected(sfTmpenc);
        sfTmpencController.createGeneral();*/
    }

    private void contabilizarPedidoConfacturaCisc(SfConfenc operacion, Pedidos pedido) {
        pedido.setContabilizado(true);
        pedido.setEstado("CONTABILIZADO");
        SfTmpenc sfTmpenc = new SfTmpenc();
        String nroTrans = sfTmpencFacade.getSiguienteNumeroTransacccion();
        sfTmpenc.setNoTrans(nroTrans);
        sfTmpenc.setDescri(operacion.getGlosa() + " " + pedido.getCodigo().toString() + " " + pedido.getCliente().getNombreCompleto());
        sfTmpenc.setGlosa(operacion.getGlosa() + " " + pedido.getCodigo().toString() + " " + pedido.getCliente().getNombreCompleto());
        sfTmpenc.setFecha(pedido.getFechaEntrega());
        sfTmpenc.setTipoDoc(operacion.getTipoDoc());
        sfTmpenc.setCliente(pedido.getCliente());
        sfTmpenc.setNombreCliente(pedido.getCliente().getNombreCompleto());
        sfTmpenc.setNoDoc(sfConfencFacade.getSiguienteNumeroDocumento(operacion.getTipoDoc()));
        sfTmpenc.setEstado("APR");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "loginBean");
        sfTmpenc.setUsuario(loginBean.getUsuario());
        pedido.setSucursal(loginBean.getUsuario().getSucursal());

        List<SfConfdet> asientos = new ArrayList<>(operacion.getAsientos());

        SfConfdet cuentasPorCobrar = asientos.get(0);
        SfConfdet ctaITgasto       = asientos.get(1);
        SfConfdet ventaDeProductos = asientos.get(2);
        SfConfdet debitoFiscalIVA  = asientos.get(3);
        SfConfdet impuestoALasTransacciones = asientos.get(4);

        //SfConfdet ctaMerma = asientos.get(5);
        //SfConfdet ctaPromo = asientos.get(6);
        //SfConfdet ctaAlmPT = asientos.get(7);

        BigDecimal importeReposicion = calcularImporteReposicion(pedido);
        BigDecimal importePromocion  = calcularImportePromocion(pedido);

        BigDecimal IT_VALUE  = new BigDecimal("0.03");
        BigDecimal IVA_VALUE = new BigDecimal("0.13");

        BigDecimal totalImporte = BigDecimalUtil.toBigDecimal(pedido.getTotalimporte());

        BigDecimal iva = BigDecimalUtil.multiply(totalImporte, IVA_VALUE, 2);
        BigDecimal it = BigDecimalUtil.multiply(totalImporte, IT_VALUE, 2);

        BigDecimal importe87 = BigDecimalUtil.subtract(totalImporte, iva, 2);

        BigDecimal totalD = BigDecimalUtil.sum(totalImporte, it, 2);
        BigDecimal totalH = BigDecimalUtil.sum(importe87, iva, 2);
        totalH = BigDecimalUtil.sum(totalH   , it , 2);

        /** Diferencia en totales **/
        if ( BigDecimalUtil.compareTo(totalD, totalH) != 0)
            importe87 = BigDecimalUtil.subtract(totalD, totalH, 2);

        /** 1. Clientes **/
        SfTmpdet asientoCuentasPorCobrar = new SfTmpdet();
        asientoCuentasPorCobrar.setCuenta(cuentasPorCobrar.getCuenta().getCuenta());
        asientoCuentasPorCobrar.setNoTrans(nroTrans);
        setDebeOHaber(cuentasPorCobrar, asientoCuentasPorCobrar, totalImporte);
        asientoCuentasPorCobrar.setSfTmpenc(sfTmpenc);
        asientoCuentasPorCobrar.setClient(pedido.getCliente());
        sfTmpenc.getAsientos().add(asientoCuentasPorCobrar);

        SfTmpdet itGastoAsiento = new SfTmpdet();
        itGastoAsiento.setCuenta(ctaITgasto.getCuenta().getCuenta());
        itGastoAsiento.setNoTrans(nroTrans);
        setDebeOHaber(cuentasPorCobrar, itGastoAsiento, it);
        itGastoAsiento.setSfTmpenc(sfTmpenc);
        sfTmpenc.getAsientos().add(itGastoAsiento);

        /////
        SfTmpdet asientoVentaDeProductos = new SfTmpdet();
        asientoVentaDeProductos.setCuenta(ventaDeProductos.getCuenta().getCuenta());
        asientoVentaDeProductos.setNoTrans(nroTrans);
        setDebeOHaber(ventaDeProductos, asientoVentaDeProductos, importe87);
        asientoVentaDeProductos.setSfTmpenc(sfTmpenc);
        sfTmpenc.getAsientos().add(asientoVentaDeProductos);
        ////
        SfTmpdet asientoIVA = new SfTmpdet();
        asientoIVA.setCuenta(debitoFiscalIVA.getCuenta().getCuenta());
        asientoIVA.setNoTrans(nroTrans);
        setDebeOHaber(debitoFiscalIVA, asientoIVA, iva);
        asientoIVA.setSfTmpenc(sfTmpenc);
        sfTmpenc.getAsientos().add(asientoIVA);
        ////
        SfTmpdet asientoIT = new SfTmpdet();
        asientoIT.setCuenta(impuestoALasTransacciones.getCuenta().getCuenta());
        asientoIT.setNoTrans(nroTrans);
        setDebeOHaber(impuestoALasTransacciones, asientoIT, it);
        asientoIT.setSfTmpenc(sfTmpenc);
        sfTmpenc.getAsientos().add(asientoIT);
        sfTmpenc.getAsientos().add(asientoIVA);

        sfTmpenc.getPedidos().add(pedido);
        sfTmpenc.setMovimiento(pedido.getMovimiento());

        if (pedido.getMovimiento() != null) {
            sfTmpenc.setNrofactura(pedido.getMovimiento().getNrofactura());
            //pedido.getMovimiento().setSfTmpdet(asientoIVA); Error
        }

        pedido.setAsiento(sfTmpenc);
        pedido.setAsiento(sfTmpenc);

    }

    private BigDecimal calcularImporteReposicion(Pedidos pedido){

        BigDecimal importe = new BigDecimal(0);
        for (ArticulosPedido articulo : pedido.getArticulosPedidos()){

            importe = BigDecimalUtil.sum(importe, BigDecimalUtil.multiply(BigDecimalUtil.toBigDecimal(articulo.getReposicion()), BigDecimalUtil.toBigDecimal(articulo.getCu().toString(), 6)));
        }
        return importe;
    }

    private BigDecimal calcularImportePromocion(Pedidos pedido){

        BigDecimal importe = new BigDecimal(0);
        for (ArticulosPedido articulo : pedido.getArticulosPedidos()){

            importe = BigDecimalUtil.sum(importe, BigDecimalUtil.multiply(BigDecimalUtil.toBigDecimal(articulo.getPromocion()), BigDecimalUtil.toBigDecimal(articulo.getCu().toString(), 6)));
        }
        return importe;
    }
    
    private void contabilizarPedidoConfacturaComision(SfConfenc operacion,Pedidos pedido) {
        pedido.setContabilizado(true);
        pedido.setEstado("CONTABILIZADO");
        String nroTrans = sfTmpencFacade.getSiguienteNumeroTransacccion();

        SfTmpenc sfTmpenc = new SfTmpenc();
        sfTmpenc.setNoTrans(nroTrans);
        sfTmpenc.setDescri(operacion.getGlosa() + " " + pedido.getCodigo().toString() + " " + pedido.getCliente().getNombreCompleto());
        sfTmpenc.setGlosa(operacion.getGlosa() + " " + pedido.getCodigo().toString() + " " + pedido.getCliente().getNombreCompleto());
        sfTmpenc.setFecha(pedido.getFechaEntrega());
        sfTmpenc.setTipoDoc(operacion.getTipoDoc());
        sfTmpenc.setCliente(pedido.getCliente());
        sfTmpenc.setNombreCliente(pedido.getCliente().getNombreCompleto());
        sfTmpenc.setNoDoc(sfConfencFacade.getSiguienteNumeroDocumento(operacion.getTipoDoc()));
        sfTmpenc.setEstado("APR");

        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "loginBean");

        sfTmpenc.setUsuario(loginBean.getUsuario());
        pedido.setSucursal(loginBean.getUsuario().getSucursal());
        List<SfConfdet> asientos = new ArrayList<>(operacion.getAsientos());

        SfConfdet cuentaClientes = asientos.get(0);
        SfConfdet cuentaComisiones = asientos.get(1);
        SfConfdet cuentaCreditoFiscal = asientos.get(2);
        SfConfdet cuentaITgasto = asientos.get(3);
        SfConfdet ventaDeProductos = asientos.get(4);
        SfConfdet cuentaDebitoIVA = asientos.get(5);
        SfConfdet cuentaIT = asientos.get(6);

        SfConfdet ctaMerma = asientos.get(7);
        SfConfdet ctaPromo = asientos.get(8);
        SfConfdet ctaAlmPT = asientos.get(9);

        SfConfdet cuentaCreditoFiscalHaber = asientos.get(10);

        BigDecimal importeReposicion = calcularImporteReposicion(pedido);
        BigDecimal importePromocion = calcularImportePromocion(pedido);

        BigDecimal IT_VALUE = new BigDecimal("0.03");
        BigDecimal IVA_VALUE = new BigDecimal("0.13");

        BigDecimal subtotal = BigDecimalUtil.toBigDecimal(pedido.getTotalimporte());
        BigDecimal comision = BigDecimalUtil.toBigDecimal(pedido.getValorComision());
        BigDecimal totalPagarClientes = BigDecimalUtil.subtract(subtotal, comision, 2);

        //Double descuento87      = descuento * 0.87;
        //Double descuento13      = descuento * 0.13;

        BigDecimal comision13 = BigDecimalUtil.multiply(comision, IVA_VALUE, 2);
        BigDecimal comision87 = BigDecimalUtil.subtract(comision, comision13, 2);



        BigDecimal valorIVA = BigDecimalUtil.multiply(subtotal, IVA_VALUE, 2);
        BigDecimal valorIVAtotalPagar = BigDecimalUtil.multiply(totalPagarClientes, IVA_VALUE, 2);

        BigDecimal valorVentaProductos = BigDecimalUtil.subtract(subtotal, valorIVA, 2);  /** Ajuste Dif **/
        //BigDecimal valorIT             = BigDecimalUtil.multiply(totalPagar, IT_VALUE, 2);
        BigDecimal valorIT = BigDecimalUtil.multiply(subtotal, IT_VALUE, 2);

        BigDecimal totalD = BigDecimalUtil.sum(totalPagarClientes, comision87, comision13, valorIT);
        BigDecimal totalH = BigDecimalUtil.sum(valorVentaProductos, valorIVAtotalPagar, valorIT, comision13);

        /** Diferencia en totales **/
        if (totalH.doubleValue() > totalD.doubleValue()) {
            BigDecimal diff = BigDecimalUtil.subtract(totalH, totalD, 2);
            valorVentaProductos = BigDecimalUtil.subtract(valorVentaProductos, diff, 2);
        }

                /** 1. Clientes **/
        SfTmpdet asientoCuentasPorCobrar = new SfTmpdet();
        asientoCuentasPorCobrar.setCuenta(cuentaClientes.getCuenta().getCuenta());
        asientoCuentasPorCobrar.setNoTrans(nroTrans);
        setDebeOHaber(cuentaClientes, asientoCuentasPorCobrar, totalPagarClientes);
        asientoCuentasPorCobrar.setSfTmpenc(sfTmpenc);
        asientoCuentasPorCobrar.setClient(pedido.getCliente());
        sfTmpenc.getAsientos().add(asientoCuentasPorCobrar);

        /** 2. Comisiones s/ventas **/
        SfTmpdet asientoComision = new SfTmpdet();
        asientoComision.setCuenta(cuentaComisiones.getCuenta().getCuenta());
        asientoComision.setNoTrans(nroTrans);
        setDebeOHaber(cuentaComisiones, asientoComision, comision87);
        asientoComision.setSfTmpenc(sfTmpenc);
        sfTmpenc.getAsientos().add(asientoComision);

        /** 3. Credito Fiscal **/
        SfTmpdet asientoCreditoFiscal = new SfTmpdet();
        asientoCreditoFiscal.setCuenta(cuentaCreditoFiscal.getCuenta().getCuenta());
        asientoCreditoFiscal.setNoTrans(nroTrans);
        setDebeOHaber(cuentaCreditoFiscal, asientoCreditoFiscal, comision13);
        asientoCreditoFiscal.setSfTmpenc(sfTmpenc);
        sfTmpenc.getAsientos().add(asientoCreditoFiscal);

        /** 4. IT Gasto **/
        SfTmpdet asientoITgasto = new SfTmpdet();
        asientoITgasto.setCuenta(cuentaITgasto.getCuenta().getCuenta());
        asientoITgasto.setNoTrans(nroTrans);
        setDebeOHaber(cuentaITgasto, asientoITgasto, valorIT);
        asientoITgasto.setSfTmpenc(sfTmpenc);
        sfTmpenc.getAsientos().add(asientoITgasto);

        /** 5. Venta de Productos **/
        SfTmpdet asientoVentaDeProductos = new SfTmpdet();
        asientoVentaDeProductos.setCuenta(ventaDeProductos.getCuenta().getCuenta());
        asientoVentaDeProductos.setNoTrans(nroTrans);
        setDebeOHaber(ventaDeProductos, asientoVentaDeProductos, valorVentaProductos);
        asientoVentaDeProductos.setSfTmpenc(sfTmpenc);
        sfTmpenc.getAsientos().add(asientoVentaDeProductos);

        /** 6. IVA **/
        SfTmpdet asientoIVA = new SfTmpdet();
        asientoIVA.setCuenta(cuentaDebitoIVA.getCuenta().getCuenta());
        asientoIVA.setNoTrans(nroTrans);
        setDebeOHaber(cuentaDebitoIVA, asientoIVA, valorIVAtotalPagar);
        asientoIVA.setSfTmpenc(sfTmpenc);
        sfTmpenc.getAsientos().add(asientoIVA);

        /** 7. IT **/
        SfTmpdet asientoIT = new SfTmpdet();
        asientoIT.setCuenta(cuentaIT.getCuenta().getCuenta());
        asientoIT.setNoTrans(nroTrans);
        setDebeOHaber(cuentaIT, asientoIT, valorIT);
        asientoIT.setSfTmpenc(sfTmpenc);
        sfTmpenc.getAsientos().add(asientoIT);

        /** Si existe Reposiciones (Mermas) **/
        /*if (importeReposicion.doubleValue() > 0){

            SfTmpdet asientoMerma = new SfTmpdet();
            asientoMerma.setCuenta(ctaMerma.getCuenta().getCuenta());
            asientoMerma.setNoTrans(nroTrans);
            setDebeOHaber(ctaMerma, asientoMerma, importeReposicion);
            asientoMerma.setSfTmpenc(sfTmpenc);
            sfTmpenc.getAsientos().add(asientoMerma);
        }*/
        /** Si existe Promocion **/
        /*if (importePromocion.doubleValue() > 0){

            SfTmpdet asientoPromo = new SfTmpdet();
            asientoPromo.setCuenta(ctaPromo.getCuenta().getCuenta());
            asientoPromo.setNoTrans(nroTrans);
            setDebeOHaber(ctaPromo, asientoPromo, importePromocion);
            asientoPromo.setSfTmpenc(sfTmpenc);
            sfTmpenc.getAsientos().add(asientoPromo);
        }*/
        /** Cta. Almacen **/
        /*if (importeReposicion.doubleValue() > 0 || importePromocion.doubleValue() > 0){
            SfTmpdet asientoCtaAlmPT = new SfTmpdet();
            asientoCtaAlmPT.setCuenta(ctaAlmPT.getCuenta().getCuenta());
            asientoCtaAlmPT.setNoTrans(nroTrans);
            setDebeOHaber(ctaAlmPT, asientoCtaAlmPT, BigDecimalUtil.sum(importeReposicion, importePromocion));
            asientoCtaAlmPT.setSfTmpenc(sfTmpenc);
            sfTmpenc.getAsientos().add(asientoCtaAlmPT);
        }*/

        /** 11. Credito Fiscal 2 haber **/
        SfTmpdet asientoCreditoFiscalHaber = new SfTmpdet();
        asientoCreditoFiscalHaber.setCuenta(cuentaCreditoFiscalHaber.getCuenta().getCuenta());
        asientoCreditoFiscalHaber.setNoTrans(nroTrans);
        setDebeOHaber(cuentaCreditoFiscalHaber, asientoCreditoFiscalHaber, comision13);
        asientoCreditoFiscalHaber.setSfTmpenc(sfTmpenc);
        sfTmpenc.getAsientos().add(asientoCreditoFiscalHaber);

        sfTmpenc.getPedidos().add(pedido);
        pedido.setAsiento(sfTmpenc);

       /* sfTmpencController.setSelected(sfTmpenc);
        sfTmpencController.createGeneral();*/
    }

    private void contabilizarPedidoDegRefRep(SfConfenc operacion, Pedidos pedido) {
        pedido.setContabilizado(true);
        pedido.setEstado("CONTABILIZADO");
        /*
        SfTmpenc sfTmpenc = new SfTmpenc();
        String nroTrans = sfTmpencFacade.getSiguienteNumeroTransacccion();
        sfTmpenc.setNoTrans(nroTrans);
        sfTmpenc.setDescri(operacion.getGlosa() + " " + pedido.getCodigo().toString() + " " + pedido.getCliente().getNombreCompleto());
        sfTmpenc.setGlosa(operacion.getGlosa()  + " " + pedido.getCodigo().toString() + " " + pedido.getCliente().getNombreCompleto());
        sfTmpenc.setFecha(pedido.getFechaEntrega());
        sfTmpenc.setTipoDoc(operacion.getTipoDoc());
        sfTmpenc.setCliente(pedido.getCliente());
        sfTmpenc.setNombreCliente(pedido.getCliente().getNombreCompleto());
        sfTmpenc.setNoDoc(sfConfencFacade.getSiguienteNumeroDocumento(operacion.getTipoDoc()));
        sfTmpenc.setEstado("APR");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "loginBean");
        sfTmpenc.setUsuario(loginBean.getUsuario());
        pedido.setSucursal(loginBean.getUsuario().getSucursal());

        List<SfConfdet> asientos = new ArrayList<>(operacion.getAsientos());
        SfConfdet ctaDegustacion = asientos.get(0);
        SfConfdet ctaRefrigerio  = asientos.get(1);
        SfConfdet ctaMerma     = asientos.get(2);
        SfConfdet ctaAlmPT       = asientos.get(3);

        BigDecimal importeCostoDegRefRep = calcularImporteCostoDegRefRep(pedido);

        if (pedido.getIdtipopedido().getNombre().equals("DEGUSTACION")){
            SfTmpdet asiento = new SfTmpdet();
            asiento.setCuenta(ctaDegustacion.getCuenta().getCuenta());
            asiento.setNoTrans(nroTrans);
            setDebeOHaber(ctaDegustacion, asiento, importeCostoDegRefRep);
            asiento.setSfTmpenc(sfTmpenc);
            sfTmpenc.getAsientos().add(asiento);
        }

        if (pedido.getIdtipopedido().getNombre().equals("REFRIGERIO")){
            SfTmpdet asiento = new SfTmpdet();
            asiento.setCuenta(ctaRefrigerio.getCuenta().getCuenta());
            asiento.setNoTrans(nroTrans);
            setDebeOHaber(ctaRefrigerio, asiento, importeCostoDegRefRep);
            asiento.setSfTmpenc(sfTmpenc);
            sfTmpenc.getAsientos().add(asiento);
        }

        if (pedido.getIdtipopedido().getNombre().equals("REPOSICION")){
            SfTmpdet asiento = new SfTmpdet();
            asiento.setCuenta(ctaMerma.getCuenta().getCuenta());
            asiento.setNoTrans(nroTrans);
            setDebeOHaber(ctaMerma, asiento, importeCostoDegRefRep);
            asiento.setSfTmpenc(sfTmpenc);
            sfTmpenc.getAsientos().add(asiento);
        }

        *//** Cta. Almacen **//*
        SfTmpdet asientoCtaAlmPT = new SfTmpdet();
        asientoCtaAlmPT.setCuenta(ctaAlmPT.getCuenta().getCuenta());
        asientoCtaAlmPT.setNoTrans(nroTrans);
        setDebeOHaber(ctaAlmPT, asientoCtaAlmPT, importeCostoDegRefRep);
        asientoCtaAlmPT.setSfTmpenc(sfTmpenc);
        sfTmpenc.getAsientos().add(asientoCtaAlmPT);

        sfTmpenc.getPedidos().add(pedido);
        pedido.setAsiento(sfTmpenc);
        */

    }

    private BigDecimal calcularImporteCostoDegRefRep(Pedidos pedido){

        BigDecimal importe = new BigDecimal(0);
        for (ArticulosPedido articulo : pedido.getArticulosPedidos()){
            importe = BigDecimalUtil.sum(importe, BigDecimalUtil.multiply(BigDecimalUtil.toBigDecimal(articulo.getCantidad()), BigDecimalUtil.toBigDecimal(articulo.getCu().toString(), 6)));
        }
        return importe;
    }

    private BigDecimal calcularImporteDegustacionCosto(Pedidos pedido){

        BigDecimal importe = new BigDecimal(0);
        for (ArticulosPedido articulo : pedido.getArticulosPedidos()){
            importe = BigDecimalUtil.sum(importe, BigDecimalUtil.multiply(BigDecimalUtil.toBigDecimal(articulo.getCantidad()), BigDecimalUtil.toBigDecimal(articulo.getCu().toString(), 6)));
        }
        return importe;
    }

    private BigDecimal calcularImporteRefrigerioCosto(Pedidos pedido){

        BigDecimal importe = new BigDecimal(0);
        for (ArticulosPedido articulo : pedido.getArticulosPedidos()){
            importe = BigDecimalUtil.sum(importe, BigDecimalUtil.multiply(BigDecimalUtil.toBigDecimal(articulo.getCantidad()), BigDecimalUtil.toBigDecimal(articulo.getCu().toString(), 6)));
        }
        return importe;
    }

    private BigDecimal calcularImporteReposicionCosto(Pedidos pedido){

        BigDecimal importe = new BigDecimal(0);
        for (ArticulosPedido articulo : pedido.getArticulosPedidos()){
            importe = BigDecimalUtil.sum(importe, BigDecimalUtil.multiply(BigDecimalUtil.toBigDecimal(articulo.getCantidad()), BigDecimalUtil.toBigDecimal(articulo.getCu().toString(), 6)));
        }
        return importe;
    }



    private void crearAsientoCostoVentas(Pedidos pedido, SfConfenc operacion) {

        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "loginBean");

        SfTmpenc sfTmpenc1 = new SfTmpenc();
        sfTmpenc1.setAgregarCtaProv("SI");
        sfTmpenc1.setNoCia("01");
        sfTmpenc1.setFecha(pedido.getFechaEntrega());
        sfTmpenc1.setTipoDoc(operacion.getTipoDoc());
        sfTmpenc1.setFormulario(operacion.getTipoDoc());
        sfTmpenc1.setDescri(operacion.getGlosa() + " Ped Nro " + pedido.getCodigo() + " " + pedido.getCliente().getNombreCompleto());
        sfTmpenc1.setGlosa(operacion.getGlosa() + " Ped Nro " + pedido.getCodigo() + " " + pedido.getCliente().getNombreCompleto());
        sfTmpenc1.setEstado("PEN");
        sfTmpenc1.setNoUsr("ADM");
        sfTmpenc1.setCliente(pedido.getCliente());
        sfTmpenc1.setUsuario(loginBean.getUsuario());

        sfTmpenc1.setNoDoc(sfTmpencFacade.getSiguienteNumeroPorNombre(operacion.getTipoDoc()));
        String nroTrans = sfTmpencFacade.getSiguienteNumeroTransacccion();
        sfTmpenc1.setNoTrans(nroTrans);

        SfConfdet confDetCosto = new SfConfdet();
        double totalCostoVentas = 0.0;
        for ( ArticulosPedido articuloPedido : pedido.getArticulosPedidos() ){
            confDetCosto = ventadirectaController.getConfDet(operacion, articuloPedido.getInvArticulos().getCuentaArt());
            //totalCostoVentas = totalCostoVentas + (articuloPedido.getCantidad().doubleValue() * articuloPedido.getInvArticulos().getCostoUni());
            totalCostoVentas = totalCostoVentas + (articuloPedido.getCantidad().doubleValue() * articuloPedido.getInvArticulos().getCu());
        }

        /** 1. Costo de Productos **/
        SfTmpdet costoVentasAsiento = new SfTmpdet();
        costoVentasAsiento.setNoCia("01");
        costoVentasAsiento.setCuenta(confDetCosto.getCuenta().getCuenta());
        costoVentasAsiento.setNoTrans(nroTrans);
        setDebeOHaber(confDetCosto, costoVentasAsiento, totalCostoVentas );
        costoVentasAsiento.setTc(new BigDecimal(1.0));
        costoVentasAsiento.setSfTmpenc(sfTmpenc1);

        sfTmpenc1.getAsientos().add(costoVentasAsiento);

        SfConfdet cuentaAlm = new SfConfdet();
        for ( ArticulosPedido articuloPedido : pedido.getArticulosPedidos() ){
            cuentaAlm = ventadirectaController.getConfDet(operacion, articuloPedido.getInvArticulos().getInvGrupos().getCuentaInv());
            break;
        }

        /** 2.- Cuenta de Almacen **/
        SfTmpdet costoInvAsiento = new SfTmpdet();
        costoInvAsiento.setNoCia("01");
        costoInvAsiento.setCuenta(cuentaAlm.getCuenta().getCuenta());
        costoInvAsiento.setNoTrans(nroTrans);
        setDebeOHaber(cuentaAlm, costoInvAsiento, totalCostoVentas );
        costoInvAsiento.setTc(new BigDecimal(1.0));
        costoInvAsiento.setSfTmpenc(sfTmpenc1);
        sfTmpenc1.getAsientos().add(costoInvAsiento);

        for ( ArticulosPedido articuloPedido : pedido.getArticulosPedidos() ){
            //double costoArt = articuloPedido.getCantidad().doubleValue() * articuloPedido.getInvArticulos().getCostoUni();
            double costoUniArticle = articuloPedido.getCantidad().doubleValue() * articuloPedido.getInvArticulos().getCostoUni();
            double cuArticle = articuloPedido.getCantidad().doubleValue() * articuloPedido.getInvArticulos().getCu() ;

            /*SfTmpdet costoInvAsiento = new SfTmpdet();
            costoInvAsiento.setNoCia("01");
            costoInvAsiento.setCuenta(cuentaAlm.getCuenta().getCuenta());
            costoInvAsiento.setNoTrans(nroTrans);
            setDebeOHaber(cuentaAlm, costoInvAsiento, cuArticle );
            costoInvAsiento.setTc(new BigDecimal(1.0));
            costoInvAsiento.setSfTmpenc(sfTmpenc1);
            sfTmpenc1.getAsientos().add(costoInvAsiento);*/

            /** Actualiza Costo **/
            invArticulosFacade.updateArticleSubtractTotalCost(articuloPedido.getInvArticulos().getProductItemCode(), cuArticle, costoUniArticle);

        }

        //sfTmpenc1.getVentadirectas().add(ventadirecta);
        pedido.setAsientocv(sfTmpenc1);
        sfTmpencFacade.saveSFtmpenc(sfTmpenc1);
    }

    public void reImprimirFacturaPedido() throws IOException, JRException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "loginBean");

        HashMap parameters = new HashMap();
        moneyUtil = new MoneyUtil();
        barcodeRenderer = new BarcodeRenderer();

        File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/reportes/factura.jasper"));

        Movimiento movimiento = pedido.getMovimiento();
        Dosificacion dosage = dosificacionFacade.findByAuthorization(movimiento.getNroAutorizacion());
        //ControlCode controlCode = generateCodControl(pedido, mov.getNrofactura(), new BigInteger(mov.getNroAutorizacion()), dosage.getLlave(), dosage.getNitEmpresa());
        ControlCode controlCode = recuperarCodControl(movimiento, dosage);
        Map<String, Object> paramMap = getParamMapReimpresion(controlCode, dosage, movimiento);
        /**/
        /*DateUtil dateUtil = new DateUtil();
        Calendar cal = Calendar.getInstance();
        cal.setTime(pedido.getMovimiento().getFechaFactura());
        int anio = cal.get(Calendar.YEAR);
        int mes = cal.get(Calendar.MONTH);
        int dia = cal.get(Calendar.DAY_OF_MONTH);
        String fecha = "Punata, "+dia+" de "+dateUtil.getMes(mes)+" de "+anio;*/

        /*Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("nitEmpresa",      dosage.getNitEmpresa());
        paramMap.put("numFac",          mov.getNrofactura().longValue());
        paramMap.put("numAutorizacion", mov.getNroAutorizacion());
        paramMap.put("nitCliente",      mov.getNitCliente());
        paramMap.put("fecha",           fecha);
        paramMap.put("nombreCliente",   mov.getRazonSocial());
        paramMap.put("fechaLimite",     dosage.getFechavencimiento());
        paramMap.put("codigoControl",   mov.getCodigocontrol());
        paramMap.put("tipoEtiqueta",    tipoEtiquetaFactura);
        paramMap.put("etiquetaEmpresa", dosage.getEtiquetaEmpresa());
        paramMap.put("etiquetaLey",     dosage.getEtiquetaLey());
        //verificar por que no requiere el codigo de control
        paramMap.put("llaveQR", controlCode.getKeyQR());
        paramMap.put("totalLiteral", moneyUtil.Convertir(mov.getImporteTotal().toString(), true));
        paramMap.put("total", mov.getImporteTotal().doubleValue());
        paramMap.put("valorComision", new Double("0"));
        paramMap.put("REPORT_LOCALE", new java.util.Locale("en", "US"));

        String filePath = FileCacheLoader.i.getPath("/resources/reportes/qr_inv.png");
        barcodeRenderer.generateQR(controlCode.getKeyQR(), filePath);
        try {
            BufferedImage img = ImageIO.read(new File(filePath));
            paramMap.put("imgQR", img);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        parameters.putAll(paramMap);
        /**/

        exportarPDF(parameters, jasper, pedido);
        this.pedido = null;
    }

    public Map<String, Object> getParamMapReimpresion(ControlCode controlCode, Dosificacion dosage, Movimiento mov){

        DateUtil dateUtil = new DateUtil();
        Calendar cal = Calendar.getInstance();
        cal.setTime(controlCode.getFechaEmision());
        int anio = cal.get(Calendar.YEAR);
        int mes = cal.get(Calendar.MONTH);
        int dia = cal.get(Calendar.DAY_OF_MONTH);
        String fecha = "Punata, "+dia+" de "+dateUtil.getMes(mes)+" de "+anio;

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("nitEmpresa",      dosage.getNitEmpresa());
        paramMap.put("numFac",          mov.getNrofactura().longValue());
        paramMap.put("numAutorizacion", mov.getNroAutorizacion());
        paramMap.put("nitCliente",      mov.getNitCliente());
        paramMap.put("fecha",           fecha);
        paramMap.put("nombreCliente",   mov.getRazonSocial());
        paramMap.put("fechaLimite",     dosage.getFechavencimiento());
        paramMap.put("codigoControl",   mov.getCodigocontrol());
        paramMap.put("tipoEtiqueta",    tipoEtiquetaFactura);
        paramMap.put("etiquetaEmpresa", dosage.getEtiquetaEmpresa());
        paramMap.put("etiquetaLey",     dosage.getEtiquetaLey());
        //verificar por que no requiere el codigo de control
        paramMap.put("llaveQR", controlCode.getKeyQR());
        paramMap.put("totalLiteral", moneyUtil.Convertir(mov.getImporteParaDebitoFiscal().toString(), true));
        paramMap.put("total", mov.getImporteTotal().doubleValue());
        paramMap.put("valorComision", mov.getDescuentos().doubleValue());
        paramMap.put("REPORT_LOCALE", new java.util.Locale("en", "US"));

        String filePath = FileCacheLoader.i.getPath("/resources/reportes/qr_inv.png");
        String filePathLogo = FileCacheLoader.i.getPath("/resources/img/ilva4.png");
        barcodeRenderer.generateQR(controlCode.getKeyQR(), filePath);
        try {
            BufferedImage img = ImageIO.read(new File(filePath));
            paramMap.put("imgQR", img);

            BufferedImage imgLogo = ImageIO.read(new File(filePathLogo));
            paramMap.put("imgLogo", imgLogo);

            //paramMap.put("imgLogo", this.getClass().getResourceAsStream("/resources/reportes/ilva3.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return paramMap;
    }

    public void reImprimirFacturaVentaContado() throws IOException, JRException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "loginBean");


        HashMap parameters = new HashMap();
        moneyUtil = new MoneyUtil();
        barcodeRenderer = new BarcodeRenderer();

        File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/reportes/factura.jasper"));

        Movimiento mov = ventadirecta.getMovimiento();
        Dosificacion dosage = dosificacionFacade.findByAuthorization(mov.getNroAutorizacion());
        ControlCode controlCode = generateCodControl(ventadirecta, mov.getNrofactura(), mov.getNroAutorizacion(), dosage.getLlave(), dosage.getNitEmpresa());

        /**/
        DateUtil dateUtil = new DateUtil();
        Calendar cal = Calendar.getInstance();
        cal.setTime(ventadirecta.getMovimiento().getFechaFactura());
        int anio = cal.get(Calendar.YEAR);
        int mes = cal.get(Calendar.MONTH);
        int dia = cal.get(Calendar.DAY_OF_MONTH);
        String fecha = "Punata, "+dia+" de "+dateUtil.getMes(mes)+" de "+anio;

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("nitEmpresa",      dosage.getNitEmpresa());
        paramMap.put("numFac",          mov.getNrofactura().longValue());
        paramMap.put("numAutorizacion", mov.getNroAutorizacion());
        paramMap.put("nitCliente",      mov.getNitCliente());
        paramMap.put("fecha",           fecha);
        paramMap.put("nombreCliente",   mov.getRazonSocial());
        paramMap.put("fechaLimite",     dosage.getFechavencimiento());
        paramMap.put("codigoControl",   mov.getCodigocontrol());
        paramMap.put("tipoEtiqueta",    tipoEtiquetaFactura);
        paramMap.put("etiquetaEmpresa", dosage.getEtiquetaEmpresa());
        paramMap.put("etiquetaLey",     dosage.getEtiquetaLey());
        //verificar por que no requiere el codigo de control
        paramMap.put("llaveQR", controlCode.getKeyQR());
        paramMap.put("totalLiteral", moneyUtil.Convertir(mov.getImporteTotal().toString(), true));
        paramMap.put("total", mov.getImporteTotal().doubleValue());
        paramMap.put("valorComision", new Double("0"));
        paramMap.put("REPORT_LOCALE", new java.util.Locale("en", "US"));

        String filePath = FileCacheLoader.i.getPath("/resources/reportes/qr_inv.png");
        barcodeRenderer.generateQR(controlCode.getKeyQR(), filePath);
        try {
            BufferedImage img = ImageIO.read(new File(filePath));
            paramMap.put("imgQR", img);
        } catch (IOException e) {
            e.printStackTrace();
        }

        parameters.putAll(paramMap);
        /**/

        exportarPDF(parameters, jasper,ventadirecta);
    }

    public void imprimirFacturaVentaDirecta() throws IOException, JRException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "loginBean");
        
        dosificacion = dosificacionFacade.findByOffice(loginBean.getUsuario().getSucursal());
        
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        /*if(dosificacion.getFechaControl().compareTo(ventadirecta.getFechaPedido()) != 0){
            JSFUtil.addWarningMessage("No se puede generar la factura, por que la fecha de entrega " + df.format(ventadirecta.getFechaPedido()) + "es diferente a " + df.format(dosificacion.getFechaControl()));
            return;
        }*/
        if(ventadirecta == null){
            JSFUtil.addWarningMessage("No hay ninguna venta elegida.");
            return;
        }

        if(ventadirecta.getEstado().equals("ANULADO")){
            JSFUtil.addWarningMessage("La venta fue anulado.");
            return;
        }

        HashMap parameters = new HashMap();
        moneyUtil = new MoneyUtil();
        barcodeRenderer = new BarcodeRenderer();
        //dosificacion = dosificacionFacade.findByPeriodo(new Date());
        //BigInteger numberAuthorization = dosificacion.getNroautorizacion();
        String numberAuthorization = dosificacion.getNroautorizacion().toString();
        String key = dosificacion.getLlave();
        File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/reportes/factura.jasper"));
        Integer numeroFactura;
        if (ventadirecta.getMovimiento() == null) {
            numeroFactura = Integer.parseInt(dosificacionFacade.getSiguienteNumeroFactura());
            dosificacion.setNumeroactual(numeroFactura);
        }else{
            numeroFactura = ventadirecta.getMovimiento().getNrofactura();
            numberAuthorization = ventadirecta.getMovimiento().getNroAutorizacion();
        }

        String razonSocial = ventadirecta.getCliente().getRazonsocial();
        if (razonSocial.equals("") || razonSocial == null )
            razonSocial = ventadirecta.getCliente().getNom()+" "+ventadirecta.getCliente().getAp()+" "+ventadirecta.getCliente().getAm();
                
        ControlCode controlCode = generateCodControl(ventadirecta, numeroFactura, numberAuthorization, key,dosificacion.getNitEmpresa());
        parameters.putAll(
                getReportParams(razonSocial.toUpperCase(), numeroFactura, tipoEtiquetaFactura, controlCode.getCodigoControl(), controlCode.getKeyQR(), ventadirecta));
        //guardarFactura(ventadirecta, controlCode.getCodigoControl(),controlCode.getKeyQR());
        guardarFactura(ventadirecta, controlCode);
        exportarPDF(parameters, jasper,ventadirecta);
    }

    public byte[] generarFacturaNotaVentaDirecta(Ventadirecta venta, Sucursal sucursal) throws IOException, JRException {
        barcodeRenderer = new BarcodeRenderer();
        //dosificacion = dosificacionFacade.findByPeriodo(new Date());
        dosificacion = dosificacionFacade.findByOffice(sucursal);
        Integer numeroFactura;
        moneyUtil = new MoneyUtil();
        BigInteger numberAuthorization = dosificacion.getNroautorizacion();
        String key = dosificacion.getLlave();
        if (venta.getMovimiento() == null) {
            //numeroFactura = Integer.parseInt(dosificacionFacade.getSiguienteNumeroFactura());
            //dosificacion.setNumeroactual(numeroFactura);
            numeroFactura = dosificacion.getNumeroactual();
            dosificacion.setNumeroactual(numeroFactura+1);
            //dosificacionFacade.updateNextNumber(sucursal);
        }else{
            numeroFactura = venta.getMovimiento().getNrofactura();
        }
        ControlCode controlCode = generateCodControl(venta, numeroFactura, numberAuthorization, key,dosificacion.getNitEmpresa());
        JasperPrint nota = generarNota(venta);
        JasperPrint factura = generarFacturaOriginalCopia(venta,controlCode,numeroFactura);
        nota.getPages().addAll(factura.getPages());
        byte[] pdf = JasperExportManager.exportReportToPdf(nota);
        //guardarFacturaGenerada(venta, controlCode.getCodigoControl());
        guardarMovimientoFactura(venta, controlCode);
        return pdf;
    }
    
    public void guardarMovimientoFactura(Ventadirecta venta, ControlCode controlCode){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "loginBean");
        if (venta.getMovimiento() == null) {
            Movimiento movimiento = new Movimiento();
            //movimiento.setCodestruct(dosificacion.getEstCod());
            movimiento.setEstado(InvoiceState.ACTIVE.getType());
            movimiento.setCodigocontrol(controlCode.getCodigoControl());
            movimiento.setFecharegistro(new Date());
            //todo:virificar estos valores
            movimiento.setGlosa("");
            /*movimiento.setMoneda("BS");*/
            movimiento.setTipocambio(6.96);
            movimiento.setNrofactura(controlCode.getNumberInvoice());
            movimiento.setCodigoQR(controlCode.getKeyQR());
            movimiento.setNroAutorizacion(controlCode.getNumeroAutorizacion());
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

    public JasperPrint generarNota(Ventadirecta ventadirecta){
        HashMap parameters = new HashMap();
        moneyUtil = new MoneyUtil();
        File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/reportes/notaDeEntregaCI.jasper"));
        JasperPrint jasperPrint = new JasperPrint();
        parameters.putAll(getReportParams(ventadirecta));
        try {
            jasperPrint = JasperFillManager.fillReport(jasper.getPath(), parameters, new JRBeanCollectionDataSource(ventadirecta.getArticulosPedidos()));
        } catch (JRException e) {
            e.printStackTrace();
            //todo:mensaje de error
            return jasperPrint;
        }
        return jasperPrint;
    }

    public JasperPrint generarFacturaOriginalCopia(Ventadirecta venta,ControlCode controlCode,Integer numeroFactura) throws JRException {        

        HashMap parameters = new HashMap();
        File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/reportes/factura.jasper"));
        
        String razonSocial = venta.getCliente().getRazonsocial();
        if (razonSocial.equals("") || razonSocial == null )
            razonSocial = venta.getCliente().getNom()+" "+venta.getCliente().getAp()+" "+venta.getCliente().getAm();
        
        parameters.putAll(
                getReportParams(razonSocial, numeroFactura, "ORIGINAL", controlCode.getCodigoControl(), controlCode.getKeyQR(), venta));
        ////////////
        JasperPrint original = JasperFillManager.fillReport(jasper.getPath(), parameters, new JRBeanCollectionDataSource(venta.getArticulosPedidos()));
        HashMap parametersCopia = new HashMap();
        parametersCopia.putAll(
                getReportParams(razonSocial, numeroFactura, "COPIA", controlCode.getCodigoControl(), controlCode.getKeyQR(), venta));
        ////////////
        JasperPrint copia= JasperFillManager.fillReport(jasper.getPath(), parametersCopia, new JRBeanCollectionDataSource(venta.getArticulosPedidos()));
        original.getPages().addAll(copia.getPages());
        return original;
    }

    public void guardarFactura(Ventadirecta venta, String codControl,String codQR) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "loginBean");
        if (venta.getMovimiento() == null) {
            Movimiento movimiento = new Movimiento();
            /*movimiento.setCodestruct(dosificacion.getEstCod());*/
            movimiento.setEstado(InvoiceState.ACTIVE.getType());
            movimiento.setCodigocontrol(codControl);
            movimiento.setFecharegistro(new Date());
            //todo:virificar estos valores
            movimiento.setGlosa("");
            /*movimiento.setMoneda("BS");*/
            movimiento.setTipocambio(6.69);
            movimiento.setNrofactura(dosificacion.getNumeroactual());
            movimiento.setCodigoQR(codQR);
            venta.setMovimiento(movimiento);

            Impresionfactura impresionfactura = new Impresionfactura();
            impresionfactura.setUsuario(loginBean.getUsuario());
            impresionfactura.setFechaimpresion(new Date());
            impresionfactura.setMovimiento(movimiento);
            impresionfactura.setDosificacion(dosificacion);
            impresionfactura.setTipo("ORIGINAL");
            impresionfactura.setNroFactura(dosificacion.getNumeroactual());
            movimiento.getImpresionfacturaCollection().add(impresionfactura);
            /*movimientoController.setSelected(movimiento);
             movimientoController.create();*/
            venta.setMovimiento(movimiento);
            if(venta.getEstado().equals("PENDIENTE"))
                venta.setEstado("PREPARAR");
            ventadirectaController.setItems(null);
            ventadirectaController.setSelected(venta);
            ventadirectaController.generalUpdate();
            // dosificacion.setNumeroactual(dosificacion.getNumeroactual().intValue() + 1);
            dosificacionController.setSelected(dosificacion);
            dosificacionController.update();
        } else {
            Impresionfactura impresionfactura = new Impresionfactura();
            impresionfactura.setUsuario(loginBean.getUsuario());
            impresionfactura.setFechaimpresion(new Date());
            impresionfactura.setMovimiento(venta.getMovimiento());
            impresionfactura.setDosificacion(dosificacion);
            impresionfactura.setTipo(tipoEtiquetaFactura);
            impresionfactura.setNroFactura(venta.getMovimiento().getNrofactura());
            venta.getMovimiento().getImpresionfacturaCollection().add(impresionfactura);
            /*movimientoController.setSelected(venta.getMovimiento());
            movimientoController.update();*/
            if(venta.getEstado().equals("PENDIENTE"))
                venta.setEstado("PREPARAR");
            ventadirectaController.setItems(null);
            ventadirectaController.setSelected(venta);
            ventadirectaController.generalUpdate();
        }
    }
    
    public void guardarFactura(Ventadirecta venta, ControlCode controlCode) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "loginBean");
        if (venta.getMovimiento() == null) {
            Movimiento movimiento = new Movimiento();
            /*movimiento.setCodestruct(dosificacion.getEstCod());*/
            movimiento.setEstado(InvoiceState.ACTIVE.getType());
            movimiento.setCodigocontrol(controlCode.getCodigoControl());
            movimiento.setFecharegistro(new Date());
            //todo:virificar estos valores
            movimiento.setGlosa("");
            /*movimiento.setMoneda("BS");*/
            movimiento.setTipocambio(6.96);
            movimiento.setNrofactura(dosificacion.getNumeroactual());
            movimiento.setCodigoQR(controlCode.getKeyQR());
            movimiento.setNroAutorizacion(controlCode.getNumeroAutorizacion());
            venta.setMovimiento(movimiento);

            Impresionfactura impresionfactura = new Impresionfactura();
            impresionfactura.setUsuario(loginBean.getUsuario());
            impresionfactura.setFechaimpresion(new Date());
            impresionfactura.setMovimiento(movimiento);
            impresionfactura.setDosificacion(dosificacion);
            impresionfactura.setTipo("ORIGINAL");
            impresionfactura.setNroFactura(dosificacion.getNumeroactual());
            movimiento.getImpresionfacturaCollection().add(impresionfactura);

            venta.setMovimiento(movimiento);
            if(venta.getEstado().equals("PENDIENTE"))
                venta.setEstado("PREPARAR");
            ventadirectaController.setItems(null);
            ventadirectaController.setSelected(venta);
            ventadirectaController.generalUpdate();
            // dosificacion.setNumeroactual(dosificacion.getNumeroactual().intValue() + 1);
            dosificacionController.setSelected(dosificacion);
            dosificacionController.update();
        } else {
            Impresionfactura impresionfactura = new Impresionfactura();
            impresionfactura.setUsuario(loginBean.getUsuario());
            impresionfactura.setFechaimpresion(new Date());
            impresionfactura.setMovimiento(venta.getMovimiento());
            impresionfactura.setDosificacion(dosificacion);
            impresionfactura.setTipo(tipoEtiquetaFactura);
            impresionfactura.setNroFactura(venta.getMovimiento().getNrofactura());
            venta.getMovimiento().getImpresionfacturaCollection().add(impresionfactura);
            /*movimientoController.setSelected(venta.getMovimiento());
            movimientoController.update();*/
            if(venta.getEstado().equals("PENDIENTE"))
                venta.setEstado("PREPARAR");
            ventadirectaController.setItems(null);
            ventadirectaController.setSelected(venta);
            ventadirectaController.generalUpdate();
        }
    }

    public void guardarFacturaGenerada(Ventadirecta venta, String codControl,String codQR) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "loginBean");

            Movimiento movimiento = new Movimiento();
            /*movimiento.setCodestruct(dosificacion.getEstCod());*/
            movimiento.setEstado(InvoiceState.ACTIVE.getType());
            movimiento.setCodigocontrol(codControl);
            movimiento.setFecharegistro(new Date());
            //todo:virificar estos valores
            movimiento.setGlosa("");
            /*movimiento.setMoneda("BS");*/
            movimiento.setTipocambio(6.69);
            movimiento.setCodigoQR(codQR);
            movimiento.setNrofactura(dosificacion.getNumeroactual());
            venta.setMovimiento(movimiento);

            Impresionfactura impresionfactura = new Impresionfactura();
            impresionfactura.setUsuario(loginBean.getUsuario());
            impresionfactura.setFechaimpresion(new Date());
            impresionfactura.setMovimiento(movimiento);
            impresionfactura.setDosificacion(dosificacion);
            impresionfactura.setTipo("ORIGINAL");
            impresionfactura.setNroFactura(dosificacion.getNumeroactual());
            movimiento.getImpresionfacturaCollection().add(impresionfactura);

            Impresionfactura impresionfacturaCopia = new Impresionfactura();
            impresionfactura.setUsuario(loginBean.getUsuario());
            impresionfactura.setFechaimpresion(new Date());
            impresionfactura.setMovimiento(movimiento);
            impresionfactura.setDosificacion(dosificacion);
            impresionfactura.setTipo("COPIA");
            impresionfactura.setNroFactura(dosificacion.getNumeroactual());

            movimiento.getImpresionfacturaCollection().add(impresionfacturaCopia);
            dosificacionController.setSelected(dosificacion);
            dosificacionController.update();
    }

    public void guardarFactura(Pedidos pedido, String codControl,String codQR) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "loginBean");
        if (pedido.getMovimiento() == null) {
            Movimiento movimiento = new Movimiento();
            /*movimiento.setCodestruct(dosificacion.getEstCod());*/
            movimiento.setEstado(InvoiceState.ACTIVE.getType());
            movimiento.setCodigocontrol(codControl);
            movimiento.setFecharegistro(new Date());
            //todo:virificar estos valores
            movimiento.setGlosa("");
            /*movimiento.setMoneda("BS");*/
            movimiento.setTipocambio(6.69);
            movimiento.setCodigoQR(codQR);
            movimiento.setNrofactura(dosificacion.getNumeroactual());
            pedido.setMovimiento(movimiento);

            Impresionfactura impresionfactura = new Impresionfactura();
            impresionfactura.setUsuario(loginBean.getUsuario());
            impresionfactura.setFechaimpresion(new Date());
            impresionfactura.setMovimiento(movimiento);
            impresionfactura.setDosificacion(dosificacion);
            impresionfactura.setTipo("ORIGINAL");
            impresionfactura.setNroFactura(dosificacion.getNumeroactual());
            movimiento.getImpresionfacturaCollection().add(impresionfactura);
            /*movimientoController.setSelected(movimiento);
             movimientoController.create();*/
            pedido.setMovimiento(movimiento);
            if(pedido.getEstado().equals("PENDIENTE"))
            pedido.setEstado("PREPARAR");
            pedido.setTieneFactura(true);
            pedidosController.setItems(null);
            pedidosController.setSelected(pedido);
            pedidosController.generalUpdate();
            // dosificacion.setNumeroactual(dosificacion.getNumeroactual().intValue() + 1);
            dosificacionController.setSelected(dosificacion);
            dosificacionController.update();
        } else {
            Impresionfactura impresionfactura = new Impresionfactura();
            impresionfactura.setUsuario(loginBean.getUsuario());
            impresionfactura.setFechaimpresion(new Date());
            impresionfactura.setMovimiento(pedido.getMovimiento());
            impresionfactura.setDosificacion(dosificacion);
            impresionfactura.setTipo(tipoEtiquetaFactura);
            impresionfactura.setNroFactura(pedido.getMovimiento().getNrofactura());
            pedido.getMovimiento().getImpresionfacturaCollection().add(impresionfactura);
            /*movimientoController.setSelected(pedido.getMovimiento());
            movimientoController.update();*/
            if(pedido.getEstado().equals("PENDIENTE"))
                pedido.setEstado("PREPARAR");
            pedidosController.setSelected(pedido);
            pedidosController.setItems(null);
            pedidosController.generalUpdate();
        }
    }

    /**
     *
     * @param pedido
     * @param controlCode
     */
    public void guardarFactura(Pedidos pedido, ControlCode controlCode) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "loginBean");
        if (pedido.getMovimiento() == null) {
            Movimiento movimiento = new Movimiento();
            /*movimiento.setCodestruct(dosificacion.getEstCod());*/
            movimiento.setEstado(InvoiceState.ACTIVE.getType());
            movimiento.setCodigocontrol(controlCode.getCodigoControl());
            movimiento.setFecharegistro(new Date());

            //todo:virificar estos valores
            movimiento.setGlosa("");
            /*movimiento.setMoneda("BS");*/
            movimiento.setTipocambio(6.96);
            movimiento.setCodigoQR(controlCode.getKeyQR());

            movimiento.setFechaFactura(pedido.getFechaEntrega());
            movimiento.setNrofactura(controlCode.getNumberInvoice());
            movimiento.setNroAutorizacion(controlCode.getNumeroAutorizacion());

            String nitCliente = pedido.getCliente().getNroDoc();
            if(StringUtils.isNotEmpty(pedido.getCliente().getNit()))
                nitCliente = pedido.getCliente().getNit();

            movimiento.setNitCliente(nitCliente);

            String razonSocial = pedido.getCliente().getRazonsocial();
            if (razonSocial.equals("") || razonSocial == null )
                razonSocial = pedido.getCliente().getNom()+" "+pedido.getCliente().getAp()+" "+pedido.getCliente().getAm();

            movimiento.setRazonSocial(razonSocial.toUpperCase());

            movimiento.setImporteTotal(new BigDecimal(controlCode.getTotal()));
            movimiento.setImporteIceTasas(new BigDecimal(controlCode.getImporteICE()));
            movimiento.setExportExentas(new BigDecimal(0));
            movimiento.setVentasGrabTasaCero(new BigDecimal(controlCode.getImporteVentasGrabadas()));
            movimiento.setSubTotal(new BigDecimal(controlCode.getTotal())); //E = A - B - C - D
            movimiento.setDescuentos(new BigDecimal(Double.parseDouble(controlCode.getDescuentosBonificaciones())));
            movimiento.setImporteParaDebitoFiscal( new BigDecimal(controlCode.getTotal() - Double.parseDouble(controlCode.getDescuentosBonificaciones())));
            movimiento.setDebitoFiscal(new BigDecimal(movimiento.getImporteParaDebitoFiscal().doubleValue() * 0.13));
            movimiento.setCodigocontrol(controlCode.getCodigoControl());
            movimiento.setPedido(pedido);

            pedido.setMovimiento(movimiento);

            Impresionfactura impresionfactura = new Impresionfactura();
            impresionfactura.setUsuario(loginBean.getUsuario());
            impresionfactura.setFechaimpresion(new Date());
            impresionfactura.setMovimiento(movimiento);
            impresionfactura.setDosificacion(dosificacion);
            impresionfactura.setTipo("ORIGINAL");
            impresionfactura.setNroFactura(controlCode.getNumberInvoice());
            movimiento.getImpresionfacturaCollection().add(impresionfactura);
            /*movimientoController.setSelected(movimiento);
             movimientoController.create();*/
            pedido.setMovimiento(movimiento);
            if(pedido.getEstado().equals("PENDIENTE"))
            pedido.setEstado("PREPARAR");
            pedido.setTieneFactura(true);
            pedidosController.setItems(null);
            pedidosController.setSelected(pedido);
            pedidosController.generalUpdate();
            // dosificacion.setNumeroactual(dosificacion.getNumeroactual().intValue() + 1);
            dosificacionController.setSelected(dosificacion);
            dosificacionController.update();
        } else {
            Impresionfactura impresionfactura = new Impresionfactura();
            impresionfactura.setUsuario(loginBean.getUsuario());
            impresionfactura.setFechaimpresion(new Date());
            impresionfactura.setMovimiento(pedido.getMovimiento());
            impresionfactura.setDosificacion(dosificacion);
            impresionfactura.setTipo(tipoEtiquetaFactura);
            impresionfactura.setNroFactura(pedido.getMovimiento().getNrofactura());
            pedido.getMovimiento().getImpresionfacturaCollection().add(impresionfactura);
            /*movimientoController.setSelected(pedido.getMovimiento());
            movimientoController.update();*/
            if(pedido.getEstado().equals("PENDIENTE"))
                pedido.setEstado("PREPARAR");
            pedidosController.setSelected(pedido);
            pedidosController.setItems(null);
            pedidosController.generalUpdate();
        }
    }

    public void imprimirFacturas() throws IOException, JRException {
        quitarAnulados();
        HashMap parameters = new HashMap();
        moneyUtil = new MoneyUtil();
        barcodeRenderer = new BarcodeRenderer();
        //todo: lanzar un exception en caso que no encuentre una dosificacion valida
        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "loginBean");
        
        for(Dosificacion dos:loginBean.getUsuario().getSucursal().getDosificaciones()){
            if(dos.getEstado().equals("ACTIVO")){
                dosificacion = dos;
            }
        }
        File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/reportes/factura.jasper"));
        //quitarSinFactura();
        if(pedidosElegidos.size() == 0){
            JSFUtil.addWarningMessage("No hay ningun pedido elegido.");
            return;
        }

        JasperPrint jasperPrint = null;

        // -----------------------------------------------
        parameters.putAll(fijarParmetrosFactura(pedidosElegidos.get(0)));
        // Verificando Articulo con cantidad 0 y q tiene reposicion
        Collection<ArticulosPedido> articulos = new ArrayList<>();
        for (ArticulosPedido articulo : pedidosElegidos.get(0).getArticulosPedidos()) {
            if (articulo.getCantidad() > 0){
                String etiquetaSub =  articulo.getPedidos().getDescripcion() != null ? " " + articulo.getPedidos().getDescripcion() : "";
                articulo.getInvArticulos().setDescri(articulo.getInvArticulos().getDescri() + etiquetaSub);
                articulos.add(articulo);
            }
        }
        // End
        try {
            //jasperPrint = JasperFillManager.fillReport(jasper.getPath(), parameters, new JRBeanCollectionDataSource(pedidosElegidos.get(0).getArticulosPedidos()));
            jasperPrint = JasperFillManager.fillReport(jasper.getPath(), parameters, new JRBeanCollectionDataSource(articulos));
        } catch (JRException e) {
            e.printStackTrace();
            //todo:mensaje de error
            return;
        }
        // -----------------------------------------------

        for (int i = 1; i < pedidosElegidos.size(); i++) {
            parameters.putAll(fijarParmetrosFactura(pedidosElegidos.get(i)));
            /** Verificando Articulo con cantidad 0 y q tiene reposicion **/
            Collection<ArticulosPedido> articulosPed = new ArrayList<>();
            for ( ArticulosPedido articulo:pedidosElegidos.get(i).getArticulosPedidos()){
                if (articulo.getCantidad() > 0) {
                    String etiquetaSub =  articulo.getPedidos().getDescripcion() != null ? " " + articulo.getPedidos().getDescripcion() : "";
                    articulo.getInvArticulos().setDescri(articulo.getInvArticulos().getDescri() + etiquetaSub);
                    articulosPed.add(articulo);
                }
            }

            /** End **/
            try {
                jasperPrint.getPages().addAll(JasperFillManager.fillReport(jasper.getPath()
                        , parameters
                        /*, new JRBeanCollectionDataSource(pedidosElegidos.get(i).getArticulosPedidos())).getPages());*/
                        , new JRBeanCollectionDataSource(articulosPed)).getPages());
            } catch (JRException e) {
                e.printStackTrace();
                //todo:mensaje de error
                return;
            }
        }
        exportarPDF(jasperPrint);
        pedidosController.actualizar();
    }

    /** Test **/
    public void imprimirFacturas2() throws IOException, JRException {
        quitarAnulados();
        HashMap parameters = new HashMap();
        moneyUtil = new MoneyUtil();
        barcodeRenderer = new BarcodeRenderer();
        //todo: lanzar un exception en caso que no encuentre una dosificacion valida
        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "loginBean");

        for(Dosificacion dos:loginBean.getUsuario().getSucursal().getDosificaciones()){
            if(dos.getEstado().equals("ACTIVO")){
                dosificacion = dos;
            }
        }
        File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/reportes/factura.jasper"));
        if(pedidosElegidos.size() == 0){
            JSFUtil.addWarningMessage("No hay ningun pedido elegido.");
            return;
        }

        JasperPrint jasperPrint = null;

        // -----------------------------------------------

        /** Ordenamiento Pedidos por nombre **/
        /*
        Collections.sort(pedidosElegidos, new Comparator<Pedidos>() {
            @Override
            public int compare(Pedidos o1, Pedidos o2) {
                return o1.getCliente().getNom().compareTo(o2.getCliente().getNom());
            }
        });
        */

        parameters.putAll(fijarParmetrosFactura(pedidosElegidos.get(0)));
        // Verificando Articulo con cantidad 0 y q tiene reposicion
        Collection<ArticulosPedido> articulos = new ArrayList<>();

        for (ArticulosPedido articulo : pedidosElegidos.get(0).getArticulosPedidos()) {
            if (articulo.getCantidad() > 0){
                String etiquetaSub =  articulo.getPedidos().getDescripcion() != null ? " " + articulo.getPedidos().getDescripcion() : "";
                articulo.getInvArticulos().setDescri(articulo.getInvArticulos().getDescri() + etiquetaSub);

                //articulos.add(articulo);
                if (!existeArticulo(articulos, articulo))
                    articulos.add(articulo);
            }
        }

        // End
        try {
            jasperPrint = JasperFillManager.fillReport(jasper.getPath(), parameters, new JRBeanCollectionDataSource(articulos));
        } catch (JRException e) {
            e.printStackTrace();
            //todo:mensaje de error
            return;
        }
        // -----------------------------------------------

        for (int i = 1; i < pedidosElegidos.size(); i++) {
            parameters.putAll(fijarParmetrosFactura(pedidosElegidos.get(i)));
            /** Verificando Articulo con cantidad 0 y q tiene reposicion **/
            Collection<ArticulosPedido> articulosPed = new ArrayList<>();
            for ( ArticulosPedido articulo:pedidosElegidos.get(i).getArticulosPedidos()){
                if (articulo.getCantidad() > 0) {
                    String etiquetaSub =  articulo.getPedidos().getDescripcion() != null ? " " + articulo.getPedidos().getDescripcion() : "";
                    articulo.getInvArticulos().setDescri(articulo.getInvArticulos().getDescri() + etiquetaSub);

                    //articulosPed.add(articulo);
                    if (!existeArticulo(articulosPed, articulo))
                        articulosPed.add(articulo);
                }
            }

            /** End **/
            try {
                jasperPrint.getPages().addAll(JasperFillManager.fillReport(jasper.getPath()
                        , parameters
                        , new JRBeanCollectionDataSource(articulosPed)).getPages());
            } catch (JRException e) {
                e.printStackTrace();
                //todo:mensaje de error
                return;
            }
        }
        exportarPDF(jasperPrint);
        pedidosController.actualizar();
    }

    public Boolean existeArticulo(Collection<ArticulosPedido> articulosList, ArticulosPedido articuloPedido){

        Boolean result = Boolean.FALSE;
        for (ArticulosPedido articulo : articulosList){
            if (articulo.getInvArticulos().getProductItemCode().equals(articuloPedido.getInvArticulos().getProductItemCode())){
                Integer cantidad  = articulo.getCantidad() + articuloPedido.getCantidad();
                Integer total = articulo.getTotal()    + articuloPedido.getTotal();
                Double importe    = articulo.getImporte()  + articuloPedido.getImporte();

                articulo.setCantidad(cantidad);
                articulo.setTotal(total);
                articulo.setImporte(importe);
                result = Boolean.TRUE;
            }
        }
        return  result;
    }

    public void imprimirFacturasVenta() throws IOException, JRException {
        if(ventaDirectaElegidos.size() == 0)
        {
            JSFUtil.addWarningMessage("No hay ningun pedido elegido.");
            return;
        }
        quitarAnuladosVenta();
        HashMap parameters = new HashMap();
        moneyUtil = new MoneyUtil();
        barcodeRenderer = new BarcodeRenderer();
        //todo: lanzar un exception en caso que no encuentre una dosificacion valida
        dosificacion = dosificacionFacade.findByPeriodo(new Date());
        File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/reportes/factura.jasper"));
        quitarSinFacturaVenta();
        JasperPrint jasperPrint;
        parameters.putAll(fijarParmetrosFactura(ventaDirectaElegidos.get(0)));
        try {
            jasperPrint = JasperFillManager.fillReport(jasper.getPath(), parameters, new JRBeanCollectionDataSource(ventaDirectaElegidos.get(0).getArticulosPedidos()));
        } catch (JRException e) {
            e.printStackTrace();
            //todo:mensaje de error
            return;
        }

        for (int i = 1; i < ventaDirectaElegidos.size(); i++) {
            parameters.putAll(fijarParmetrosFactura(ventaDirectaElegidos.get(i)));
            try {
                jasperPrint.getPages().addAll(JasperFillManager.fillReport(jasper.getPath()
                        , parameters
                        , new JRBeanCollectionDataSource(ventaDirectaElegidos.get(i).getArticulosPedidos())).getPages());
            } catch (JRException e) {
                e.printStackTrace();
                //todo:mensaje de error
                return;
            }
        }
        exportarPDF(jasperPrint);
    }

    private void quitarSinFactura() {
        pedidosElegidos = pedidosElegidos.stream().filter(p->p.getCliente().getConfactura() == true).collect(Collectors.toList());
    }

    private void quitarNoEnviados()
    {
        pedidosElegidos = pedidosElegidos.stream().filter(p->p.getEstado().equals("ENTREGADO") == true).collect(Collectors.toList());
    }

    private void quitarAnulados() {
        pedidosElegidos = pedidosElegidos.stream().filter(p->p.getEstado().equals("ANULADO") != true).collect(Collectors.toList());
    }
    
    private void quitarContabilizados() {
        pedidosElegidos = pedidosElegidos.stream().filter(p->p.getEstado().equals("CONTABILIZADO") != true).collect(Collectors.toList());
    }

    private void quitarSinFacturaVenta() {
        ventaDirectaElegidos = ventaDirectaElegidos.stream().filter(p->p.getCliente().getConfactura() == true).collect(Collectors.toList());
    }

    private void quitarAnuladosVenta() {
        ventaDirectaElegidos = ventaDirectaElegidos.stream().filter(p->p.getEstado().equals("ANULADO") != true).collect(Collectors.toList());
    }
    //todo: guardar el codigo QR
    public void imprimirNota(List<Pedidos> pedidosElegidos) throws IOException, JRException {
        if(pedidosElegidos.size() == 0){
            JSFUtil.addWarningMessage("No hay ningun pedido elegido.");
            return;
        }

        /** Ordenamiento Pedidos por nombre **/
        /*
        Collections.sort(pedidosElegidos, new Comparator<Pedidos>() {
            @Override
            public int compare(Pedidos o1, Pedidos o2) {
                return o1.getCliente().getNom().compareTo(o2.getCliente().getNom());
            }
        });*/

        HashMap parameters = new HashMap();
        moneyUtil = new MoneyUtil();
        File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/reportes/notaDeEntrega.jasper"));
        this.pedidosElegidos = pedidosElegidos;

        quitarAnulados();
        pedidosElegidos = this.pedidosElegidos;

        JasperPrint jasperPrint;
        parameters.putAll(getReportParams(pedidosElegidos.get(0)));

        try {
            jasperPrint = JasperFillManager.fillReport(jasper.getPath(), parameters, new JRBeanCollectionDataSource(pedidosElegidos.get(0).getArticulosPedidos()));
        } catch (JRException e) {
            e.printStackTrace();
            //todo:mensaje de error
            return;
        }

        for (int i = 1; i < pedidosElegidos.size(); i++) {
            parameters.putAll(getReportParams(pedidosElegidos.get(i)));
            try {
                jasperPrint.getPages().addAll(JasperFillManager.fillReport(jasper.getPath(), parameters, new JRBeanCollectionDataSource(pedidosElegidos.get(i).getArticulosPedidos())).getPages());
            } catch (JRException e) {
                e.printStackTrace();
                //todo:mensaje de error
                return;
            }
        }

        for (Pedidos pedido : pedidosElegidos) {
            if (pedido.getEstado().equals("PENDIENTE")) {
                pedido.setEstado("PREPARAR");
                pedidosController.setSelected(pedido);
                pedidosController.setItems(null);
                pedidosController.generalUpdate();
            }
        }

        exportarPDF(jasperPrint);
        this.pedidosElegidos.clear();
    }

    public Map<String, Object> fijarParmetrosFacturaVenta(Ventadirecta venta) {
        Integer numeroFactura;
        if (venta.getMovimiento() == null) {
            numeroFactura = Integer.parseInt(dosificacionFacade.getSiguienteNumeroFactura());
            dosificacion.setNumeroactual(numeroFactura);
        }else{
            numeroFactura = venta.getMovimiento().getNrofactura();
        }
        ControlCode controlCode = generateCodControl(venta, numeroFactura, dosificacion.getNroautorizacion(), dosificacion.getLlave(),dosificacion.getNitEmpresa());
        if (venta.getEstado().equals("PENDIENTE")) {
            venta.setEstado("PREPARAR");
        }
        guardarFactura(venta, controlCode.getCodigoControl(),controlCode.getKeyQR());
        return getReportParams(
                venta.getCliente().getNombreCompleto(), numeroFactura, tipoEtiquetaFactura, controlCode.getCodigoControl(), controlCode.getKeyQR(), venta);
    }

    public Map<String, Object> fijarParmetrosFactura(Pedidos pedido) {
        Map<String, Object> paramMapResult = null;
        /** Para nueva factura **/
        if (pedido.getMovimiento() == null) {
            Integer numeroFactura;
            FacesContext facesContext = FacesContext.getCurrentInstance();
            LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "loginBean");
            Sucursal sucursal = loginBean.getUsuario().getSucursal();

            Dosificacion dosifica = dosificacionFacade.findByOffice(sucursal);
            this.dosificacion = dosifica;

            if (pedido.getMovimiento() == null) {
                //numeroFactura = Integer.parseInt(dosificacionFacade.getSiguienteNumeroFactura());
                //dosificacion.setNumeroactual(numeroFactura);
                numeroFactura = dosifica.getNumeroactual();
                //dosificacionFacade.updateNextNumber(sucursal);
                dosificacion.setNumeroactual(numeroFactura + 1);


            } else {
                numeroFactura = pedido.getMovimiento().getNrofactura();
            }
            ControlCode controlCode = generateCodControl(pedido, numeroFactura, dosifica.getNroautorizacion(), dosifica.getLlave(), dosifica.getNitEmpresa());
            if (pedido.getEstado().equals("PENDIENTE")) {
                pedido.setEstado("PREPARAR");
            }
            //guardarFactura(pedido, controlCode.getCodigoControl(),controlCode.getKeyQR());
            guardarFactura(pedido, controlCode);
            //return getReportParams(pedido.getCliente().getNombreCompleto(), numeroFactura, tipoEtiquetaFactura, controlCode.getCodigoControl(), controlCode.getKeyQR(), pedido);
            paramMapResult = getReportParams(pedido, controlCode, dosifica);
        } else {
          /** Para reimpresion de factura **/
            paramMapResult = getParamsReimpresion(pedido);
        }
        return paramMapResult;
    }

    /**
     * Para reimpresion de factura de pedido
     * @param pedido
     * @return Parametros para reimpresion
     */
    private Map<String, Object> getParamsReimpresion(Pedidos pedido) {

        String filePath = FileCacheLoader.i.getPath("/resources/reportes/qr_inv.png");
        DateUtil dateUtil = new DateUtil();

        Movimiento mov = pedido.getMovimiento();
        Dosificacion dosage     = dosificacionFacade.findByAuthorization(mov.getNroAutorizacion());
        ControlCode controlCode = generateCodControl(pedido, mov.getNrofactura(), dosage.getNroautorizacion(), dosage.getLlave(), dosage.getNitEmpresa());

        Calendar cal = Calendar.getInstance();
        cal.setTime(mov.getFechaFactura());
        int anio = cal.get(Calendar.YEAR);
        int mes  = cal.get(Calendar.MONTH);
        int dia  = cal.get(Calendar.DAY_OF_MONTH);

        String fecha = "Punata, "+dia+" de "+dateUtil.getMes(mes)+" de "+anio;

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("nitEmpresa",      dosage.getNitEmpresa());
        paramMap.put("numFac",          mov.getNrofactura().longValue());
        paramMap.put("numAutorizacion", mov.getNroAutorizacion());
        paramMap.put("nitCliente",      mov.getNitCliente());
        paramMap.put("fecha", fecha);
        paramMap.put("nombreCliente",   mov.getRazonSocial());
        paramMap.put("fechaLimite",     dosage.getFechavencimiento());
        paramMap.put("codigoControl",   mov.getCodigocontrol());
        paramMap.put("tipoEtiqueta",    tipoEtiquetaFactura);
        paramMap.put("etiquetaEmpresa", dosage.getEtiquetaEmpresa());
        paramMap.put("etiquetaLey",     dosage.getEtiquetaLey());
        //verificar por que no requiere el codigo de control
        paramMap.put("llaveQR", controlCode.getKeyQR());

        Double totalPagar = pedido.getTotalimporte() - pedido.getValorComision();
        DecimalFormat df = new DecimalFormat("0.00");
        totalPagar = Double.parseDouble(df.format(totalPagar).replace(",", "."));

        paramMap.put("totalLiteral", moneyUtil.Convertir(totalPagar.toString(), true));
        paramMap.put("total", pedido.getTotalimporte());
        paramMap.put("valorComision", pedido.getValorComision());
        paramMap.put("REPORT_LOCALE", new java.util.Locale("en", "US"));
        barcodeRenderer.generateQR(controlCode.getKeyQR(), filePath);
        try {
            BufferedImage img = ImageIO.read(new File(filePath));
            paramMap.put("imgQR", img);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return paramMap;
    }

    public Map<String, Object> fijarParmetrosFactura(Ventadirecta venta) {
        Integer numeroFactura;
        if (venta.getMovimiento() == null) {
            numeroFactura = Integer.parseInt(dosificacionFacade.getSiguienteNumeroFactura());
            dosificacion.setNumeroactual(numeroFactura);
        }else{
            numeroFactura = venta.getMovimiento().getNrofactura();
        }
        ControlCode controlCode = generateCodControl(venta, numeroFactura, dosificacion.getNroautorizacion(), dosificacion.getLlave(),dosificacion.getNitEmpresa());
        if (venta.getEstado().equals("PENDIENTE")) {
            venta.setEstado("PREPARAR");
        }
        guardarFactura(venta, controlCode.getCodigoControl(),controlCode.getKeyQR());
        return getReportParams(
                venta.getCliente().getNombreCompleto(), numeroFactura, tipoEtiquetaFactura, controlCode.getCodigoControl(), controlCode.getKeyQR(), venta);
    }

    public void exportarPDF(JasperPrint jasperPrint) throws IOException, JRException {

        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        response.addHeader("Content-disposition", "attachment; filename=DOCUMENTO_ILVA.pdf");
        ServletOutputStream stream = response.getOutputStream();

        JasperExportManager.exportReportToPdfStream(jasperPrint, stream);

        stream.flush();
        stream.close();
        FacesContext.getCurrentInstance().responseComplete();
    }

    public void exportarPDF(HashMap parametros, File jasper) throws JRException, IOException {

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasper.getPath(), parametros, new JRBeanCollectionDataSource(pedido.getArticulosPedidos()));
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        response.addHeader("Content-disposition", "attachment; filename=DOCUMENTO_ILVA.pdf");
        ServletOutputStream stream = response.getOutputStream();

        JasperExportManager.exportReportToPdfStream(jasperPrint, stream);

        stream.flush();
        stream.close();
        FacesContext.getCurrentInstance().responseComplete();
    }

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

    public void exportarPDF(HashMap parametros, File jasper, Pedidos pedido) throws JRException, IOException {

        Collection<ArticulosPedido> articulos = new ArrayList<>();
        for (ArticulosPedido articulo : pedido.getArticulosPedidos()) {
                String etiquetaSub =  articulo.getPedidos().getDescripcion() != null ? " " + articulo.getPedidos().getDescripcion() : "";
                articulo.getInvArticulos().setDescri(articulo.getInvArticulos().getDescri() + etiquetaSub);

                //articulos.add(articulo);
                if (!existeArticulo(articulos, articulo))
                    articulos.add(articulo);

        }

        //JasperPrint jasperPrint = JasperFillManager.fillReport(jasper.getPath(), parametros, new JRBeanCollectionDataSource(pedido.getArticulosPedidos()));
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasper.getPath(), parametros, new JRBeanCollectionDataSource(articulos));
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        response.addHeader("Content-disposition", "attachment; filename=DOCUMENTO_ILVA.pdf");
        ServletOutputStream stream = response.getOutputStream();

        JasperExportManager.exportReportToPdfStream(jasperPrint, stream);

        stream.flush();
        stream.close();
        FacesContext.getCurrentInstance().responseComplete();
    }

    public byte[] exportarPDFToString(HashMap parametros, File jasper,Ventadirecta venta) throws JRException, IOException {

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasper.getPath(), parametros, new JRBeanCollectionDataSource(venta.getArticulosPedidos()));
        byte[] pdf = JasperExportManager.exportReportToPdf(jasperPrint);
        return pdf;
    }

    private ControlCode recuperarCodControl(Movimiento movimiento, Dosificacion dosificacion) {

        ControlCode controlCode = new ControlCode(
                dosificacion.getNitEmpresa(),
                movimiento.getNrofactura(),
                dosificacion.getNroautorizacion().toString(),
                movimiento.getFechaFactura(),
                movimiento.getImporteTotal().doubleValue(),
                movimiento.getImporteParaDebitoFiscal().doubleValue(),
                movimiento.getNitCliente(),
                movimiento.getDescuentos().doubleValue());

        moneyUtil.getLlaveQR(controlCode, dosificacion.getLlave());
        controlCode.generarCodigoQR();
        return controlCode;
    }

    //todo: dar formato al total importe y al importeBaseCreditFisical
    private ControlCode generateCodControl(Pedidos pedido, Integer numberInvoice, BigInteger numberAutorization, String key,String nitEmpresa) {
        Double importeBaseCreditFisical = pedido.getTotalimporte() - pedido.getValorComision();
        String nroDoc = pedido.getCliente().getNroDoc();
        if(StringUtils.isNotEmpty(pedido.getCliente().getNit()))
        {
            nroDoc = pedido.getCliente().getNit();
        }
        pedido.setImpuesto(pedido.getTotalimporte() * 0.13);
        ControlCode controlCode = new ControlCode(  nitEmpresa, 
                                                    numberInvoice, 
                                                    numberAutorization.toString(),
                                                    pedido.getFechaEntrega(), 
                                                    pedido.getTotalimporte(),
                                                    importeBaseCreditFisical, 
                                                    nroDoc,
                                                    pedido.getValorComision());
        moneyUtil.getLlaveQR(controlCode, key);
        controlCode.generarCodigoQR();
        return controlCode;
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
    
    private ControlCode generateCodControl(Ventadirecta venta, Integer numberInvoice, String nroAutorizacion, String key,String nitEmpresa) {
        Double importeBaseCreditFisical = venta.getTotalimporte();
        String nroDoc = venta.getCliente().getNroDoc();
        if(StringUtils.isNotEmpty(venta.getCliente().getNit()))
        {
            nroDoc = venta.getCliente().getNit();
        }
        venta.setImpuesto(venta.getTotalimporte() * 0.13);
        ControlCode controlCode = new ControlCode(nitEmpresa, numberInvoice, nroAutorizacion, venta.getFechaPedido(), venta.getTotalimporte(), importeBaseCreditFisical, nroDoc);
        moneyUtil.getLlaveQR(controlCode, key);
        controlCode.generarCodigoQR();
        return controlCode;
    }

    public String getTipoEtiquetaFactura() {
        return tipoEtiquetaFactura;
    }

    public void setTipoEtiquetaFactura(String tipoEtiquetaFactura) {
        this.tipoEtiquetaFactura = tipoEtiquetaFactura;
    }

    public Boolean getEsCopia() {
        return esCopia;
    }

    public void setEsCopia(Boolean esCopia) {
        this.esCopia = esCopia;
    }

    public Pedidos getPedido() {
        return pedido;
    }

    public void setPedido(Pedidos pedido) {
        this.pedido = pedido;
    }

    public List<Pedidos> getPedidosElegidos() {
        return pedidosElegidos;
    }

    public void setPedidosElegidos(List<Pedidos> pedidosElegidos) {
        this.pedidosElegidos = pedidosElegidos;
    }

    public Ventadirecta getVentadirecta() {
        return ventadirecta;
    }

    public void setVentadirecta(Ventadirecta ventadirecta) {
        this.ventadirecta = ventadirecta;
    }

    public List<Ventadirecta> getVentaDirectaElegidos() {
        return ventaDirectaElegidos;
    }

    public void setVentaDirectaElegidos(List<Ventadirecta> ventaDirectaElegidos) {
        this.ventaDirectaElegidos = ventaDirectaElegidos;
    }
}
