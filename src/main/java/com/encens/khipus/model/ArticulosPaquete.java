/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.model;

import java.io.Serializable;
import java.math.BigInteger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "articulos_paquete")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ArticulosPaquete.findAll", query = "SELECT a FROM ArticulosPaquete a"),
    @NamedQuery(name = "ArticulosPaquete.findByIdarticulospaquete", query = "SELECT a FROM ArticulosPaquete a WHERE a.idarticulospaquete = :idarticulospaquete"),
    @NamedQuery(name = "ArticulosPaquete.findByIdpaquetes", query = "SELECT a FROM ArticulosPaquete a WHERE a.idpaquetes = :idpaquetes"),
    @NamedQuery(name = "ArticulosPaquete.findByIdCuenta", query = "SELECT a FROM ArticulosPaquete a WHERE a.idCuenta = :idCuenta"),
    @NamedQuery(name = "ArticulosPaquete.findByCodArt", query = "SELECT a FROM ArticulosPaquete a WHERE a.codArt = :codArt"),
    @NamedQuery(name = "ArticulosPaquete.findByNoCia", query = "SELECT a FROM ArticulosPaquete a WHERE a.noCia = :noCia"),
    @NamedQuery(name = "ArticulosPaquete.findByCantidad", query = "SELECT a FROM ArticulosPaquete a WHERE a.cantidad = :cantidad"),
    @NamedQuery(name = "ArticulosPaquete.findByCodAlm", query = "SELECT a FROM ArticulosPaquete a WHERE a.codAlm = :codAlm"),
    @NamedQuery(name = "ArticulosPaquete.findByPrecio", query = "SELECT a FROM ArticulosPaquete a WHERE a.precio = :precio"),
    @NamedQuery(name = "ArticulosPaquete.findByTotal", query = "SELECT a FROM ArticulosPaquete a WHERE a.total = :total")})
public class ArticulosPaquete implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "IDARTICULOSPAQUETE")
    private Long idarticulospaquete;
    @Column(name = "IDPAQUETES")
    private BigInteger idpaquetes;
    @Column(name = "ID_CUENTA")
    private BigInteger idCuenta;
    @Size(max = 6)
    @Column(name = "COD_ART")
    private String codArt;
    @Size(max = 2)
    @Column(name = "NO_CIA")
    private String noCia;
    @Column(name = "CANTIDAD")
    private BigInteger cantidad;
    @Size(max = 6)
    @Column(name = "COD_ALM")
    private String codAlm;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "PRECIO")
    private Double precio;
    @Column(name = "TOTAL")
    private Double total;

    public ArticulosPaquete() {
    }

    public ArticulosPaquete(Long idarticulospaquete) {
        this.idarticulospaquete = idarticulospaquete;
    }

    public Long getIdarticulospaquete() {
        return idarticulospaquete;
    }

    public void setIdarticulospaquete(Long idarticulospaquete) {
        this.idarticulospaquete = idarticulospaquete;
    }

    public BigInteger getIdpaquetes() {
        return idpaquetes;
    }

    public void setIdpaquetes(BigInteger idpaquetes) {
        this.idpaquetes = idpaquetes;
    }

    public BigInteger getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(BigInteger idCuenta) {
        this.idCuenta = idCuenta;
    }

    public String getCodArt() {
        return codArt;
    }

    public void setCodArt(String codArt) {
        this.codArt = codArt;
    }

    public String getNoCia() {
        return noCia;
    }

    public void setNoCia(String noCia) {
        this.noCia = noCia;
    }

    public BigInteger getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigInteger cantidad) {
        this.cantidad = cantidad;
    }

    public String getCodAlm() {
        return codAlm;
    }

    public void setCodAlm(String codAlm) {
        this.codAlm = codAlm;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idarticulospaquete != null ? idarticulospaquete.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ArticulosPaquete)) {
            return false;
        }
        ArticulosPaquete other = (ArticulosPaquete) object;
        if ((this.idarticulospaquete == null && other.idarticulospaquete != null) || (this.idarticulospaquete != null && !this.idarticulospaquete.equals(other.idarticulospaquete))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.encens.khipus.model.ArticulosPaquete[ idarticulospaquete=" + idarticulospaquete + " ]";
    }
    
}
