/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.model;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "modulocompania")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Modulocompania.findAll", query = "SELECT m FROM Modulocompania m"),
    @NamedQuery(name = "Modulocompania.findByIdcompania", query = "SELECT m FROM Modulocompania m WHERE m.modulocompaniaPK.idcompania = :idcompania"),
    @NamedQuery(name = "Modulocompania.findByIdmodulo", query = "SELECT m FROM Modulocompania m WHERE m.modulocompaniaPK.idmodulo = :idmodulo"),
    @NamedQuery(name = "Modulocompania.findByActivo", query = "SELECT m FROM Modulocompania m WHERE m.activo = :activo")})
public class Modulocompania implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ModulocompaniaPK modulocompaniaPK;
    @Column(name = "ACTIVO")
    private Integer activo;
    @OneToMany(mappedBy = "modulocompania")
    private Collection<Derechoacceso> derechoaccesoCollection;
    @JoinColumn(name = "IDMODULO", referencedColumnName = "IDMODULO", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Modulo modulo;

    public Modulocompania() {
    }

    public Modulocompania(ModulocompaniaPK modulocompaniaPK) {
        this.modulocompaniaPK = modulocompaniaPK;
    }

    public Modulocompania(long idcompania, long idmodulo) {
        this.modulocompaniaPK = new ModulocompaniaPK(idcompania, idmodulo);
    }

    public ModulocompaniaPK getModulocompaniaPK() {
        return modulocompaniaPK;
    }

    public void setModulocompaniaPK(ModulocompaniaPK modulocompaniaPK) {
        this.modulocompaniaPK = modulocompaniaPK;
    }

    public Integer getActivo() {
        return activo;
    }

    public void setActivo(Integer activo) {
        this.activo = activo;
    }

    @XmlTransient
    public Collection<Derechoacceso> getDerechoaccesoCollection() {
        return derechoaccesoCollection;
    }

    public void setDerechoaccesoCollection(Collection<Derechoacceso> derechoaccesoCollection) {
        this.derechoaccesoCollection = derechoaccesoCollection;
    }

    public Modulo getModulo() {
        return modulo;
    }

    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (modulocompaniaPK != null ? modulocompaniaPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Modulocompania)) {
            return false;
        }
        Modulocompania other = (Modulocompania) object;
        if ((this.modulocompaniaPK == null && other.modulocompaniaPK != null) || (this.modulocompaniaPK != null && !this.modulocompaniaPK.equals(other.modulocompaniaPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.encens.khipus.model.Modulocompania[ modulocompaniaPK=" + modulocompaniaPK + " ]";
    }
    
}
