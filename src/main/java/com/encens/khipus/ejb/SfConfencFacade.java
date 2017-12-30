/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.ejb;

import com.encens.khipus.model.SfConfenc;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Diego
 */
@Stateless
public class SfConfencFacade extends AbstractFacade<SfConfenc> {
    @PersistenceContext(unitName = "khipusPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SfConfencFacade() {
        super(SfConfenc.class);
    }

    public SfConfenc getOperacion(String pedidosinfactura) {
        SfConfenc operacion;
        try {
            operacion = (SfConfenc) em.createQuery("select op from SfConfenc op where op.operacion =:pedidosinfactura")
                    .setParameter("pedidosinfactura", pedidosinfactura).getSingleResult();
        }catch (NoResultException e){
            return null;
        }
        return operacion;
    }

    public String getSiguienteNumeroDocumento(String tipoDoc) {

            String numero = "0";
            try{
                numero = (String)em.createNativeQuery("SELECT getNextSeq('"+tipoDoc+"')")
                        .setParameter("tipoDoc",tipoDoc)
                        .getSingleResult();
            }catch (NoResultException e){
                return "0";
            }
            return numero;

    }

    public void crearSecuencia(String tipoDoc) {
        em.createNativeQuery("INSERT  INTO _sequence(seq_name,seq_val) VALUES ('"+tipoDoc+"',1)")
                .executeUpdate();
    }

    public boolean existeTipoDoc(String tipoDoc) {
        return em.createNativeQuery("SELECT * FROM _sequence se WHERE se.seq_name = '"+tipoDoc+"'").getResultList().size() > 0;
    }

    public SfConfenc findByID(long idSfConfenc) {
        return (SfConfenc)em.createQuery("select sf from SfConfenc sf where sf.idSfConfenc =:id")
                .setParameter("id",idSfConfenc)
                .getSingleResult();
    }
}
