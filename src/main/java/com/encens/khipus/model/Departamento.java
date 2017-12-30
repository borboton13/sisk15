/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.model;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "departamento")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Departamento.findAll", query = "SELECT d FROM Departamento d"),
    @NamedQuery(name = "Departamento.findByIddepartamento", query = "SELECT d FROM Departamento d WHERE d.iddepartamento = :iddepartamento"),
    @NamedQuery(name = "Departamento.findByNombre", query = "SELECT d FROM Departamento d WHERE d.nombre = :nombre")})
public class Departamento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @NotNull
    @Column(name = "IDDEPARTAMENTO")
    private Long iddepartamento;
    @Size(max = 20)
    @Column(name = "NOMBRE")
    private String nombre;
    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "departamento")
    private Collection<Territoriotrabajo> territoriotrabajos;
    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "departamento")
    private Collection<Persona> clientes;

    public Departamento() {
    }

    public Departamento(Long iddepartamento) {
        this.iddepartamento = iddepartamento;
    }

    public Long getIddepartamento() {
        return iddepartamento;
    }

    public void setIddepartamento(Long iddepartamento) {
        this.iddepartamento = iddepartamento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (iddepartamento != null ? iddepartamento.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Departamento)) {
            return false;
        }
        Departamento other = (Departamento) object;
        if ((this.iddepartamento == null && other.iddepartamento != null) || (this.iddepartamento != null && !this.iddepartamento.equals(other.iddepartamento))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nombre;
    }

    public Collection<Territoriotrabajo> getTerritoriotrabajos() {
        return territoriotrabajos;
    }

    public void setTerritoriotrabajos(Collection<Territoriotrabajo> territoriotrabajos) {
        this.territoriotrabajos = territoriotrabajos;
    }

    public Collection<Persona> getClientes() {
        return clientes;
    }

    public void setClientes(Collection<Persona> clientes) {
        this.clientes = clientes;
    }
}
