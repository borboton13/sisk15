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
import javax.persistence.Lob;
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
@Table(name = "modulo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Modulo.findAll", query = "SELECT m FROM Modulo m"),
    @NamedQuery(name = "Modulo.findByIdmodulo", query = "SELECT m FROM Modulo m WHERE m.idmodulo = :idmodulo"),
    @NamedQuery(name = "Modulo.findByNombrerecurso", query = "SELECT m FROM Modulo m WHERE m.nombrerecurso = :nombrerecurso"),
    @NamedQuery(name = "Modulo.findByIdcompania", query = "SELECT m FROM Modulo m WHERE m.idcompania = :idcompania")})
public class Modulo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "IDMODULO")
    private Long idmodulo;
    @Lob
    @Size(max = 65535)
    @Column(name = "DESCRIPCION")
    private String descripcion;
    @Size(max = 150)
    @Column(name = "NOMBRERECURSO")
    private String nombrerecurso;
    @Column(name = "IDCOMPANIA")
    private BigInteger idcompania;
    @OneToMany(mappedBy = "idmodulo")
    private Collection<Funcionalidad> funcionalidadCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "modulo")
    private Collection<Modulocompania> modulocompaniaCollection;

    public Modulo() {
    }

    public Modulo(Long idmodulo) {
        this.idmodulo = idmodulo;
    }

    public Long getIdmodulo() {
        return idmodulo;
    }

    public void setIdmodulo(Long idmodulo) {
        this.idmodulo = idmodulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
    public Collection<Funcionalidad> getFuncionalidadCollection() {
        return funcionalidadCollection;
    }

    public void setFuncionalidadCollection(Collection<Funcionalidad> funcionalidadCollection) {
        this.funcionalidadCollection = funcionalidadCollection;
    }

    @XmlTransient
    public Collection<Modulocompania> getModulocompaniaCollection() {
        return modulocompaniaCollection;
    }

    public void setModulocompaniaCollection(Collection<Modulocompania> modulocompaniaCollection) {
        this.modulocompaniaCollection = modulocompaniaCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idmodulo != null ? idmodulo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Modulo)) {
            return false;
        }
        Modulo other = (Modulo) object;
        if ((this.idmodulo == null && other.idmodulo != null) || (this.idmodulo != null && !this.idmodulo.equals(other.idmodulo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.encens.khipus.model.Modulo[ idmodulo=" + idmodulo + " ]";
    }
    
}
