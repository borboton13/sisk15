/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.ejb;

import com.encens.khipus.model.InvArticulos;
import com.encens.khipus.model.Ventaarticulo;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Diego
 */
@Stateless
public class VentaarticuloFacade extends AbstractFacade<Ventaarticulo> {
    @PersistenceContext(unitName = "khipusPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VentaarticuloFacade() {
        super(Ventaarticulo.class);
    }

    public Ventaarticulo findByInvArticulo(InvArticulos articuloElegido) {
        Ventaarticulo ventaarticulo = null;
        try{
            ventaarticulo = (Ventaarticulo)em.createNamedQuery("Ventaarticulo.findByInvArticulo")
                    .setParameter("invArticulos",articuloElegido)
                    .getSingleResult();
        }catch (NoResultException e){
            return null;
        }
        return ventaarticulo;
    }

    public Ventaarticulo findById(Long idventaarticulo) {
        return (Ventaarticulo)em.createNamedQuery("Ventaarticulo.findByIdventaarticulo")
                .setParameter("idventaarticulo",idventaarticulo)
                .getSingleResult();
    }
}
