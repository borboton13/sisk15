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

@Named("ventaController")
@SessionScoped
public class VentaController implements Serializable {

    @EJB
    private PersonasFacade personasFacade;
    @EJB
    private InvArticulosFacade invArticulosFacade;
    @EJB
    private VentadirectaFacade ventadirectaFacade;
    @EJB
    SfConfencFacade sfConfencFacade;
    @EJB
    private VentaarticuloFacade ventaarticuloFacade;
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
    private MoneyUtil moneyUtil;

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

    public VentaController() {}




}
