/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.ejb;

import com.encens.khipus.model.Distribuidor;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Diego
 */
@Stateless
public class DistribuidorFacade extends AbstractFacade<Distribuidor> {
    @PersistenceContext(unitName = "khipusPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DistribuidorFacade() {
        super(Distribuidor.class);
    }

    public Distribuidor findById(Long piId) {
        return (Distribuidor)em.createQuery("select dis from Persona dis where dis.piId =:piId")
                .setParameter("piId",piId)
                .getSingleResult();
    }
}
