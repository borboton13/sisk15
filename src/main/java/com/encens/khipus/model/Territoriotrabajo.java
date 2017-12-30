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
@Table(name = "territoriotrabajo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Territoriotrabajo.findAll", query = "SELECT t FROM Territoriotrabajo t"),
    @NamedQuery(name = "Territoriotrabajo.findByIdterritoriotrabajo", query = "SELECT t FROM Territoriotrabajo t WHERE t.idterritoriotrabajo = :idterritoriotrabajo"),
    @NamedQuery(name = "Territoriotrabajo.findByNombre", query = "SELECT t FROM Territoriotrabajo t WHERE t.nombre = :nombre"),
    @NamedQuery(name = "Territoriotrabajo.findByPais", query = "SELECT t FROM Territoriotrabajo t WHERE t.pais = :pais"),
    @NamedQuery(name = "Territoriotrabajo.findByDescripcion", query = "SELECT t FROM Territoriotrabajo t WHERE t.descripcion = :descripcion")})
public class Territoriotrabajo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @NotNull
    @TableGenerator(name = "Territoriotrabajo_Gen"
            ,table="ID_GEN"
            ,pkColumnName = "GEN_NAME"
            ,valueColumnName = "GEN_VAL"
            ,initialValue = 1
            ,allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Territoriotrabajo_Gen")
    @Column(name = "IDTERRITORIOTRABAJO")
    private Long idterritoriotrabajo;
    @Size(max = 100)
    @Column(name = "NOMBRE")
    private String nombre;
    @Size(max = 50)
    @Column(name = "PAIS")
    private String pais;
    @Size(max = 50)
    @Column(name = "DESCRIPCION")
    private String descripcion;
    @OneToMany(mappedBy = "territoriotrabajo")
    private Collection<Persona> clientes;
    @JoinColumn(name = "IDDISTRIBUIDOR", referencedColumnName = "IDPERSONACLIENTE")
    @ManyToOne
    private Persona distribuidor;
    @JoinColumn(name = "IDDEPARTAMENTO", referencedColumnName = "IDDEPARTAMENTO")
    @ManyToOne
    private Departamento departamento;

    public Territoriotrabajo() {
    }

    public Territoriotrabajo(Long idterritoriotrabajo) {
        this.idterritoriotrabajo = idterritoriotrabajo;
    }

    public Long getIdterritoriotrabajo() {
        return idterritoriotrabajo;
    }

    public void setIdterritoriotrabajo(Long idterritoriotrabajo) {
        this.idterritoriotrabajo = idterritoriotrabajo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPais() {
        if(this.pais == null)
            pais = "Bolivia";
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @XmlTransient
    public Collection<Persona> getClientes() {
        return clientes;
    }

    public void setClientes(Collection<Persona> personaCollection) {
        this.clientes = personaCollection;
    }

    public Persona getDistribuidor() {
        return distribuidor;
    }

    public void setDistribuidor(Persona iddistribuidor) {
        this.distribuidor = iddistribuidor;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idterritoriotrabajo != null ? idterritoriotrabajo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Territoriotrabajo)) {
            return false;
        }
        Territoriotrabajo other = (Territoriotrabajo) object;
        if ((this.idterritoriotrabajo == null && other.idterritoriotrabajo != null) || (this.idterritoriotrabajo != null && !this.idterritoriotrabajo.equals(other.idterritoriotrabajo))) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return nombre;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }
}
