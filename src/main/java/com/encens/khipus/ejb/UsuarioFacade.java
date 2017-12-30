/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.ejb;

import com.encens.khipus.model.Usuario;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Diego
 */
@Stateless
public class UsuarioFacade extends AbstractFacade<Usuario> {
    @PersistenceContext(unitName = "khipusPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsuarioFacade() {
        super(Usuario.class);
    }

    public Usuario findByUserName(String user){
       Usuario usuario;
               try {
                   usuario = (Usuario) em.createQuery("SELECT u FROM Usuario u WHERE u.usuario = :usuario")
                           .setParameter("usuario",user)
                           .getSingleResult();
               }catch (NoResultException e)
               {
                   return null;
               }
        return usuario;
    }


}
