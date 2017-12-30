/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.controller;

import com.encens.khipus.ejb.PedidosFacade;
import com.encens.khipus.model.Territoriotrabajo;
import com.encens.khipus.util.Templates;
import net.sf.dynamicreports.report.builder.crosstab.CrosstabBuilder;
import net.sf.dynamicreports.report.builder.crosstab.CrosstabColumnGroupBuilder;
import net.sf.dynamicreports.report.builder.crosstab.CrosstabMeasureBuilder;
import net.sf.dynamicreports.report.builder.crosstab.CrosstabRowGroupBuilder;
import net.sf.dynamicreports.report.constant.Calculation;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static net.sf.dynamicreports.report.builder.DynamicReports.*;

/**
 *
 * @author Diego
 */
@Named(value = "recepcionPedidoReportController")
@RequestScoped
public class RecepcionPedidoReportController {
    @EJB
    private PedidosFacade pedidosFacade;
    private Date fechaEntrega;
    private Territoriotrabajo territoriotrabajo;
    private Integer cantidadPedidos;

    /**
     * Creates a new instance of RecepcionPedidoReportController
     */
    public RecepcionPedidoReportController() {
    }

    private void build(ServletOutputStream stream) {
        String fecha;
        JRDataSource dataSource = createDataSource();
        if(fechaEntrega == null)
            fecha = "(TODOS)";
        else
            fecha = fechaEntrega.toString();
        String titulo = "PEDIDOS CORRESPONDIENTES AL:"+fecha+"     "+"CANTIDAD: "+cantidadPedidos.toString();

        CrosstabRowGroupBuilder<String> rowGroup = ctab.rowGroup("cliente", String.class)
                .setTotalHeader("Total:");

        CrosstabColumnGroupBuilder<String> columnGroup = ctab.columnGroup("producto", String.class).setShowTotal(false);
        CrosstabRowGroupBuilder<String> rowItemGroup = ctab.rowGroup("distribuidor", String.class);
        CrosstabMeasureBuilder<String> cantidad = ctab.measure("cantidad", Integer.class, Calculation.COUNT);

        CrosstabBuilder crosstab = ctab.crosstab()
                .headerCell(cmp.text("Cliente / Producto").setStyle(Templates.boldCenteredStyle))
                .setCellWidth(10)
                .rowGroups(rowItemGroup, rowGroup)
                .columnGroups(columnGroup)
                .addMeasure(cantidad);

        try {
            report()
                    .setPageFormat(PageType.A4, PageOrientation.LANDSCAPE)
                    .setTemplate(Templates.reportTemplate)
                    .title(Templates.createTitleComponent(titulo))
                    .summary(crosstab)
                    .pageFooter(Templates.footerComponent)
                    .setDataSource(dataSource)
                    .toPdf(stream);
        } catch (DRException e) {
            e.printStackTrace();
        }
    }

    public void imprimir() throws IOException, JRException {
        exportarPDF();
    }

    private void exportarPDF() throws JRException, IOException {


        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        response.addHeader("Content-disposition","attachment; filename=RecepcionPedios.pdf");
        ServletOutputStream stream = response.getOutputStream();
        build(stream);
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
        cantidadPedidos = pedidosFacade.getTotalPedidos(fechaEntrega,territoriotrabajo);

        DRDataSource dataSource = new DRDataSource("cliente", "producto", "cantidad", "distribuidor");
        for (Object[] obj : resultado) {
            dataSource.add(obj[0], obj[1], obj[2], obj[3]);
        }
        return dataSource;
    }

    public Date getFechaEntrega() {
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
