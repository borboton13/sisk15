/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "funcionalidad")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Funcionalidad.findAll", query = "SELECT f FROM Funcionalidad f"),
    @NamedQuery(name = "Funcionalidad.findByIdfuncionalidad", query = "SELECT f FROM Funcionalidad f WHERE f.idfuncionalidad = :idfuncionalidad"),
    @NamedQuery(name = "Funcionalidad.findByCodigo", query = "SELECT f FROM Funcionalidad f WHERE f.codigo = :codigo"),
    @NamedQuery(name = "Funcionalidad.findByPermiso", query = "SELECT f FROM Funcionalidad f WHERE f.permiso = :permiso"),
    @NamedQuery(name = "Funcionalidad.findByNombrerecurso", query = "SELECT f FROM Funcionalidad f WHERE f.nombrerecurso = :nombrerecurso"),
    @NamedQuery(name = "Funcionalidad.findByIdcompania", query = "SELECT f FROM Funcionalidad f WHERE f.idcompania = :idcompania")})
public class Funcionalidad implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "IDFUNCIONALIDAD")
    private Long idfuncionalidad;
    @Size(max = 40)
    @Column(name = "CODIGO")
    private String codigo;
    @Lob
    @Size(max = 65535)
    @Column(name = "DESCRIPCION")
    private String descripcion;
    @Column(name = "PERMISO")
    private Integer permiso;
    @Size(max = 100)
    @Column(name = "NOMBRERECURSO")
    private String nombrerecurso;
    @Column(name = "IDCOMPANIA")
    private BigInteger idcompania;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "funcionalidad")
    private Collection<Derechoacceso> derechoaccesoCollection;
    @JoinColumn(name = "IDMODULO", referencedColumnName = "IDMODULO")
    @ManyToOne
    private Modulo idmodulo;

    public Funcionalidad() {
    }

    public Funcionalidad(Long idfuncionalidad) {
        this.idfuncionalidad = idfuncionalidad;
    }

    public Long getIdfuncionalidad() {
        return idfuncionalidad;
    }

    public void setIdfuncionalidad(Long idfuncionalidad) {
        this.idfuncionalidad = idfuncionalidad;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getPermiso() {
        return permiso;
    }

    public void setPermiso(Integer permiso) {
        this.permiso = permiso;
    }

    public String getNombrerecurso() {
        return nombrerecurso;
    }

    public void setNombrerecurso(String nombrerecurso) {
        this.nombrerecurso = nombrerecurso;
    }

    public BigInteger getIdcompania() {
        return idcompania;
    }

    public void setIdcompania(BigInteger idcompania) {
        this.idcompania = idcompania;
    }

    @XmlTransient
    public Collection<Derechoacceso> getDerechoaccesoCollection() {
        return derechoaccesoCollection;
    }

    public void setDerechoaccesoCollection(Collection<Derechoacceso> derechoaccesoCollection) {
        this.derechoaccesoCollection = derechoaccesoCollection;
    }

    public Modulo getIdmodulo() {
        return idmodulo;
    }

    public void setIdmodulo(Modulo idmodulo) {
        this.idmodulo = idmodulo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idfuncionalidad != null ? idfuncionalidad.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Funcionalidad)) {
            return false;
        }
        Funcionalidad other = (Funcionalidad) object;
        if ((this.idfuncionalidad == null && other.idfuncionalidad != null) || (this.idfuncionalidad != null && !this.idfuncionalidad.equals(other.idfuncionalidad))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.encens.khipus.model.Funcionalidad[ idfuncionalidad=" + idfuncionalidad + " ]";
    }
    
}
