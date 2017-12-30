/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.ejb;

import com.encens.khipus.model.Territoriotrabajo;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Diego
 */
@Stateless
public class TerritoriotrabajoFacade extends AbstractFacade<Territoriotrabajo> {
    @PersistenceContext(unitName = "khipusPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TerritoriotrabajoFacade() {
        super(Territoriotrabajo.class);
    }

    public Territoriotrabajo findById(Long idterritoriotrabajo) {
        return (Territoriotrabajo)em.createNamedQuery("Territoriotrabajo.findByIdterritoriotrabajo")
                .setParameter("idterritoriotrabajo",idterritoriotrabajo)
                .getSingleResult();
    }
}
