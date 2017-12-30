/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.model;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "ventacliente")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ventacliente.findAll", query = "SELECT v FROM Ventacliente v"),
    @NamedQuery(name = "Ventacliente.findByIdventacliente", query = "SELECT v FROM Ventacliente v WHERE v.idventacliente = :idventacliente"),
    @NamedQuery(name = "Ventacliente.findByPrecioespecial", query = "SELECT v FROM Ventacliente v WHERE v.precioespecial = :precioespecial")})
public class Ventacliente implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @NotNull
    @TableGenerator(name = "Ventacliente_Gen"
            ,table="ID_GEN"
            ,pkColumnName = "GEN_NAME"
            ,valueColumnName = "GEN_VAL"
            ,initialValue = 1
            ,allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Ventacliente_Gen")
    private Long idventacliente;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "PRECIOESPECIAL")
    private Double precioespecial;

    @JoinColumn(name = "IDCLIENTE", referencedColumnName = "IDPERSONACLIENTE")
    //@ManyToOne(optional = false, cascade = CascadeType.ALL)
    @ManyToOne(optional = false)
    private Persona persona;

    @JoinColumns({
            @JoinColumn(name = "NO_CIA", referencedColumnName = "NO_CIA"),
            @JoinColumn(name = "COD_ART", referencedColumnName = "COD_ART")})
    @ManyToOne(optional = false)
    private InvArticulos invArticulos;

    public Ventacliente() {
    }

    public Ventacliente(Long idventacliente) {
        this.idventacliente = idventacliente;
    }

    public Long getIdventacliente() {
        return idventacliente;
    }

    public void setIdventacliente(Long idventacliente) {
        this.idventacliente = idventacliente;
    }

    public Double getPrecioespecial() {
        return precioespecial;
    }

    public void setPrecioespecial(Double precioespecial) {
        this.precioespecial = precioespecial;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idventacliente != null ? idventacliente.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ventacliente)) {
            return false;
        }
        Ventacliente other = (Ventacliente) object;
        if ((this.idventacliente == null && other.idventacliente != null) || (this.idventacliente != null && !this.idventacliente.equals(other.idventacliente))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.encens.khipus.model.Ventacliente[ idventacliente=" + idventacliente + " ]";
    }

    public InvArticulos getInvArticulos() {
        return invArticulos;
    }

    public void setInvArticulos(InvArticulos invArticulos) {
        this.invArticulos = invArticulos;
    }
}
