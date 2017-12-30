/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "movimiento")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Movimiento.findAll", query = "SELECT m FROM Movimiento m"),
    @NamedQuery(name = "Movimiento.findByIdmovimiento", query = "SELECT m FROM Movimiento m WHERE m.idmovimiento = :idmovimiento"),
    @NamedQuery(name = "Movimiento.findByGlosa", query = "SELECT m FROM Movimiento m WHERE m.glosa = :glosa"),
    @NamedQuery(name = "Movimiento.findByEstado", query = "SELECT m FROM Movimiento m WHERE m.estado = :estado"),
    @NamedQuery(name = "Movimiento.findByNrofactura", query = "SELECT m FROM Movimiento m WHERE m.nrofactura = :nrofactura"),
    @NamedQuery(name = "Movimiento.findByCodigocontrol", query = "SELECT m FROM Movimiento m WHERE m.codigocontrol = :codigocontrol"),
    @NamedQuery(name = "Movimiento.findByTipopago", query = "SELECT m FROM Movimiento m WHERE m.tipopago = :tipopago"),
    @NamedQuery(name = "Movimiento.findByTipocambio", query = "SELECT m FROM Movimiento m WHERE m.tipocambio = :tipocambio"),
    @NamedQuery(name = "Movimiento.findByFecharegistro", query = "SELECT m FROM Movimiento m WHERE m.fecharegistro = :fecharegistro")})
public class Movimiento implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @NotNull
    @TableGenerator(name = "Movimiento_Gen"
            ,table="ID_GEN"
            ,pkColumnName = "GEN_NAME"
            ,valueColumnName = "GEN_VAL"
            ,initialValue = 1
            ,allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Movimiento_Gen")
    @Column(name = "IDMOVIMIENTO")
    private Long idmovimiento;

    @Size(max = 300)
    @Column(name = "GLOSA")
    private String glosa;

    @Size(max = 15)
    @Column(name = "ESTADO")
    private String estado;

    @Column(name = "NROFACTURA")
    private Integer nrofactura;

    @Size(max = 30)
    @Column(name = "CODIGOCONTROL")
    private String codigocontrol;

    @Size(max = 10)
    @Column(name = "TIPOPAGO")
    private String tipopago;

    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "TIPOCAMBIO")
    private Double tipocambio;

    @Basic
    @Column(name = "CODIGO_QR")
    private String codigoQR;
    
    @Column(name = "NRO_AUTORIZACION")
    @Size(max = 15)
    private String nroAutorizacion;

    @Column(name = "FECHA_FACTURA")
    @Temporal(TemporalType.DATE)
    private Date fechaFactura;

    @Size(max = 50)
    @Column(name = "NIT_CLIENTE")
    private String nitCliente;

    @Size(max = 200)
    @Column(name = "RAZON_SOCIAL")
    private String razonSocial;

    @Column(name = "IMPORTE_TOTAL", precision = 10, scale = 2)
    private BigDecimal importeTotal;

    @Column(name = "IMPORTE_ICE_IEHD_TASAS", precision = 10, scale = 2)
    private BigDecimal importeIceTasas;

    @Column(name = "EXPORT_EXENTAS", precision = 10, scale = 2)
    private BigDecimal exportExentas;

    @Column(name = "VENTAS_GRAB_TASACERO", precision = 10, scale = 2)
    private BigDecimal ventasGrabTasaCero;

    @Column(name = "SUBTOTAL", precision = 10, scale = 2)
    private BigDecimal subTotal;

    @Column(name = "DESCUENTOS", precision = 10, scale = 2)
    private BigDecimal descuentos;

    @Column(name = "IMPORTE_PARA_DEBITO_FISCAL", precision = 10, scale = 2)
    private BigDecimal importeParaDebitoFiscal;

    @Column(name = "DEBITO_FISCAL", precision = 10, scale = 2)
    private BigDecimal debitoFiscal;

    @ManyToOne
    @JoinColumn(name = "IDPEDIDOS", referencedColumnName = "IDPEDIDOS")
    private Pedidos pedido;

    @ManyToOne
    @JoinColumn(name = "IDVENTADIRECTA", referencedColumnName = "IDVENTADIRECTA")
    private Ventadirecta ventadirecta;

    @Basic(optional = false)
    @NotNull
    @Column(name = "FECHAREGISTRO")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecharegistro;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "movimiento")
    private Collection<Impresionfactura> impresionfacturaCollection = new ArrayList<>();

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "movimiento")
    private Collection<Pedidos> pedidos = new ArrayList<>();

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "movimiento")
    private Collection<Ventadirecta> ventadirectas = new ArrayList<>();

    public Movimiento() {
    }

    public Movimiento(Long idmovimiento) {
        this.idmovimiento = idmovimiento;
    }

    public Movimiento(Long idmovimiento, Date fecharegistro) {
        this.idmovimiento = idmovimiento;
        this.fecharegistro = fecharegistro;
    }

    public Long getIdmovimiento() {
        return idmovimiento;
    }

    public void setIdmovimiento(Long idmovimiento) {
        this.idmovimiento = idmovimiento;
    }

    public String getGlosa() {
        return glosa;
    }

    public void setGlosa(String glosa) {
        this.glosa = glosa;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getNrofactura() {
        return nrofactura;
    }

    public void setNrofactura(Integer nrofactura) {
        this.nrofactura = nrofactura;
    }

    public String getCodigocontrol() {
        return codigocontrol;
    }

    public void setCodigocontrol(String codigocontrol) {
        this.codigocontrol = codigocontrol;
    }

    public String getTipopago() {
        return tipopago;
    }

    public void setTipopago(String tipopago) {
        this.tipopago = tipopago;
    }

    public Double getTipocambio() {
        return tipocambio;
    }

    public void setTipocambio(Double tipocambio) {
        this.tipocambio = tipocambio;
    }

    public Date getFecharegistro() {
        return fecharegistro;
    }

    public void setFecharegistro(Date fecharegistro) {
        this.fecharegistro = fecharegistro;
    }

    @XmlTransient
    public Collection<Impresionfactura> getImpresionfacturaCollection() {
        return impresionfacturaCollection;
    }

    public void setImpresionfacturaCollection(Collection<Impresionfactura> impresionfacturaCollection) {
        this.impresionfacturaCollection = impresionfacturaCollection;
    }

    public Collection<Pedidos> getPedidos() {
        return pedidos;
    }

    public void setPedidos(Collection<Pedidos> pedidos) {
        this.pedidos = pedidos;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idmovimiento != null ? idmovimiento.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Movimiento)) {
            return false;
        }
        Movimiento other = (Movimiento) object;
        if ((this.idmovimiento == null && other.idmovimiento != null) || (this.idmovimiento != null && !this.idmovimiento.equals(other.idmovimiento))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.encens.khipus.model.Movimiento[ idmovimiento=" + idmovimiento + " ]";
    }

    public Collection<Ventadirecta> getVentadirectas() {
        return ventadirectas;
    }

    public void setVentadirectas(Collection<Ventadirecta> ventadirectas) {
        this.ventadirectas = ventadirectas;
    }

    public String getCodigoQR() {
        return codigoQR;
    }

    public void setCodigoQR(String vodigoQR) {
        this.codigoQR = vodigoQR;
    }

    /**
     * @return the nroAutorizacion
     */
    public String getNroAutorizacion() {
        return nroAutorizacion;
    }

    /**
     * @param nroAutorizacion the nroAutorizacion to set
     */
    public void setNroAutorizacion(String nroAutorizacion) {
        this.nroAutorizacion = nroAutorizacion;
    }

    public Date getFechaFactura() {
        return fechaFactura;
    }

    public void setFechaFactura(Date fechaFactura) {
        this.fechaFactura = fechaFactura;
    }

    public String getNitCliente() {
        return nitCliente;
    }

    public void setNitCliente(String nitCliente) {
        this.nitCliente = nitCliente;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public BigDecimal getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(BigDecimal importeTotal) {
        this.importeTotal = importeTotal;
    }

    public BigDecimal getImporteIceTasas() {
        return importeIceTasas;
    }

    public void setImporteIceTasas(BigDecimal importeIceTasas) {
        this.importeIceTasas = importeIceTasas;
    }

    public BigDecimal getExportExentas() {
        return exportExentas;
    }

    public void setExportExentas(BigDecimal exportExentas) {
        this.exportExentas = exportExentas;
    }

    public BigDecimal getVentasGrabTasaCero() {
        return ventasGrabTasaCero;
    }

    public void setVentasGrabTasaCero(BigDecimal ventasGrabTasaCero) {
        this.ventasGrabTasaCero = ventasGrabTasaCero;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public BigDecimal getDescuentos() {
        return descuentos;
    }

    public void setDescuentos(BigDecimal descuentos) {
        this.descuentos = descuentos;
    }

    public BigDecimal getImporteParaDebitoFiscal() {
        return importeParaDebitoFiscal;
    }

    public void setImporteParaDebitoFiscal(BigDecimal importeParaDebitoFiscal) {
        this.importeParaDebitoFiscal = importeParaDebitoFiscal;
    }

    public BigDecimal getDebitoFiscal() {
        return debitoFiscal;
    }

    public void setDebitoFiscal(BigDecimal debitoFiscal) {
        this.debitoFiscal = debitoFiscal;
    }

    public Pedidos getPedido() {
        return pedido;
    }

    public void setPedido(Pedidos pedido) {
        this.pedido = pedido;
    }

    public Ventadirecta getVentadirecta() {
        return ventadirecta;
    }

    public void setVentadirecta(Ventadirecta ventadirecta) {
        this.ventadirecta = ventadirecta;
    }
}
