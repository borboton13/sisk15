/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.model;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
@Table(name = "tipopedido")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tipopedido.findAll", query = "SELECT t FROM Tipopedido t"),
    @NamedQuery(name = "Tipopedido.findByIdtipopedido", query = "SELECT t FROM Tipopedido t WHERE t.idtipopedido = :idtipopedido"),
    @NamedQuery(name = "Tipopedido.findByNombre", query = "SELECT t FROM Tipopedido t WHERE t.nombre = :nombre")})
public class Tipopedido implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "IDTIPOPEDIDO")
    private Long idtipopedido;
    @Size(max = 20)
    @Column(name = "NOMBRE")
    private String nombre;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idtipopedido")
    private Collection<Pedidos> pedidosCollection;

    public Tipopedido() {
    }

    public Tipopedido(Long idtipopedido) {
        this.idtipopedido = idtipopedido;
    }

    public Long getIdtipopedido() {
        return idtipopedido;
    }

    public void setIdtipopedido(Long idtipopedido) {
        this.idtipopedido = idtipopedido;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @XmlTransient
    public Collection<Pedidos> getPedidosCollection() {
        return pedidosCollection;
    }

    public void setPedidosCollection(Collection<Pedidos> pedidosCollection) {
        this.pedidosCollection = pedidosCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idtipopedido != null ? idtipopedido.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tipopedido)) {
            return false;
        }
        Tipopedido other = (Tipopedido) object;
        if ((this.idtipopedido == null && other.idtipopedido != null) || (this.idtipopedido != null && !this.idtipopedido.equals(other.idtipopedido))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nombre;
    }
    
}
