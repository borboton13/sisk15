/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.controller;

import com.encens.khipus.ejb.PedidosFacade;
import com.encens.khipus.model.Territoriotrabajo;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.jasperreports.engine.*;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Diego
 */
@Named(value = "resumenGeneralReportController")
@SessionScoped
public class ResumenGeneralReportController implements Serializable {

    /**
     * Creates a new instance of PedidosReportController
     */
    @EJB
    private PedidosFacade pedidosFacade;
    private Date fechaEntrega;
    private Territoriotrabajo territoriotrabajo;
    private Integer cantidadPedidos;


    public void imprimirReporte() throws IOException, JRException {

        HashMap parameters = new HashMap();

        File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/reportes/recepcionPedidos.jasper"));

        parameters.putAll(getReportParams());
        /*List<PedidosFacade.RecepcionPedido> resultado;
        if(fechaEntrega == null && territoriotrabajo == null)
        {
            resultado = pedidosFacade.recepcionDePedidosTodos();
        }else{
            resultado = pedidosFacade.recepcionDePedidosPorFechaTerritorio(fechaEntrega, territoriotrabajo);
        }
        cantidadPedidos = resultado.size();*/
        exportarPDF(parameters,jasper);
    }

    private Map<String, Object> getReportParams() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginBean loginBean = (LoginBean) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "loginBean");
        Map<String, Object> paramMap = new HashMap<String, Object>();
        String fecha;
        String nombreTerritorio;
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        if(fechaEntrega == null)
            fecha = "(TODOS LOS PENDIENTES)";
        else{                        
            String reportDate = df.format(fechaEntrega);
            fecha = reportDate;
        }
        if(territoriotrabajo != null)
            nombreTerritorio = territoriotrabajo.getNombre();
        else
            nombreTerritorio = "Todos";
        cantidadPedidos = pedidosFacade.getTotalPedidos(fechaEntrega,territoriotrabajo);
        paramMap.put("fecha",fecha);
        paramMap.put("cantidadPedidos",cantidadPedidos.toString());
        paramMap.put("nomUsr",loginBean.getUsuario().getUsuario());
        paramMap.put("nombreTerritorio",nombreTerritorio);

        return paramMap;
    }

    public void exportarPDF(HashMap parametros, File jasper) throws JRException, IOException {

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasper.getPath(), parametros, createDataSource());
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        response.addHeader("Content-disposition", "attachment; filename=RecepcionPedios.pdf");
        ServletOutputStream stream = response.getOutputStream();

        JasperExportManager.exportReportToPdfStream(jasperPrint, stream);

        stream.flush();
        stream.close();
        FacesContext.getCurrentInstance().responseComplete();
    }

    private JRDataSource createDataSource() {
        List<Object[]> resultado;
        if(fechaEntrega == null && territoriotrabajo == null)
        {
            resultado = pedidosFacade.recepcionDePedidos();
        }else{
            resultado = pedidosFacade.recepcionDePedidos(fechaEntrega,territoriotrabajo);
        }        

        DRDataSource dataSource = new DRDataSource("cliente", "producto", "cantidad", "distribuidor");
        for (Object[] obj : resultado) {
            dataSource.add((String)obj[0],(String)obj[1],(Integer)obj[2],(String)obj[3]);
        }
        return dataSource;
    }

    public Date getFechaEntrega() {
        if(fechaEntrega == null)
            fechaEntrega = new Date();
        return fechaEntrega;
    }

    public void setFechaEntrega(Date fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public Territoriotrabajo getTerritoriotrabajo() {
        return territoriotrabajo;
    }

    public void setTerritoriotrabajo(Territoriotrabajo territoriotrabajo) {
        this.territoriotrabajo = territoriotrabajo;
    }
}
