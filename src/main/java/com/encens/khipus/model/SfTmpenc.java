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
@Table(name = "sf_tmpenc")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SfTmpenc.findAll", query = "SELECT s FROM SfTmpenc s"),
    @NamedQuery(name = "SfTmpenc.findByIdTmpenc", query = "SELECT s FROM SfTmpenc s WHERE s.idTmpenc = :idTmpenc"),
    @NamedQuery(name = "SfTmpenc.findByNoTrans", query = "SELECT s FROM SfTmpenc s WHERE s.noTrans = :noTrans"),
    @NamedQuery(name = "SfTmpenc.findByAgregarCtaProv", query = "SELECT s FROM SfTmpenc s WHERE s.agregarCtaProv = :agregarCtaProv"),
    @NamedQuery(name = "SfTmpenc.findByMonto", query = "SELECT s FROM SfTmpenc s WHERE s.monto = :monto"),
    @NamedQuery(name = "SfTmpenc.findByCtaBco", query = "SELECT s FROM SfTmpenc s WHERE s.ctaBco = :ctaBco"),
    @NamedQuery(name = "SfTmpenc.findByCuenta", query = "SELECT s FROM SfTmpenc s WHERE s.cuenta = :cuenta"),
    @NamedQuery(name = "SfTmpenc.findBySedePagoChq", query = "SELECT s FROM SfTmpenc s WHERE s.sedePagoChq = :sedePagoChq"),
    @NamedQuery(name = "SfTmpenc.findByNoCia", query = "SELECT s FROM SfTmpenc s WHERE s.noCia = :noCia"),
    @NamedQuery(name = "SfTmpenc.findByMoneda", query = "SELECT s FROM SfTmpenc s WHERE s.moneda = :moneda"),
    @NamedQuery(name = "SfTmpenc.findByFecha", query = "SELECT s FROM SfTmpenc s WHERE s.fecha = :fecha"),
    @NamedQuery(name = "SfTmpenc.findByDescri", query = "SELECT s FROM SfTmpenc s WHERE s.descri = :descri"),
    @NamedQuery(name = "SfTmpenc.findByNoDoc", query = "SELECT s FROM SfTmpenc s WHERE s.noDoc = :noDoc"),
    @NamedQuery(name = "SfTmpenc.findByTipoDoc", query = "SELECT s FROM SfTmpenc s WHERE s.tipoDoc = :tipoDoc"),
    @NamedQuery(name = "SfTmpenc.findByBeneficiario", query = "SELECT s FROM SfTmpenc s WHERE s.beneficiario = :beneficiario"),
    @NamedQuery(name = "SfTmpenc.findByTc", query = "SELECT s FROM SfTmpenc s WHERE s.tc = :tc"),
    @NamedQuery(name = "SfTmpenc.findByFechaVen", query = "SELECT s FROM SfTmpenc s WHERE s.fechaVen = :fechaVen"),
    @NamedQuery(name = "SfTmpenc.findByFormulario", query = "SELECT s FROM SfTmpenc s WHERE s.formulario = :formulario"),
    @NamedQuery(name = "SfTmpenc.findByGlosa", query = "SELECT s FROM SfTmpenc s WHERE s.glosa = :glosa"),
    @NamedQuery(name = "SfTmpenc.findByObservacion", query = "SELECT s FROM SfTmpenc s WHERE s.observacion = :observacion"),
    @NamedQuery(name = "SfTmpenc.findByPendienteRegistro", query = "SELECT s FROM SfTmpenc s WHERE s.pendienteRegistro = :pendienteRegistro"),
    @NamedQuery(name = "SfTmpenc.findByCodProv", query = "SELECT s FROM SfTmpenc s WHERE s.codProv = :codProv"),
    @NamedQuery(name = "SfTmpenc.findByEntregadoA", query = "SELECT s FROM SfTmpenc s WHERE s.entregadoA = :entregadoA"),
    @NamedQuery(name = "SfTmpenc.findByNoTransRel", query = "SELECT s FROM SfTmpenc s WHERE s.noTransRel = :noTransRel"),
    @NamedQuery(name = "SfTmpenc.findByProcedencia", query = "SELECT s FROM SfTmpenc s WHERE s.procedencia = :procedencia"),
    @NamedQuery(name = "SfTmpenc.findByEstado", query = "SELECT s FROM SfTmpenc s WHERE s.estado = :estado"),
    @NamedQuery(name = "SfTmpenc.findByNoUsr", query = "SELECT s FROM SfTmpenc s WHERE s.noUsr = :noUsr"),
    @NamedQuery(name = "SfTmpenc.findByCodEnti", query = "SELECT s FROM SfTmpenc s WHERE s.codEnti = :codEnti")})
public class SfTmpenc implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @NotNull
    @TableGenerator(name = "sf_tmpenc"
            ,table="secuencia"
            ,pkColumnName = "tabla"
            ,valueColumnName = "valor"
            ,initialValue = 1
            ,allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "sf_tmpenc")
    @Column(name = "id_tmpenc")
    private Long idTmpenc;
    @Size(max = 10)
    @Column(name = "no_trans")
    private String noTrans;
    @Size(max = 255)
    @Column(name = "agregar_cta_prov")
    private String agregarCtaProv;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "monto")
    private BigDecimal monto;
    @Size(max = 20)
    @Column(name = "cta_bco")
    private String ctaBco;
    @Size(max = 20)
    @Column(name = "cuenta")
    private String cuenta;
    @Size(max = 8)
    @Column(name = "sede_pago_chq")
    private String sedePagoChq;
    @Size(max = 20)
    @Column(name = "no_cia")
    private String noCia;
    @Size(max = 255)
    @Column(name = "moneda")
    private String moneda;

    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;

    @JoinColumn(name = "idmovimiento", referencedColumnName = "idmovimiento")
    @ManyToOne(optional = true)
    private Movimiento movimiento;

    @Column(name = "fac")
    private Integer nrofactura;

    @Size(max = 1000)
    @Column(name = "descri")
    private String descri;
    @Size(max = 20)
    @Column(name = "no_doc")
    private String noDoc;
    @Basic
    @Size(max = 3)
    @Column(name = "tipo_doc")
    private String tipoDoc;
    @Size(max = 100)
    @Column(name = "beneficiario")
    private String beneficiario;
    @Column(name = "tc")
    private BigDecimal tc;
    @Column(name = "fecha_ven")
    @Temporal(TemporalType.DATE)
    private Date fechaVen;
    @Size(max = 30)
    @Column(name = "formulario")
    private String formulario;
    @Size(max = 1000)
    @Column(name = "glosa")
    private String glosa;
    @Size(max = 1000)
    @Column(name = "observacion")
    private String observacion;
    @Size(max = 2)
    @Column(name = "pendiente_registro")
    private String pendienteRegistro;
    @Size(max = 6)
    @Column(name = "cod_prov")
    private String codProv;
    @Size(max = 660)
    @Column(name = "entregado_a")
    private String entregadoA;
    @Size(max = 10)
    @Column(name = "no_trans_rel")
    private String noTransRel;
    @Size(max = 3)
    @Column(name = "procedencia")
    private String procedencia;
    @Size(max = 3)
    @Column(name = "estado")
    private String estado;
    @Size(max = 4)
    @Column(name = "no_usr")
    private String noUsr;
    @Size(max = 6)
    @Column(name = "cod_enti")
    private String codEnti;

    @JoinColumn(name = "idusuario",referencedColumnName = "idusuario")
    @ManyToOne
    private Usuario usuario;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sfTmpenc")
    private Collection<SfTmpdet> asientos = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "asiento")
    private Collection<Pedidos> pedidos = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "asiento")
    private Collection<Pago> pagos = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "asiento")
    private Collection<Ventadirecta> ventadirectas = new ArrayList<>();

    @JoinColumn(name = "IDPERSONACLIENTE")
    @ManyToOne
    private Persona cliente;

    @Basic
    @Column(name = "NOMBRECLIENTE")
    private String nombreCliente;
    @Basic
    @Column(name = "DEBE")
    private Double debe;
    @Basic
    @Column(name = "HABER")
    private Double haber;

    public SfTmpenc() {
    }

    public SfTmpenc(Long idTmpenc) {
        this.idTmpenc = idTmpenc;
    }

    public Long getIdTmpenc() {
        return idTmpenc;
    }

    public void setIdTmpenc(Long idTmpenc) {
        this.idTmpenc = idTmpenc;
    }

    public String getNoTrans() {
        return noTrans;
    }

    public void setNoTrans(String noTrans) {
        this.noTrans = noTrans;
    }

    public String getAgregarCtaProv() {
        return agregarCtaProv;
    }

    public void setAgregarCtaProv(String agregarCtaProv) {
        this.agregarCtaProv = agregarCtaProv;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getCtaBco() {
        return ctaBco;
    }

    public void setCtaBco(String ctaBco) {
        this.ctaBco = ctaBco;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getSedePagoChq() {
        return sedePagoChq;
    }

    public void setSedePagoChq(String sedePagoChq) {
        this.sedePagoChq = sedePagoChq;
    }

    public String getNoCia() {
        return noCia;
    }

    public void setNoCia(String noCia) {
        this.noCia = noCia;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getDescri() {
        return descri;
    }

    public void setDescri(String descri) {
        this.descri = descri;
    }

    public String getNoDoc() {
        return noDoc;
    }

    public void setNoDoc(String noDoc) {
        this.noDoc = noDoc;
    }

    public String getTipoDoc() {
        return tipoDoc;
    }

    public void setTipoDoc(String tipoDoc) {
        this.tipoDoc = tipoDoc;
    }

    public String getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(String beneficiario) {
        this.beneficiario = beneficiario;
    }

    public BigDecimal getTc() {
        return tc;
    }

    public void setTc(BigDecimal tc) {
        this.tc = tc;
    }

    public Date getFechaVen() {
        return fechaVen;
    }

    public void setFechaVen(Date fechaVen) {
        this.fechaVen = fechaVen;
    }

    public String getFormulario() {
        return formulario;
    }

    public void setFormulario(String formulario) {
        this.formulario = formulario;
    }

    public String getGlosa() {
        return glosa;
    }

    public void setGlosa(String glosa) {
        this.glosa = glosa;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getPendienteRegistro() {
        return pendienteRegistro;
    }

    public void setPendienteRegistro(String pendienteRegistro) {
        this.pendienteRegistro = pendienteRegistro;
    }

    public String getCodProv() {
        return codProv;
    }

    public void setCodProv(String codProv) {
        this.codProv = codProv;
    }

    public String getEntregadoA() {
        return entregadoA;
    }

    public void setEntregadoA(String entregadoA) {
        this.entregadoA = entregadoA;
    }

    public String getNoTransRel() {
        return noTransRel;
    }

    public void setNoTransRel(String noTransRel) {
        this.noTransRel = noTransRel;
    }

    public String getProcedencia() {
        return procedencia;
    }

    public void setProcedencia(String procedencia) {
        this.procedencia = procedencia;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNoUsr() {
        return noUsr;
    }

    public void setNoUsr(String noUsr) {
        this.noUsr = noUsr;
    }

    public String getCodEnti() {
        return codEnti;
    }

    public void setCodEnti(String codEnti) {
        this.codEnti = codEnti;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idTmpenc != null ? idTmpenc.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SfTmpenc)) {
            return false;
        }
        SfTmpenc other = (SfTmpenc) object;
        if ((this.idTmpenc == null && other.idTmpenc != null) || (this.idTmpenc != null && !this.idTmpenc.equals(other.idTmpenc))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.encens.khipus.model.SfTmpenc[ idTmpenc=" + idTmpenc + " ]";
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Collection<SfTmpdet> getAsientos() {
        return asientos;
    }

    public void setAsientos(Collection<SfTmpdet> asientos) {
        this.asientos = asientos;
    }

    public Collection<Pedidos> getPedidos() {
        return pedidos;
    }

    public void setPedidos(Collection<Pedidos> pedidos) {
        this.pedidos = pedidos;
    }

    public Collection<Pago> getPagos() {
        return pagos;
    }

    public void setPagos(Collection<Pago> pagos) {
        this.pagos = pagos;
    }

    public Collection<Ventadirecta> getVentadirectas() {
        return ventadirectas;
    }

    public void setVentadirectas(Collection<Ventadirecta> ventadirectas) {
        this.ventadirectas = ventadirectas;
    }

    public Persona getCliente() {
        return cliente;
    }

    public void setCliente(Persona cliente) {
        this.cliente = cliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public Double getDebe() {
        return debe;
    }

    public void setDebe(Double debe) {
        this.debe = debe;
    }

    public Double getHaber() {
        return haber;
    }

    public void setHaber(Double haber) {
        this.haber = haber;
    }
    
    public String getDocumentAll(){
        return tipoDoc + " - " + noDoc + " - " + estado;
    }

    public Movimiento getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(Movimiento movimiento) {
        this.movimiento = movimiento;
    }

    public Integer getNrofactura() {
        return nrofactura;
    }

    public void setNrofactura(Integer nrofactura) {
        this.nrofactura = nrofactura;
    }
}
