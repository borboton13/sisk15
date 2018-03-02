/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "pedidos")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Pedidos.findAll", query = "SELECT p FROM Pedidos p"),
        @NamedQuery(name = "Pedidos.findByIdpedidos", query = "SELECT p FROM Pedidos p WHERE p.idpedidos = :idpedidos"),
        @NamedQuery(name = "Pedidos.findByDescripcion", query = "SELECT p FROM Pedidos p WHERE p.descripcion = :descripcion"),
        @NamedQuery(name = "Pedidos.findByFechaPedido", query = "SELECT p FROM Pedidos p WHERE p.fechaPedido = :fechaPedido"),
        @NamedQuery(name = "Pedidos.findByIddireccion", query = "SELECT p FROM Pedidos p WHERE p.iddireccion = :iddireccion"),
        @NamedQuery(name = "Pedidos.findByTotal", query = "SELECT p FROM Pedidos p WHERE p.total = :total"),
        @NamedQuery(name = "Pedidos.findByFechaEntrega", query = "SELECT p FROM Pedidos p WHERE p.fechaEntrega = :fechaEntrega"),
        @NamedQuery(name = "Pedidos.findByFechaAPagar", query = "SELECT p FROM Pedidos p WHERE p.fechaAPagar = :fechaAPagar"),
        @NamedQuery(name = "Pedidos.findByObservacion", query = "SELECT p FROM Pedidos p WHERE p.observacion = :observacion"),
        @NamedQuery(name = "Pedidos.findByFactura", query = "SELECT p FROM Pedidos p WHERE p.factura = :factura"),
        @NamedQuery(name = "Pedidos.findBySupervisor", query = "SELECT p FROM Pedidos p WHERE p.supervisor = :supervisor"),
        @NamedQuery(name = "Pedidos.findByPorcenDescuento", query = "SELECT p FROM Pedidos p WHERE p.porcentajeComision = :porcenDescuento"),
        @NamedQuery(name = "Pedidos.findByPorcenRetencion", query = "SELECT p FROM Pedidos p WHERE p.porcentajeGarantia = :porcenRetencion")})
public class Pedidos implements Serializable {

    private static final long serialVersionUID = 1L;
    //todo:revisar por q el id no es correlativo
    @Id
    @NotNull
    @TableGenerator(name = "Pedidos_Gen"
            ,table="ID_GEN"
            ,pkColumnName = "GEN_NAME"
            ,valueColumnName = "GEN_VAL"
            ,initialValue = 1
            ,allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Pedidos_Gen")
    @Column(name = "IDPEDIDOS")
    private Long idpedidos;

    @Size(max = 200)
    @Column(name = "DESCRIPCION")
    private String descripcion;

    @NotNull
    @Column(name = "FECHA_PEDIDO")
    @Temporal(TemporalType.DATE)
    private Date fechaPedido;

    @Column(name = "IDDIRECCION")
    private BigInteger iddireccion;

    @Basic(optional = false)
    @Column(name = "FECHA_ENTREGA")
    @Temporal(TemporalType.DATE)
    private Date fechaEntrega;

    @Column(name = "FECHA_A_PAGAR")
    @Temporal(TemporalType.DATE)
    private Date fechaAPagar;

    @Size(max = 100)
    @Column(name = "OBSERVACION")
    private String observacion;

    @Column(name = "FACTURA")
    private String factura;

    @Column(name = "SUPERVISOR")
    private BigInteger supervisor;

    @Column(name = "PORCENTAJECOMISION")
    private Double porcentajeComision = 0.0;

    @Column(name = "PORCENTAJEGARANTIA")
    private Double porcentajeGarantia = 0.0;

    @Column(name = "VALORCOMISION")
    private Double valorComision = 0.0;

    @Column(name = "VALORGARANTIA")
    private Double valorGarantia = 0.0;

    @Column(name = "ESTADO")
    private String estado;

    @Column(name = "CON_REPOSICION")
    private Boolean conReposicion = false;

    @Column(name = "flagstock")
    private Boolean flagstock = false;

    @Column(name = "CV")
    private Boolean cvFlag = false;

    @Column(name = "CODIGO")
    @Basic
    private BigInteger codigo;

    @JoinColumn(name = "IDCLIENTE", referencedColumnName = "IDPERSONACLIENTE")
    @ManyToOne(optional = false)
    private Persona cliente;

    @JoinColumn(name = "IDTIPOPEDIDO", referencedColumnName = "IDTIPOPEDIDO")
    @ManyToOne(optional = false)
    private Tipopedido idtipopedido;

    @JoinColumn(name = "IDUSUARIO",referencedColumnName = "IDUSUARIO")
    @ManyToOne
    private Usuario usuario;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "pedidos")
    private Collection<ArticulosPedido> articulosPedidos = new ArrayList<>();

    @JoinColumn(name = "IDMOVIMIENTO",referencedColumnName = "IDMOVIMIENTO")
    @ManyToOne(optional = true)
    private Movimiento movimiento;

    @Column(name = "TOTAL")
    private Double total = 0.0;
    //cantidad * precio de venta

    @Basic(optional = false)
    @Column(name = "TOTALIMPORTE")
    private Double totalimporte = 0.0;

    @Column(name = "IMPUESTO")
    private Double impuesto = 0.0;

    @JoinColumn(name = "PEDIDOSREPOSICION", referencedColumnName = "IDPEDIDOS")
    @ManyToOne(optional = false)
    private Pedidos reposicion;

    @OneToMany(cascade = CascadeType.PERSIST,mappedBy = "reposicion")
    private Collection<Pedidos> pedidosConReposicion;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_tmpenc", referencedColumnName = "id_tmpenc")
    private SfTmpenc asiento;

    @JoinColumn(name = "id_tmpenc_cv", referencedColumnName = "id_tmpenc")
    @ManyToOne
    private SfTmpenc asientocv;

    @Basic
    @Column(name = "CONTABILIZADO")
    private Boolean contabilizado;

    @Basic
    @Column(name = "TIENEFACTURA")
    private Boolean tieneFactura = true;

    @JoinColumn(name = "IDSUCURSAL", referencedColumnName = "IDSUCURSAL")
    @ManyToOne(optional = true)
    private Sucursal sucursal;

    public Pedidos() {
    }

    public Pedidos(Long idpedidos) {
        this.idpedidos = idpedidos;
    }

    public Pedidos(Long idpedidos, Date fechaPedido,Double total, Date fechaAPagar, String factura) {
        this.idpedidos = idpedidos;
        this.fechaPedido = fechaPedido;
        this.total = total;
        this.fechaAPagar = fechaAPagar;
        this.factura = factura;
    }

    public Long getIdpedidos() {
        return idpedidos;
    }

    public void setIdpedidos(Long idpedidos) {
        this.idpedidos = idpedidos;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(Date fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public BigInteger getIddireccion() {
        return iddireccion;
    }

    public void setIddireccion(BigInteger iddireccion) {
        this.iddireccion = iddireccion;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Date getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Date fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public Date getFechaAPagar() {
        return fechaAPagar;
    }

    public void setFechaAPagar(Date fechaAPagar) {
        this.fechaAPagar = fechaAPagar;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getFactura() {
        return factura;
    }

    public void setFactura(String factura) {
        this.factura = factura;
    }

    public BigInteger getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(BigInteger supervisor) {
        this.supervisor = supervisor;
    }

    public Double getPorcentajeComision() {
        return porcentajeComision;
    }

    public void setPorcentajeComision(Double porcenDescuento) {
        this.porcentajeComision = porcenDescuento;
    }

    public Double getPorcentajeGarantia() {
        return porcentajeGarantia;
    }

    public void setPorcentajeGarantia(Double porcenRetencion) {
        this.porcentajeGarantia = porcenRetencion;
    }

    public Persona getCliente() {
        return cliente;
    }

    public void setCliente(Persona cliente) {
        this.porcentajeComision = cliente.getPorcentajeComision();
        this.porcentajeGarantia = cliente.getPorcentajeGarantia();
        this.cliente = cliente;
    }

    public Tipopedido getIdtipopedido() {
        return idtipopedido;
    }

    public void setIdtipopedido(Tipopedido idtipopedido) {
        this.idtipopedido = idtipopedido;
    }

    public String getEstado() {
        if(estado == null)
        {
            estado = "ACTIVO";
        }
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idpedidos != null ? idpedidos.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Pedidos)) {
            return false;
        }
        Pedidos other = (Pedidos) object;
        if ((this.idpedidos == null && other.idpedidos != null) || (this.idpedidos != null && !this.idpedidos.equals(other.idpedidos))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.encens.khipus.model.Pedidos[ idpedidos=" + idpedidos + " ]";
    }

    public Collection<ArticulosPedido> getArticulosPedidos() {
        return articulosPedidos;
    }

    public void setArticulosPedidos(Collection<ArticulosPedido> articulosPedidos) {
        this.articulosPedidos = articulosPedidos;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
            this.usuario = usuario;
    }

    public Double getValorComision() {
        return valorComision;
    }

    public void setValorComision(Double valorDescuento) {
        this.valorComision = valorDescuento;
    }

    public Double getValorGarantia() {
        return valorGarantia;
    }

    public void setValorGarantia(Double valorRetencion) {
        this.valorGarantia = valorRetencion;
    }

    public Movimiento getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(Movimiento movimiento) {
        this.movimiento = movimiento;
    }

    public Double getTotalimporte() {
        totalimporte = 0.0;
        Double cien = new Double("100.0");
        if(articulosPedidos != null)
        for(ArticulosPedido articulosPedido:articulosPedidos)
        {
            totalimporte += articulosPedido.getImporte();
        }
        valorComision = totalimporte * (porcentajeComision / cien);
        
        return totalimporte;
    }

    public void setTotalimporte(Double totalimporte) {
        this.totalimporte = totalimporte;
    }

    public BigInteger getCodigo() {
        return codigo;
    }

    public void setCodigo(BigInteger codigo) {
        this.codigo = codigo;
    }

    public Boolean getConReposicion() {
        return conReposicion;
    }

    public void setConReposicion(Boolean conReposicion) {
        this.conReposicion = conReposicion;
    }

    public Pedidos getReposicion() {
        return reposicion;
    }

    public void setReposicion(Pedidos reposicion) {
        this.reposicion = reposicion;
    }

    public Collection<Pedidos> getPedidosConReposicion() {
        return pedidosConReposicion;
    }

    public void setPedidosConReposicion(Collection<Pedidos> pedidosConReposicion) {
        this.pedidosConReposicion = pedidosConReposicion;
    }

    public Double getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(Double impuesto) {
        this.impuesto = impuesto;
    }

    public SfTmpenc getAsiento() {
        return asiento;
    }

    public void setAsiento(SfTmpenc sfTmpenc) {
        this.asiento = sfTmpenc;
    }

    public Boolean getContabilizado() {
        return contabilizado;
    }

    public void setContabilizado(Boolean contabilizado) {
        this.contabilizado = contabilizado;
    }

    public Boolean getTieneFactura() {
        return tieneFactura;
    }

    public void setTieneFactura(Boolean tieneFactura) {
        this.tieneFactura = tieneFactura;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

    /**
     * @return the flagstock
     */
    public Boolean getFlagstock() {
        return flagstock;
    }

    /**
     * @param flagstock the flagstock to set
     */
    public void setFlagstock(Boolean flagstock) {
        this.flagstock = flagstock;
    }

    public SfTmpenc getAsientocv() {
        return asientocv;
    }

    public void setAsientocv(SfTmpenc asientocv) {
        this.asientocv = asientocv;
    }

    public Boolean getCvFlag() {
        return cvFlag;
    }

    public void setCvFlag(Boolean cvFlag) {
        this.cvFlag = cvFlag;
    }

    public Boolean facturado(){
        return movimiento != null;
    }
}
