/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "derechoacceso")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Derechoacceso.findAll", query = "SELECT d FROM Derechoacceso d"),
    @NamedQuery(name = "Derechoacceso.findByIdfuncionalidad", query = "SELECT d FROM Derechoacceso d WHERE d.derechoaccesoPK.idfuncionalidad = :idfuncionalidad"),
    @NamedQuery(name = "Derechoacceso.findByIdrol", query = "SELECT d FROM Derechoacceso d WHERE d.derechoaccesoPK.idrol = :idrol"),
    @NamedQuery(name = "Derechoacceso.findByPermiso", query = "SELECT d FROM Derechoacceso d WHERE d.permiso = :permiso")})
public class Derechoacceso implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected DerechoaccesoPK derechoaccesoPK;
    @Column(name = "PERMISO")
    private Integer permiso;
    @JoinColumn(name = "IDFUNCIONALIDAD", referencedColumnName = "IDFUNCIONALIDAD", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Funcionalidad funcionalidad;
    @JoinColumns({
        @JoinColumn(name = "IDCOMPANIA", referencedColumnName = "IDCOMPANIA"),
        @JoinColumn(name = "IDMODULO", referencedColumnName = "IDMODULO")})
    @ManyToOne
    private Modulocompania modulocompania;
    @JoinColumn(name = "IDROL", referencedColumnName = "IDROL", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Rol rol;

    public Derechoacceso() {
    }

    public Derechoacceso(DerechoaccesoPK derechoaccesoPK) {
        this.derechoaccesoPK = derechoaccesoPK;
    }

    public Derechoacceso(long idfuncionalidad, long idrol) {
        this.derechoaccesoPK = new DerechoaccesoPK(idfuncionalidad, idrol);
    }

    public DerechoaccesoPK getDerechoaccesoPK() {
        return derechoaccesoPK;
    }

    public void setDerechoaccesoPK(DerechoaccesoPK derechoaccesoPK) {
        this.derechoaccesoPK = derechoaccesoPK;
    }

    public Integer getPermiso() {
        return permiso;
    }

    public void setPermiso(Integer permiso) {
        this.permiso = permiso;
    }

    public Funcionalidad getFuncionalidad() {
        return funcionalidad;
    }

    public void setFuncionalidad(Funcionalidad funcionalidad) {
        this.funcionalidad = funcionalidad;
    }

    public Modulocompania getModulocompania() {
        return modulocompania;
    }

    public void setModulocompania(Modulocompania modulocompania) {
        this.modulocompania = modulocompania;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (derechoaccesoPK != null ? derechoaccesoPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Derechoacceso)) {
            return false;
        }
        Derechoacceso other = (Derechoacceso) object;
        if ((this.derechoaccesoPK == null && other.derechoaccesoPK != null) || (this.derechoaccesoPK != null && !this.derechoaccesoPK.equals(other.derechoaccesoPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.encens.khipus.model.Derechoacceso[ derechoaccesoPK=" + derechoaccesoPK + " ]";
    }
    
}
