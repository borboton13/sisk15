/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.model;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
@Table(name = "compania")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Compania.findAll", query = "SELECT c FROM Compania c"),
    @NamedQuery(name = "Compania.findByIdcompania", query = "SELECT c FROM Compania c WHERE c.idcompania = :idcompania"),
    @NamedQuery(name = "Compania.findByCodigo", query = "SELECT c FROM Compania c WHERE c.codigo = :codigo")})
public class Compania implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "IDCOMPANIA")
    private Long idcompania;
    @Size(max = 255)
    @Column(name = "CODIGO")
    private String codigo;
    @OneToMany(mappedBy = "idcompania")
    private Collection<Usuario> usuarioCollection;

    public Compania() {
    }

    public Compania(Long idcompania) {
        this.idcompania = idcompania;
    }

    public Long getIdcompania() {
        return idcompania;
    }

    public void setIdcompania(Long idcompania) {
        this.idcompania = idcompania;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    @XmlTransient
    public Collection<Usuario> getUsuarioCollection() {
        return usuarioCollection;
    }

    public void setUsuarioCollection(Collection<Usuario> usuarioCollection) {
        this.usuarioCollection = usuarioCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idcompania != null ? idcompania.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Compania)) {
            return false;
        }
        Compania other = (Compania) object;
        if ((this.idcompania == null && other.idcompania != null) || (this.idcompania != null && !this.idcompania.equals(other.idcompania))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.encens.khipus.model.Compania[ idcompania=" + idcompania + " ]";
    }
    
}
