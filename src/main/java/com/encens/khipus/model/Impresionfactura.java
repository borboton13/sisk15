/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "impresionfactura")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Impresionfactura.findAll", query = "SELECT i FROM Impresionfactura i"),
    @NamedQuery(name = "Impresionfactura.findByIdimprecionfactura", query = "SELECT i FROM Impresionfactura i WHERE i.idimprecionfactura = :idimprecionfactura"),
    @NamedQuery(name = "Impresionfactura.findByFechaimpresion", query = "SELECT i FROM Impresionfactura i WHERE i.fechaimpresion = :fechaimpresion"),
    @NamedQuery(name = "Impresionfactura.findByTipo", query = "SELECT i FROM Impresionfactura i WHERE i.tipo = :tipo")})
public class Impresionfactura implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @NotNull
    @TableGenerator(name = "Impresionfactura_Gen"
            ,table="ID_GEN"
            ,pkColumnName = "GEN_NAME"
            ,valueColumnName = "GEN_VAL"
            ,initialValue = 1
            ,allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Impresionfactura_Gen")
    @Column(name = "IDIMPRECIONFACTURA")
    private Long idimprecionfactura;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FECHAIMPRESION")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaimpresion;
    @Size(max = 10)
    @Column(name = "TIPO")
    private String tipo;
    @Column(name = "NROFACTURA")
    private Integer nroFactura;
    @JoinColumn(name = "IDMOVIMIENTO", referencedColumnName = "IDMOVIMIENTO")
    @ManyToOne(optional = false)
    private Movimiento movimiento;
    @JoinColumn(name = "IDUSUARIO", referencedColumnName = "IDUSUARIO")
    @ManyToOne(optional = false)
    private Usuario usuario;
    @JoinColumn(name = "IDDOSIFICACION", referencedColumnName = "IDDOSIFICACION")
    @ManyToOne
    private Dosificacion dosificacion;

    public Impresionfactura() {
    }

    public Impresionfactura(Long idimprecionfactura) {
        this.idimprecionfactura = idimprecionfactura;
    }

    public Impresionfactura(Long idimprecionfactura, Date fechaimpresion) {
        this.idimprecionfactura = idimprecionfactura;
        this.fechaimpresion = fechaimpresion;
    }

    public Long getIdimprecionfactura() {
        return idimprecionfactura;
    }

    public void setIdimprecionfactura(Long idimprecionfactura) {
        this.idimprecionfactura = idimprecionfactura;
    }

    public Date getFechaimpresion() {
        return fechaimpresion;
    }

    public void setFechaimpresion(Date fechaimpresion) {
        this.fechaimpresion = fechaimpresion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Movimiento getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(Movimiento idmovimiento) {
        this.movimiento = idmovimiento;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario idusuario) {
        this.usuario = idusuario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idimprecionfactura != null ? idimprecionfactura.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Impresionfactura)) {
            return false;
        }
        Impresionfactura other = (Impresionfactura) object;
        if ((this.idimprecionfactura == null && other.idimprecionfactura != null) || (this.idimprecionfactura != null && !this.idimprecionfactura.equals(other.idimprecionfactura))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.encens.khipus.model.Impresionfactura[ idimprecionfactura=" + idimprecionfactura + " ]";
    }

    public Dosificacion getDosificacion() {
        return dosificacion;
    }

    public void setDosificacion(Dosificacion iddosificacion) {
        this.dosificacion = iddosificacion;
    }

    public Integer getNroFactura() {
        return nroFactura;
    }

    public void setNroFactura(Integer nroFactura) {
        this.nroFactura = nroFactura;
    }
}
