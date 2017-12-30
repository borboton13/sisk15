/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.ejb;

import com.encens.khipus.model.Dosificacion;
import com.encens.khipus.model.Sucursal;
import com.encens.khipus.util.BigDecimalUtil;
import javassist.bytecode.stackmap.BasicBlock;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.Date;

/**
 *
 * @author Diego
 */
@Stateless
public class DosificacionFacade extends AbstractFacade<Dosificacion> {
    @PersistenceContext(unitName = "khipusPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DosificacionFacade() {
        super(Dosificacion.class);
    }

    public Dosificacion findByPeriodo(Date date) {
        Dosificacion dosificacion;
        try{
            dosificacion =(Dosificacion)em.createNamedQuery("Dosificacion.findByPeriodo")
                    .setParameter("fechaFactura",date)
                    .getSingleResult();
        }catch (NoResultException e){
            return null;
        }
        return dosificacion;
    }
    
    public Dosificacion findByOffice(Sucursal sucursal){
        Dosificacion dosificacion;
        try{
            dosificacion =(Dosificacion)em.createNamedQuery("Dosificacion.findByOffice")
                    .setParameter("sucursal",sucursal)
                    .getSingleResult();
        }catch (NoResultException e){
            return null;
        }
        return dosificacion;
    }

    public Dosificacion findByAuthorization(String noAuthorization){
        Dosificacion dosificacion;
        try{
            dosificacion =(Dosificacion)em.createNamedQuery("Dosificacion.findByNroautorizacion")
                    .setParameter("nroautorizacion", new BigInteger(noAuthorization))
                    .getSingleResult();
        }catch (NoResultException e){
            return null;
        }
        return dosificacion;
    }

    public String getSiguienteNumeroFactura()
    {
        String numero = "";
        try{
            numero = (String)em.createNativeQuery("SELECT getNextSeq('FACTURA')").getSingleResult();
        }catch (NoResultException e){
            return numero;
        }
        return numero;
    }
    
    /*public void updateNextNumber(Sucursal sucursal){
        
        System.out.println("====> update idsucursal: " + sucursal.getIdsucursal() + " - "+
        em.createNativeQuery("UPDATE dosificacion SET NUMEROACTUAL = NUMEROACTUAL + 1 " + 
                             "WHERE  IDSUCURSAL = " + sucursal.getIdsucursal()).executeUpdate()
        );
        
    }*/
            
}
