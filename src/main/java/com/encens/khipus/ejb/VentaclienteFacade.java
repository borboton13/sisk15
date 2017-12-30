/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.ejb;

import com.encens.khipus.model.Persona;
import com.encens.khipus.model.Ventaarticulo;
import com.encens.khipus.model.Ventacliente;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.Collection;

/**
 *
 * @author Diego
 */
@Stateless
public class VentaclienteFacade extends AbstractFacade<Ventacliente> {
    @PersistenceContext(unitName = "khipusPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VentaclienteFacade() {
        super(Ventacliente.class);
    }

    public Ventacliente findByInvArticuloPersona(Ventaarticulo ventaarticulo, Persona personaElegido) {
        Ventacliente ventacliente = null;
        try{
            ventacliente = (Ventacliente)em.createNamedQuery("Ventacliente.findByVentaclientePersona")
            .setParameter("ventaarticulo",ventaarticulo)
            .setParameter("persona",personaElegido)
            .getSingleResult();

        }catch (NoResultException e){
            return null;
        }
        return ventacliente;
    }

    public void mergeItems(Collection<Ventacliente> precios){
        for ( Ventacliente precio:precios){
            em.merge(precio);
        }

    }

}
