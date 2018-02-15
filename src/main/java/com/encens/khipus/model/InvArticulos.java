/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "inv_articulos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "InvArticulos.findAll", query = "SELECT i FROM InvArticulos i"),
    @NamedQuery(name = "InvArticulos.findByNoCia", query = "SELECT i FROM InvArticulos i WHERE i.invArticulosPK.noCia = :noCia"),
    @NamedQuery(name = "InvArticulos.findByCodArt", query = "SELECT i FROM InvArticulos i WHERE i.invArticulosPK.codArt = :codArt"),
    @NamedQuery(name = "InvArticulos.findByControlValorado", query = "SELECT i FROM InvArticulos i WHERE i.controlValorado = :controlValorado"),
    @NamedQuery(name = "InvArticulos.findByCantiadEqui", query = "SELECT i FROM InvArticulos i WHERE i.cantiadEqui = :cantiadEqui"),
    @NamedQuery(name = "InvArticulos.findByCodMedMay", query = "SELECT i FROM InvArticulos i WHERE i.codMedMay = :codMedMay"),
    @NamedQuery(name = "InvArticulos.findBySaldoMon", query = "SELECT i FROM InvArticulos i WHERE i.saldoMon = :saldoMon"),
    @NamedQuery(name = "InvArticulos.findByStockmaximo", query = "SELECT i FROM InvArticulos i WHERE i.stockmaximo = :stockmaximo"),
    @NamedQuery(name = "InvArticulos.findByStockminimo", query = "SELECT i FROM InvArticulos i WHERE i.stockminimo = :stockminimo"),
    @NamedQuery(name = "InvArticulos.findByDescri", query = "SELECT i FROM InvArticulos i WHERE i.descri = :descri"),
    @NamedQuery(name = "InvArticulos.findByNombrecorto", query = "SELECT i FROM InvArticulos i WHERE i.nombrecorto = :nombrecorto"),
    @NamedQuery(name = "InvArticulos.findByCuentaArt", query = "SELECT i FROM InvArticulos i WHERE i.cuentaArt = :cuentaArt"),
    @NamedQuery(name = "InvArticulos.findByVendible", query = "SELECT i FROM InvArticulos i WHERE i.vendible = :vendible"),
    @NamedQuery(name = "InvArticulos.findByEstado", query = "SELECT i FROM InvArticulos i WHERE i.estado = :estado"),
    @NamedQuery(name = "InvArticulos.findByCodSub", query = "SELECT i FROM InvArticulos i WHERE i.codSub = :codSub"),
    @NamedQuery(name = "InvArticulos.findByCostoUni", query = "SELECT i FROM InvArticulos i WHERE i.costoUni = :costoUni"),
    @NamedQuery(name = "InvArticulos.findByCodMed", query = "SELECT i FROM InvArticulos i WHERE i.codMed = :codMed"),
    @NamedQuery(name = "InvArticulos.findByVersion", query = "SELECT i FROM InvArticulos i WHERE i.version = :version")})
public class InvArticulos implements Serializable {

    @OneToMany(mappedBy = "invArticulos")
    private Collection<ArticulosPedido> articulosPedidoCollection;

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected InvArticulosPK invArticulosPK;

    @Column(name = "COD_ART", length = 6, nullable = false)
    private String productItemCode;

    @Size(max = 255)
    @Column(name = "CONTROL_VALORADO")
    private String controlValorado;

    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "CANTIAD_EQUI")
    private Double cantiadEqui;

    @Size(max = 6)
    @Column(name = "COD_MED_MAY")
    private String codMedMay;

    @Column(name = "SALDO_MON")
    private Double saldoMon;

    @Column(name = "STOCKMAXIMO")
    private Double stockmaximo;

    @Column(name = "STOCKMINIMO")
    private Double stockminimo;

    @Size(max = 100)
    @Column(name = "DESCRI")
    private String descri;

    @Size(max = 14)
    @Column(name = "NOMBRECORTO")
    private String nombrecorto;

    @Size(max = 31)
    @Column(name = "CUENTA_ART")
    private String cuentaArt;

    @Size(max = 255)
    @Column(name = "VENDIBLE")
    private String vendible;

    @Size(max = 3)
    @Column(name = "ESTADO")
    private String estado;

    @Size(max = 3)
    @Column(name = "COD_SUB")
    private String codSub;

    @Column(name = "COSTO_UNI")
    private Double costoUni;

    @Column(name = "CU")
    private Double cu;

    @Column(name = "CT")
    private Double ct;

    @Size(max = 6)
    @Column(name = "COD_MED")
    private String codMed;

    @Column(name = "VERSION")
    private BigInteger version;

    @Size(max = 1)
    @Column(name = "CONTROL_S")
    private String stockControl;

    @JoinColumns({
        @JoinColumn(name = "NO_CIA", referencedColumnName = "NO_CIA", insertable = false, updatable = false),
        @JoinColumn(name = "COD_GRU", referencedColumnName = "COD_GRU")})
    @ManyToOne(optional = false)
    private InvGrupos invGrupos;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "invArticulos")
    private Collection<Ventaarticulo> ventaArticulos;

    public InvArticulos() {
    }

    public InvArticulos(InvArticulosPK invArticulosPK) {
        this.invArticulosPK = invArticulosPK;
    }

    public InvArticulos(String noCia, String codArt) {
        this.invArticulosPK = new InvArticulosPK(noCia, codArt);
    }

    public InvArticulosPK getInvArticulosPK() {
        return invArticulosPK;
    }

    public void setInvArticulosPK(InvArticulosPK invArticulosPK) {
        this.invArticulosPK = invArticulosPK;
    }

    public String getControlValorado() {
        return controlValorado;
    }

    public void setControlValorado(String controlValorado) {
        this.controlValorado = controlValorado;
    }

    public Double getCantiadEqui() {
        return cantiadEqui;
    }

    public void setCantiadEqui(Double cantiadEqui) {
        this.cantiadEqui = cantiadEqui;
    }

    public String getCodMedMay() {
        return codMedMay;
    }

    public void setCodMedMay(String codMedMay) {
        this.codMedMay = codMedMay;
    }

    public Double getSaldoMon() {
        return saldoMon;
    }

    public void setSaldoMon(Double saldoMon) {
        this.saldoMon = saldoMon;
    }

    public Double getStockmaximo() {
        return stockmaximo;
    }

    public void setStockmaximo(Double stockmaximo) {
        this.stockmaximo = stockmaximo;
    }

    public Double getStockminimo() {
        return stockminimo;
    }

    public void setStockminimo(Double stockminimo) {
        this.stockminimo = stockminimo;
    }

    public String getDescri() {
        return descri;
    }

    public void setDescri(String descri) {
        this.descri = descri;
    }

    public String getNombrecorto() {
        return nombrecorto;
    }

    public void setNombrecorto(String nombrecorto) {
        this.nombrecorto = nombrecorto;
    }

    public String getCuentaArt() {
        return cuentaArt;
    }

    public void setCuentaArt(String cuentaArt) {
        this.cuentaArt = cuentaArt;
    }

    public String getVendible() {
        return vendible;
    }

    public void setVendible(String vendible) {
        this.vendible = vendible;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCodSub() {
        return codSub;
    }

    public void setCodSub(String codSub) {
        this.codSub = codSub;
    }

    public Double getCostoUni() {
        return costoUni;
    }

    public void setCostoUni(Double costoUni) {
        this.costoUni = costoUni;
    }

    public String getCodMed() {
        return codMed;
    }

    public void setCodMed(String codMed) {
        this.codMed = codMed;
    }

    public BigInteger getVersion() {
        return version;
    }

    public void setVersion(BigInteger version) {
        this.version = version;
    }

    public InvGrupos getInvGrupos() {
        return invGrupos;
    }

    public void setInvGrupos(InvGrupos invGrupos) {
        this.invGrupos = invGrupos;
    }

    @XmlTransient
    public Collection<Ventaarticulo> getVentaArticulos() {
        return ventaArticulos;
    }

    public void setVentaArticulos(Collection<Ventaarticulo> ventaarticuloCollection) {
        this.ventaArticulos = ventaarticuloCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (invArticulosPK != null ? invArticulosPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof InvArticulos)) {
            return false;
        }
        InvArticulos other = (InvArticulos) object;
        if ((this.invArticulosPK == null && other.invArticulosPK != null) || (this.invArticulosPK != null && !this.invArticulosPK.equals(other.invArticulosPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.encens.khipus.model.InvArticulos[ invArticulosPK=" + invArticulosPK + " ]";
    }

    @XmlTransient
    public Collection<ArticulosPedido> getArticulosPedidoCollection() {
        return articulosPedidoCollection;
    }

    public void setArticulosPedidoCollection(Collection<ArticulosPedido> articulosPedidoCollection) {
        this.articulosPedidoCollection = articulosPedidoCollection;
    }

    /**
     * @return the productItemCode
     */
    public String getProductItemCode() {
        return productItemCode;
    }

    /**
     * @param productItemCode the productItemCode to set
     */
    public void setProductItemCode(String productItemCode) {
        this.productItemCode = productItemCode;
    }

    public String getFullName() {
        return getProductItemCode() + "-" + getDescri();
    }

    public Double getCu() {
        return cu;
    }

    public void setCu(Double cu) {
        this.cu = cu;
    }

    public Double getCt() {
        return ct;
    }

    public void setCt(Double ct) {
        this.ct = ct;
    }

    public String getStockControl() {
        return stockControl;
    }

    public void setStockControl(String stockControl) {
        this.stockControl = stockControl;
    }
}
