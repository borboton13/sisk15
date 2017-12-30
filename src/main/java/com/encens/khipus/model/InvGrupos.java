/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "inv_grupos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "InvGrupos.findAll", query = "SELECT i FROM InvGrupos i"),
    @NamedQuery(name = "InvGrupos.findByNoCia", query = "SELECT i FROM InvGrupos i WHERE i.invGruposPK.noCia = :noCia"),
    @NamedQuery(name = "InvGrupos.findByCodGru", query = "SELECT i FROM InvGrupos i WHERE i.invGruposPK.codGru = :codGru"),
    @NamedQuery(name = "InvGrupos.findByCuentaInv", query = "SELECT i FROM InvGrupos i WHERE i.cuentaInv = :cuentaInv"),
    @NamedQuery(name = "InvGrupos.findByDescri", query = "SELECT i FROM InvGrupos i WHERE i.descri = :descri"),
    @NamedQuery(name = "InvGrupos.findByVersion", query = "SELECT i FROM InvGrupos i WHERE i.version = :version"),
    @NamedQuery(name = "InvGrupos.findByTipo", query = "SELECT i FROM InvGrupos i WHERE i.tipo = :tipo")})
public class InvGrupos implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "invGrupos")
    private Collection<InvArticulos> invArticulosCollection;

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected InvGruposPK invGruposPK;

    @Size(max = 31)
    @Column(name = "CUENTA_INV")
    private String cuentaInv;

    @Size(max = 100)
    @Column(name = "DESCRI")
    private String descri;

    @Column(name = "VERSION")
    private BigInteger version;

    @Size(max = 50)
    @Column(name = "TIPO")
    private String tipo;
    /*@OneToMany(cascade = CascadeType.ALL, mappedBy = "invGrupos")
    private Collection<InvArticulos> invArticulosCollection;*/

    @Size(max = 31)
    @Column(name = "CUENTA_INGRESO")
    private String cuentaIngreso;

    public InvGrupos() {
    }

    public InvGrupos(InvGruposPK invGruposPK) {
        this.invGruposPK = invGruposPK;
    }

    public InvGrupos(String noCia, String codGru) {
        this.invGruposPK = new InvGruposPK(noCia, codGru);
    }

    public InvGruposPK getInvGruposPK() {
        return invGruposPK;
    }

    public void setInvGruposPK(InvGruposPK invGruposPK) {
        this.invGruposPK = invGruposPK;
    }

    public String getCuentaInv() {
        return cuentaInv;
    }

    public void setCuentaInv(String cuentaInv) {
        this.cuentaInv = cuentaInv;
    }

    public String getDescri() {
        return descri;
    }

    public void setDescri(String descri) {
        this.descri = descri;
    }

    public BigInteger getVersion() {
        return version;
    }

    public void setVersion(BigInteger version) {
        this.version = version;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

   /* @XmlTransient
    public Collection<InvArticulos> getInvArticulosCollection() {
        return invArticulosCollection;
    }

    public void setInvArticulosCollection(Collection<InvArticulos> invArticulosCollection) {
        this.invArticulosCollection = invArticulosCollection;
    }
*/
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (invGruposPK != null ? invGruposPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof InvGrupos)) {
            return false;
        }
        InvGrupos other = (InvGrupos) object;
        if ((this.invGruposPK == null && other.invGruposPK != null) || (this.invGruposPK != null && !this.invGruposPK.equals(other.invGruposPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.encens.khipus.model.InvGrupos[ invGruposPK=" + invGruposPK + " ]";
    }

    @XmlTransient
    public Collection<InvArticulos> getInvArticulosCollection() {
        return invArticulosCollection;
    }

    public void setInvArticulosCollection(Collection<InvArticulos> invArticulosCollection) {
        this.invArticulosCollection = invArticulosCollection;
    }

    public String getCuentaIngreso() {
        return cuentaIngreso;
    }

    public void setCuentaIngreso(String cuentaIngreso) {
        this.cuentaIngreso = cuentaIngreso;
    }
}
