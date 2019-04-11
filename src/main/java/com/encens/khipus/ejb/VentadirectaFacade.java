/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.ejb;

import com.encens.khipus.model.Usuario;
import com.encens.khipus.model.Ventadirecta;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Diego
 */
@Stateless
public class VentadirectaFacade extends AbstractFacade<Ventadirecta> {
    @PersistenceContext(unitName = "khipusPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VentadirectaFacade() {
        super(Ventadirecta.class);
    }

    public Integer getSiguienteNumeroVenta()
    {
        String numero = "";
        try{
            numero = (String)em.createNativeQuery("SELECT getNextSeq('VENTADIRECTA')").getSingleResult();
        }catch (NoResultException e){
            return 0;
        }
        return Integer.parseInt(numero);
    }
    
    public List<Ventadirecta> findItemsDesc(Usuario usuario) {
        List<Ventadirecta> result = new ArrayList<>();
        Date currentDate = new Date();
        try{
            em.flush();
            result = (List<Ventadirecta>)em.createQuery("select v from Ventadirecta v " +
                                                        "where v.usuario =:usuario " +
                                                        " and v.fechaPedido =:currentDate " +
                                                        "order by v.idventadirecta desc")
                    .setParameter("usuario", usuario)
                    .setParameter("currentDate", currentDate)
                    .getResultList();
        }catch (NoResultException e){
            return result;
        }
        return result;
    }

    public void actualizarVentadirecta(Ventadirecta ventadirecta){

        em.merge(ventadirecta);

    }

    public void crearMovimiento(Ventadirecta ventadirecta){

        em.merge(ventadirecta);

    }

}
