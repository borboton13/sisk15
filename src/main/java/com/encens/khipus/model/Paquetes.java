/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "paquetes")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Paquetes.findAll", query = "SELECT p FROM Paquetes p"),
    @NamedQuery(name = "Paquetes.findByIdpaquetes", query = "SELECT p FROM Paquetes p WHERE p.idpaquetes = :idpaquetes"),
    @NamedQuery(name = "Paquetes.findByNombre", query = "SELECT p FROM Paquetes p WHERE p.nombre = :nombre"),
    @NamedQuery(name = "Paquetes.findByFechaInicio", query = "SELECT p FROM Paquetes p WHERE p.fechaInicio = :fechaInicio"),
    @NamedQuery(name = "Paquetes.findByFechaFin", query = "SELECT p FROM Paquetes p WHERE p.fechaFin = :fechaFin"),
    @NamedQuery(name = "Paquetes.findByActivo", query = "SELECT p FROM Paquetes p WHERE p.activo = :activo"),
    @NamedQuery(name = "Paquetes.findByTotal", query = "SELECT p FROM Paquetes p WHERE p.total = :total"),
    @NamedQuery(name = "Paquetes.findByCuentaId", query = "SELECT p FROM Paquetes p WHERE p.cuentaId = :cuentaId")})
public class Paquetes implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "IDPAQUETES")
    private Long idpaquetes;
    @Size(max = 100)
    @Column(name = "NOMBRE")
    private String nombre;
    @Column(name = "FECHA_INICIO")
    @Temporal(TemporalType.DATE)
    private Date fechaInicio;
    @Column(name = "FECHA_FIN")
    @Temporal(TemporalType.DATE)
    private Date fechaFin;
    @Size(max = 2)
    @Column(name = "ACTIVO")
    private String activo;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "TOTAL")
    private Double total;
    @Column(name = "CUENTA_ID")
    private BigInteger cuentaId;

    public Paquetes() {
    }

    public Paquetes(Long idpaquetes) {
        this.idpaquetes = idpaquetes;
    }

    public Long getIdpaquetes() {
        return idpaquetes;
    }

    public void setIdpaquetes(Long idpaquetes) {
        this.idpaquetes = idpaquetes;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public BigInteger getCuentaId() {
        return cuentaId;
    }

    public void setCuentaId(BigInteger cuentaId) {
        this.cuentaId = cuentaId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idpaquetes != null ? idpaquetes.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Paquetes)) {
            return false;
        }
        Paquetes other = (Paquetes) object;
        if ((this.idpaquetes == null && other.idpaquetes != null) || (this.idpaquetes != null && !this.idpaquetes.equals(other.idpaquetes))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.encens.khipus.model.Paquetes[ idpaquetes=" + idpaquetes + " ]";
    }
    
}
