/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Diego
 */
@Embeddable
public class ModulocompaniaPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "IDCOMPANIA")
    private long idcompania;
    @Basic(optional = false)
    @NotNull
    @Column(name = "IDMODULO")
    private long idmodulo;

    public ModulocompaniaPK() {
    }

    public ModulocompaniaPK(long idcompania, long idmodulo) {
        this.idcompania = idcompania;
        this.idmodulo = idmodulo;
    }

    public long getIdcompania() {
        return idcompania;
    }

    public void setIdcompania(long idcompania) {
        this.idcompania = idcompania;
    }

    public long getIdmodulo() {
        return idmodulo;
    }

    public void setIdmodulo(long idmodulo) {
        this.idmodulo = idmodulo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idcompania;
        hash += (int) idmodulo;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ModulocompaniaPK)) {
            return false;
        }
        ModulocompaniaPK other = (ModulocompaniaPK) object;
        if (this.idcompania != other.idcompania) {
            return false;
        }
        if (this.idmodulo != other.idmodulo) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.encens.khipus.model.ModulocompaniaPK[ idcompania=" + idcompania + ", idmodulo=" + idmodulo + " ]";
    }
    
}
