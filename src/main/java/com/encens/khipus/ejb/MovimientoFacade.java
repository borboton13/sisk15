/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.ejb;

import com.encens.khipus.model.Movimiento;
import com.encens.khipus.model.Pedidos;
import com.encens.khipus.model.SfTmpenc;

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
public class MovimientoFacade extends AbstractFacade<Movimiento> {
    @PersistenceContext(unitName = "khipusPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MovimientoFacade() {
        super(Movimiento.class);
    }

    public List<Pedidos> findInvoices(Date startDate, Date endDate) {
        List<Pedidos> result = new ArrayList<Pedidos>();
        List<Movimiento> movimientoList = new ArrayList<Movimiento>();
        try{
            movimientoList = (List<Movimiento>)em.createQuery("select m from Movimiento m " +
                    "where m.fechaFactura between :startDate and :endDate " +
                    " " +
                    "order by m.nrofactura asc")
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .getResultList();

            for (Movimiento movimiento : movimientoList){
                result.add(movimiento.getPedido());
            }

        }catch (NoResultException e){
            return result;
        }
        return result;
    }

    public void anularFactura(Movimiento movimiento){
        if (movimiento != null){
            movimiento.setEstado("A");
            em.merge(movimiento);
            em.flush();
        }
    }

}
