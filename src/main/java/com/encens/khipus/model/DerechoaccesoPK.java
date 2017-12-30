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
public class DerechoaccesoPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "IDFUNCIONALIDAD")
    private long idfuncionalidad;
    @Basic(optional = false)
    @NotNull
    @Column(name = "IDROL")
    private long idrol;

    public DerechoaccesoPK() {
    }

    public DerechoaccesoPK(long idfuncionalidad, long idrol) {
        this.idfuncionalidad = idfuncionalidad;
        this.idrol = idrol;
    }

    public long getIdfuncionalidad() {
        return idfuncionalidad;
    }

    public void setIdfuncionalidad(long idfuncionalidad) {
        this.idfuncionalidad = idfuncionalidad;
    }

    public long getIdrol() {
        return idrol;
    }

    public void setIdrol(long idrol) {
        this.idrol = idrol;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idfuncionalidad;
        hash += (int) idrol;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DerechoaccesoPK)) {
            return false;
        }
        DerechoaccesoPK other = (DerechoaccesoPK) object;
        if (this.idfuncionalidad != other.idfuncionalidad) {
            return false;
        }
        if (this.idrol != other.idrol) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.encens.khipus.model.DerechoaccesoPK[ idfuncionalidad=" + idfuncionalidad + ", idrol=" + idrol + " ]";
    }
    
}
