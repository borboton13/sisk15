/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.model;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Diego
 */
@Entity
@XmlRootElement
@DiscriminatorValue("distribuidor")
@NamedQueries({
    @NamedQuery(name = "Distribuidor.findAll", query = "SELECT d FROM Distribuidor d"),
    @NamedQuery(name = "Distribuidor.findByObservacion", query = "SELECT d FROM Distribuidor d WHERE d.observacion = :observacion")})
public class Distribuidor extends Persona {
    @Size(max = 50)
    @Column(name = "OBSERVACION")
    private String observacion;

    public Distribuidor() {
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    @Override
    public String toString() {
        return super.getNombreCompleto();
    }
    
}
