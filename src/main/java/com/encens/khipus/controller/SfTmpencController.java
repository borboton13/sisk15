package com.encens.khipus.controller;

import com.encens.khipus.ejb.SfTmpencFacade;
import com.encens.khipus.model.Persona;
import com.encens.khipus.model.SfTmpenc;
import com.encens.khipus.util.JSFUtil;
import com.encens.khipus.util.JSFUtil.PersistAction;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("sfTmpencController")
@SessionScoped
public class SfTmpencController implements Serializable {

    @EJB
    private com.encens.khipus.ejb.SfTmpencFacade ejbFacade;
    private List<SfTmpenc> items = null;
    private SfTmpenc selected;
    private Persona personaElegida = new Persona();
    private Date fechaIni;
    private Date fechaFin;

    public SfTmpencController() {
    }

    public SfTmpenc getSelected() {
        return selected;
    }

    public void setSelected(SfTmpenc selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private SfTmpencFacade getFacade() {
        return ejbFacade;
    }

    public SfTmpenc prepareCreate() {
        selected = new SfTmpenc();
        initializeEmbeddableKey();
        return selected;
    }

    public void generarKardex() throws IOException, JRException {
        if(validarCampos())
        {
            return;
        }
        HashMap parameters = new HashMap();

        File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/reportes/kardex.jasper"));
        parameters.putAll(getReportParams());
        exportarPDF(parameters,jasper);
    }
    
    public boolean validarCampos()
    {
        Boolean band = false;
        if(fechaIni == null)
        {
             JSFUtil.addWarningMessage("La fecha inicial es necesario");
             band = true;
        }
        if(fechaFin == null)
        {
             JSFUtil.addWarningMessage("La fecha final es necesario");
             band = true;
        }
        if(personaElegida == null)
        {
             JSFUtil.addWarningMessage("Elija un cliente primeramente.");
             band = true;
        }
        return band;
    }

    public void generarRecaudacion() throws IOException, JRException {
        HashMap parameters = new HashMap();

        File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/reportes/Recaudacion.jasper"));
        parameters.putAll(getReportParamsRecaudaciones());
        exportarPDFRecaudaciones(parameters, jasper);
    }
    
    public void generarResumenRecaudacion() throws IOException, JRException {
        
        HashMap parameters = new HashMap();
        File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/reportes/ResumenRecaudacion.jasper"));
        
        /** PARAMETERS **/
        // parameters.putAll(getReportParamsRecaudaciones());
        Map<String, Object> paramMap = new HashMap<String, Object>();
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String periodo = df.format(fechaIni)+" - "+df.format(fechaFin);        
        String detalleCuentaPeriodo = "1110110100 (Caja General M.N.)";
        paramMap.put("periodo",periodo);
        paramMap.put("detalleCuentaPeriodo",detalleCuentaPeriodo);
        parameters.putAll(paramMap);
        
        /** EXPORT PDF **/
        //exportarPDFResumenRecaudacion(parameters, jasper);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "loginBean");
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasper.getPath(), 
                                                               parameters, 
                                                               new JRBeanCollectionDataSource(getFacade().getResumenRecaudaciones(loginBean.getUsuario(),fechaIni,fechaFin, "1110110100"))
        );
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        response.addHeader("Content-disposition", "attachment; filename=Recaudacion.pdf");
        ServletOutputStream stream = response.getOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, stream);

        stream.flush();
        stream.close();
        FacesContext.getCurrentInstance().responseComplete();
        
    }
    public void exportarPDFResumenRecaudacion(HashMap parametros, File jasper) throws JRException, IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "loginBean");

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasper.getPath(), parametros, new JRBeanCollectionDataSource(getFacade().getRecaudacionesUsuario(loginBean.getUsuario(), fechaIni, fechaFin, "1110110100")));
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        response.addHeader("Content-disposition", "attachment; filename=Recaudacion.pdf");
        ServletOutputStream stream = response.getOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, stream);

        stream.flush();
        stream.close();
        FacesContext.getCurrentInstance().responseComplete();
    }
    
    private Map<String,Object> getReportParamsRecaudaciones(){
        Map<String, Object> paramMap = new HashMap<String, Object>();
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String periodo = df.format(fechaIni)+" - "+df.format(fechaFin);        
        String detalleCuentaPeriodo = "1110110100 (Caja General M.N.)";
        paramMap.put("periodo",periodo);
        paramMap.put("detalleCuentaPeriodo",detalleCuentaPeriodo);

        return paramMap;
    }

    private Map<String, Object> getReportParams() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String periodo = "Periodo: " + df.format(fechaIni)+" - "+df.format(fechaFin);
        String nombreCliente = personaElegida.getNombreCompleto();
        String detalleCuenta = "1421010100 Clientes (Cuentas X Cobrar de Clientes)";

        Double balance = ejbFacade.getBalance(fechaIni, "1421010100", personaElegida);

        System.out.println("TOTAL TOTAL: " + balance);

        paramMap.put("periodo",periodo);
        paramMap.put("nombreCliente",personaElegida.getNombreCompleto());
        paramMap.put("detalleCuenta",detalleCuenta);
        paramMap.put("balance", balance);

        return paramMap;
    }

    public void exportarPDFRecaudaciones(HashMap parametros, File jasper) throws JRException, IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "loginBean");

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasper.getPath(), parametros, new JRBeanCollectionDataSource(getFacade().getRecaudacionesUsuario(loginBean.getUsuario(), fechaIni, fechaFin, "1110110100")));
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        response.addHeader("Content-disposition", "attachment; filename=Recaudacion.pdf");
        ServletOutputStream stream = response.getOutputStream();

        JasperExportManager.exportReportToPdfStream(jasperPrint, stream);

        stream.flush();
        stream.close();
        FacesContext.getCurrentInstance().responseComplete();
    }

    public void exportarPDF(HashMap parametros, File jasper) throws JRException, IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "loginBean");

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasper.getPath(), parametros, new JRBeanCollectionDataSource(getFacade().getKardexcliente(personaElegida,fechaIni,fechaFin,"1421010100",loginBean.getUsuario().getSucursal())));
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        response.addHeader("Content-disposition", "attachment; filename=Kardex.pdf");
        ServletOutputStream stream = response.getOutputStream();

        JasperExportManager.exportReportToPdfStream(jasperPrint, stream);

        stream.flush();
        stream.close();
        FacesContext.getCurrentInstance().responseComplete();
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("SfTmpencCreated"));
        if (!JSFUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("SfTmpencUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("SfTmpencDeleted"));
        if (!JSFUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<SfTmpenc> getItems() {
        if (items == null) {
            items = getFacade().findKardex();
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

    public SfTmpenc getSfTmpenc(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<SfTmpenc> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<SfTmpenc> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public void createGeneral() {
        persist(PersistAction.CREATE, "");
    }


    @FacesConverter(forClass = SfTmpenc.class)
    public static class SfTmpencControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SfTmpencController controller = (SfTmpencController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "sfTmpencController");
            return controller.getSfTmpenc(getKey(value));
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
            if (object instanceof SfTmpenc) {
                SfTmpenc o = (SfTmpenc) object;
                return getStringKey(o.getIdTmpenc());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), SfTmpenc.class.getName()});
                return null;
            }
        }

    }

    public Persona getPersonaElegida() {
        return personaElegida;
    }

    public void setPersonaElegida(Persona personaElegida) {
        this.personaElegida = personaElegida;
    }

    public Date getFechaIni() {
        return fechaIni;
    }

    public void setFechaIni(Date fechaIni) {
        this.fechaIni = fechaIni;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }
}
