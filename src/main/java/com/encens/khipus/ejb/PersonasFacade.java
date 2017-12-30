/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.ejb;

import antlr.LexerSharedInputState;
import com.encens.khipus.model.Cliente;
import com.encens.khipus.model.Distribuidor;
import com.encens.khipus.model.Persona;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Diego
 */
@Stateless
public class PersonasFacade extends AbstractFacade<Persona> {
    @PersistenceContext(unitName = "khipusPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PersonasFacade() {
        super(Persona.class);
    }

    public List<Persona> findAllClientesPersonaInstitucion() {
        List<Persona> personas = new ArrayList<>();
        try {
            personas = (List<Persona>)em.createQuery("select persona from Persona persona where persona.tipoPersona = 'cliente' or persona.tipoPersona = 'institucion'" +
                    "order by persona.piId desc")
                    .getResultList();

        }catch (NoResultException e){
            return personas;
        }
        return personas;
    }

    public List<Persona> findAlldistribuidores() {
        List<Persona> personas = new ArrayList<>();
        try {
            personas = (List<Persona>)em.createQuery("select persona from Persona persona where persona.tipoPersona = 'distribuidor'")
                    .getResultList();

        }catch (NoResultException e){
            return personas;
        }
        return personas;
    }

    public Persona findById(Persona persona) {
        return (Persona)em.createNamedQuery("Personas.findByPiId").setParameter("piId",persona.getPiId()).getSingleResult();
    }
}
