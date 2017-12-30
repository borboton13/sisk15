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
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
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
@Table(name = "rol")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Rol.findAll", query = "SELECT r FROM Rol r"),
    @NamedQuery(name = "Rol.findByIdrol", query = "SELECT r FROM Rol r WHERE r.idrol = :idrol"),
    @NamedQuery(name = "Rol.findByNombre", query = "SELECT r FROM Rol r WHERE r.nombre = :nombre"),
    @NamedQuery(name = "Rol.findByVersion", query = "SELECT r FROM Rol r WHERE r.version = :version"),
    @NamedQuery(name = "Rol.findByIdcompania", query = "SELECT r FROM Rol r WHERE r.idcompania = :idcompania")})
public class Rol implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "IDROL")
    private Long idrol;
    @Lob
    @Size(max = 65535)
    @Column(name = "DESCRIPCION")
    private String descripcion;
    @Size(max = 150)
    @Column(name = "NOMBRE")
    private String nombre;
    @Column(name = "VERSION")
    private BigInteger version;
    @Column(name = "IDCOMPANIA")
    private BigInteger idcompania;
    @JoinTable(name = "usuariorol", joinColumns = {
        @JoinColumn(name = "IDROL", referencedColumnName = "IDROL")}, inverseJoinColumns = {
        @JoinColumn(name = "IDUSUARIO", referencedColumnName = "IDUSUARIO")})
    @ManyToMany
    private Collection<Usuario> usuarioCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rol")
    private Collection<Derechoacceso> derechoaccesoCollection;

    public Rol() {
    }

    public Rol(Long idrol) {
        this.idrol = idrol;
    }

    public Long getIdrol() {
        return idrol;
    }

    public void setIdrol(Long idrol) {
        this.idrol = idrol;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigInteger getVersion() {
        return version;
    }

    public void setVersion(BigInteger version) {
        this.version = version;
    }

    public BigInteger getIdcompania() {
        return idcompania;
    }

    public void setIdcompania(BigInteger idcompania) {
        this.idcompania = idcompania;
    }

    @XmlTransient
    public Collection<Usuario> getUsuarioCollection() {
        return usuarioCollection;
    }

    public void setUsuarioCollection(Collection<Usuario> usuarioCollection) {
        this.usuarioCollection = usuarioCollection;
    }

    @XmlTransient
    public Collection<Derechoacceso> getDerechoaccesoCollection() {
        return derechoaccesoCollection;
    }

    public void setDerechoaccesoCollection(Collection<Derechoacceso> derechoaccesoCollection) {
        this.derechoaccesoCollection = derechoaccesoCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idrol != null ? idrol.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Rol)) {
            return false;
        }
        Rol other = (Rol) object;
        if ((this.idrol == null && other.idrol != null) || (this.idrol != null && !this.idrol.equals(other.idrol))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.encens.khipus.model.Rol[ idrol=" + idrol + " ]";
    }
    
}
