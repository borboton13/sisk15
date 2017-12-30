package com.encens.khipus.controller;

import com.encens.khipus.ejb.BancoFacade;
import com.encens.khipus.ejb.PagoFacade;
import com.encens.khipus.ejb.SfConfencFacade;
import com.encens.khipus.ejb.SfTmpencFacade;
import com.encens.khipus.model.*;
import com.encens.khipus.util.JSFUtil;
import com.encens.khipus.util.JSFUtil.PersistAction;
import com.encens.khipus.util.MoneyUtil;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("pagoController")
@SessionScoped
public class PagoController implements Serializable {

    @EJB
    private PagoFacade ejbFacade;
    @EJB
    SfConfencFacade sfConfencFacade;
    @EJB
    SfTmpencFacade sfTmpencFacade;
    @EJB
    BancoFacade bancoFacade;

    private List<Pago> items = null;
    private Pago selected;
    private Persona personaElegida;
    private String tipoPago = "EFECTIVO";
    private Banco bancoElejido = new Banco();
    private List<Banco> bancos = new ArrayList<>();

    public PagoController() {
    }

    public Pago getSelected() {
        return selected;
    }

    public void setSelected(Pago selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private PagoFacade getFacade() {
        return ejbFacade;
    }

    public Pago prepareCreate() {
        selected = new Pago();
        bancos = bancoFacade.findAll();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        if(validarCampos()){
            return;
        }
        SfConfenc operacionCaja = sfConfencFacade.getOperacion("PAGOCAJA");
        if(operacionCaja == null)
        {
            JSFUtil.addErrorMessage("No se encuentra una operación registrada");
            return;
        }
        SfConfenc operacionBanco = sfConfencFacade.getOperacion("PAGOBANCO");
        if(operacionBanco == null)
        {
            JSFUtil.addErrorMessage("No se encuentra una operación registrada");
            return;
        }
        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "loginBean");
        selected.setPersona(personaElegida);
        selected.setFecha(new Timestamp(new Date().getTime()));
        selected.setUsuario(loginBean.getUsuario());
        selected.setSucursal(loginBean.getUsuario().getSucursal());
        String noTrans =sfTmpencFacade.getSiguienteNumeroTransacccion();
        List<SfConfdet> asientos = new ArrayList<>(operacionCaja.getAsientos());
        SfConfdet cajaOBanco = asientos.get(0);
        SfConfdet clientesCuentasPorCobrar = asientos.get(1);
        SfTmpenc asiento = new SfTmpenc();
        asiento.setFecha(new Date());
        asiento.setNombreCliente(personaElegida.getNombreCompleto());
        asiento.setCliente(personaElegida);
        asiento.setNoTrans(noTrans);
        asiento.setEstado("APR");
        asiento.setUsuario(loginBean.getUsuario());
        ////
        SfTmpdet cajaoBancoAsiento = new SfTmpdet();
        cajaoBancoAsiento.setNoTrans(noTrans);
        setDebeOHaber(cajaOBanco,cajaoBancoAsiento,selected.getPago());
        if(tipoPago.equals("BANCO")){
            asiento.setGlosa(operacionBanco.getGlosa() + " " + selected.getDescripcion());
            asiento.setTipoDoc(operacionBanco.getTipoDoc());
            asiento.setNoDoc(sfConfencFacade.getSiguienteNumeroDocumento(operacionBanco.getTipoDoc()));
            cajaoBancoAsiento.setCuenta(bancoElejido.getCuenta());
            cajaoBancoAsiento.setSfTmpenc(asiento);
            asiento.getAsientos().add(cajaoBancoAsiento);
        }else{
            asiento.setGlosa(operacionCaja.getGlosa() + " " + selected.getDescripcion());
            asiento.setTipoDoc(operacionCaja.getTipoDoc());
            asiento.setNoDoc(sfConfencFacade.getSiguienteNumeroDocumento(operacionCaja.getTipoDoc()));
            cajaoBancoAsiento.setCuenta(cajaOBanco.getCuenta().getCuenta());
            cajaoBancoAsiento.setSfTmpenc(asiento);
            asiento.getAsientos().add(cajaoBancoAsiento);
        }
        ////
        SfTmpdet clientesCuentasPorCobrarAsiento = new SfTmpdet();
        clientesCuentasPorCobrarAsiento.setNoTrans(noTrans);
        clientesCuentasPorCobrarAsiento.setCuenta(clientesCuentasPorCobrar.getCuenta().getCuenta());
        setDebeOHaber(clientesCuentasPorCobrar, clientesCuentasPorCobrarAsiento, selected.getPago());
        asiento.getAsientos().add(clientesCuentasPorCobrarAsiento);
        clientesCuentasPorCobrarAsiento.setClient(personaElegida);
        clientesCuentasPorCobrarAsiento.setSfTmpenc(asiento);
        selected.setAsiento(asiento);
        asiento.getPagos().add(selected);
        persist(PersistAction.CREATE, "El pago se registro correctamente");
        if (!JSFUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
            personaElegida = new Persona();
            selected = new Pago();
        }
    }

    private boolean validarCampos() {
        boolean band = false;
        if(personaElegida == null)
        {
            JSFUtil.addErrorMessage("El cliente es necesario.");
            band = true;
        }
        if(selected.getPago() == null || selected.getPago()== 0.0)
        {
            JSFUtil.addErrorMessage("El pago tiene que ser registrado y ser mayor que cero.");
            band = true;
        }
        return band;
    }

    private void setDebeOHaber(SfConfdet ope,SfTmpdet asiento, Double monto){
        if(ope.getTipomovimiento().equals("DEBE")) {
            asiento.setDebe(new BigDecimal(monto));
            asiento.setHaber(BigDecimal.ZERO);
        }
        else {
            asiento.setHaber(new BigDecimal(monto));
            asiento.setDebe(BigDecimal.ZERO);
        }
    }

    public void update() {
        persist(PersistAction.UPDATE,"El pago se actualizo correctamente");
    }

    public void destroy() {
        persist(PersistAction.DELETE, "El pago se elimino correctamente");
        if (!JSFUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Pago> getItems() {
        if (items == null) {
            //items = getFacade().findAll();
            items = getFacade().findPagosDesc();
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

    public Pago getPago(long id) {
        return getFacade().find(id);
    }

    public List<Pago> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Pago> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Pago.class)
    public static class PagoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PagoController controller = (PagoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "pagoController");
            return controller.getPago(getKey(value));
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
            if (object instanceof Pago) {
                Pago o = (Pago) object;
                return getStringKey(o.getIdPago());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Pago.class.getName()});
                return null;
            }
        }

    }


    public void imprimirPago(Pago pago) throws IOException, JRException {

        if(pago == null){
            JSFUtil.addWarningMessage("No hay items seleccionados.");
            return;
        }

        HashMap parameters = new HashMap();
        JasperPrint jasperPrint;
        MoneyUtil moneyUtil = new MoneyUtil();

        File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/reportes/comprobantePago.jasper"));
        Map<String, Object> paramMap = new HashMap<String, Object>();

        String tituloDoc = "";

        if (pago.getAsiento().getTipoDoc().equals("CI")) tituloDoc = "C O M P R O B A N T E   DE   I N G R E S O";
        if (pago.getAsiento().getTipoDoc().equals("DB")) tituloDoc = "D E P O S I T O   B A N C A R I O";

        paramMap.put("cliente", pago.getPersona().getNombreCompleto());
        paramMap.put("descripcion", pago.getDescripcion());
        paramMap.put("fecha", pago.getFecha());
        paramMap.put("tituloDoc", tituloDoc);
        paramMap.put("noDoc", pago.getAsiento().getNoDoc());
        paramMap.put("importe", pago.getPago());
        paramMap.put("totalLiteral", moneyUtil.Convertir(pago.getPago().toString(), true));


        parameters.putAll(paramMap);
        try {

                ArrayList<Pago> dataList = new ArrayList<Pago>();
                dataList.add(pago);

                JRBeanCollectionDataSource beanCollectionDS = new JRBeanCollectionDataSource(sfTmpencFacade.getResumenPago(pago));

                jasperPrint = JasperFillManager.fillReport(jasper.getPath(), parameters, beanCollectionDS);
                //jasperPrint.getPages().addAll(jasperPrint.getPages());

        } catch (JRException e) {
                e.printStackTrace();
                //todo:mensaje de error
                return;
        }


        //exportarPDF(jasperPrint);
        /** Export to PDF **/
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        response.addHeader("Content-disposition", "attachment; filename=ComprobantePago.pdf");
        ServletOutputStream stream = response.getOutputStream();

        JasperExportManager.exportReportToPdfStream(jasperPrint, stream);

        stream.flush();
        stream.close();
        FacesContext.getCurrentInstance().responseComplete();

        this.selected = null;
    }

    public Persona getPersonaElegida() {
        return personaElegida;
    }

    public void setPersonaElegida(Persona personaElegida) {
        this.personaElegida = personaElegida;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public Banco getBancoElejido() {
        return bancoElejido;
    }

    public void setBancoElejido(Banco bancoElejido) {
        this.bancoElejido = bancoElejido;
    }

    public List<Banco> getBancos() {
        return bancos;
    }

    public void setBancos(List<Banco> bancos) {
        this.bancos = bancos;
    }
}
