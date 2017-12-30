/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.ejb;

import com.encens.khipus.model.*;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Diego
 */
@Stateless
public class InvArticulosFacade extends AbstractFacade<InvArticulos> {
    @PersistenceContext(unitName = "khipusPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public InvArticulosFacade() {
        super(InvArticulos.class);
    }

    public InvArticulos findByName(String descri)
    {
        InvArticulos invArticulo;
        try {
            invArticulo = (InvArticulos)em.createNamedQuery("InvArticulos.findByDescri")
                    .setParameter("descri", descri)
                    .getSingleResult();
        }catch (NoResultException e)
        {
            return null;
        }
        return invArticulo;
    }

     public InvArticulos findByCodArt(String codArt)
    {
        InvArticulos invArticulo;
        try {
            invArticulo = (InvArticulos)em.createNamedQuery("InvArticulos.findByCodArt")
                    .setParameter("codArt", codArt)
                    .getSingleResult();
        }catch (NoResultException e)
        {
            return null;
        }
        return invArticulo;
    }
    
    public List<InvArticulos> findAllInvArticulos() {

        List<InvArticulos> articulos = new ArrayList<>();
        try{
            articulos = (List<InvArticulos>)em.createQuery("select articulo from InvArticulos articulo inner join articulo.invGrupos grupo where grupo.tipo =:venta")
                        .setParameter("venta","VENTA")
                    .getResultList();
        }catch (NoResultException e)
        {
            return articulos;
        }
        return articulos;
    }

    public void inputProducts(Ventadirecta ventadirecta) {

        for ( ArticulosPedido articleOrder : ventadirecta.getArticulosPedidos()){

            //ProductInventory productInventory = findProductInventoryByCode(articleOrder.getInvArticulos().getProductItemCode());
            ProductInventory productInventory = null;
            if (articleOrder.getInvArticulos().getProductItemCode().equals("148")) //EDAM
                productInventory = findProductInventoryByCode("151"); //PRENSADO
            else
                productInventory = findProductInventoryByCode(articleOrder.getInvArticulos().getProductItemCode());

            if (productInventory != null){
                Double currentAmount = productInventory.getQuantity().doubleValue() + articleOrder.getTotal();
                productInventory.setQuantity(BigDecimal.valueOf(currentAmount));

                ProductInventoryRecord productInventoryRecord = new ProductInventoryRecord();
                productInventoryRecord.setId(getSecuenceNextValue("inv_product_register").longValue());
                productInventoryRecord.setProductItemCode(articleOrder.getInvArticulos().getProductItemCode());
                productInventoryRecord.setQuantity(BigDecimal.valueOf(articleOrder.getTotal()));
                productInventoryRecord.setType(ProductInventoryRecordType.PRODUCTION);
                productInventoryRecord.setMovementType(ProductInventoryRecordType.PRODUCTION.getType());
                productInventoryRecord.setDescription(productInventory.getProductItem().getFullName());
                productInventoryRecord.setProductInventory(productInventory);

                em.persist(productInventoryRecord);
                em.flush();
            }
        }

    }

    public void outputProducts(Ventadirecta ventadirecta) {

        for ( ArticulosPedido articleOrder : ventadirecta.getArticulosPedidos()){

            ProductInventory productInventory = null;
            if (articleOrder.getInvArticulos().getProductItemCode().equals("148")) //EDAM
                productInventory = findProductInventoryByCode("151"); //PRENSADO
            else
                productInventory = findProductInventoryByCode(articleOrder.getInvArticulos().getProductItemCode());

            //ProductInventory productInventory = findProductInventoryByCode(articleOrder.getInvArticulos().getProductItemCode());

            if (productInventory != null) {
                Double stock = productInventory.getQuantity().doubleValue();
                stock = stock - articleOrder.getTotal().doubleValue();
                productInventory.setQuantity(BigDecimal.valueOf(stock));

                ProductInventoryRecord productInventoryRecord = new ProductInventoryRecord();
                productInventoryRecord.setId(getSecuenceNextValue("inv_product_register").longValue());
                productInventoryRecord.setProductItemCode(articleOrder.getInvArticulos().getProductItemCode());
                productInventoryRecord.setQuantity(BigDecimal.valueOf(articleOrder.getTotal()));
                productInventoryRecord.setType(ProductInventoryRecordType.DELIVERY_CASH_SALE);
                productInventoryRecord.setMovementType(ProductInventoryRecordType.DELIVERY_CASH_SALE.getType());
                productInventoryRecord.setDescription(productInventory.getProductItem().getFullName() + ", #" + ventadirecta.getCodigo());
                productInventoryRecord.setProductInventory(productInventory);

                productInventory.getProductInventoryRecords().add(productInventoryRecord);
                ventadirecta.setFlagstock(true);

                em.persist(productInventoryRecord);
                em.flush();
            }
        }
    }

    public void inputProducts(Pedidos pedido) {

        for ( ArticulosPedido articleOrder : pedido.getArticulosPedidos()){
            //ProductInventory productInventory = findProductInventoryByCode(articleOrder.getInvArticulos().getProductItemCode());
            ProductInventory productInventory = null;
            if (articleOrder.getInvArticulos().getProductItemCode().equals("148")) //EDAM
                productInventory = findProductInventoryByCode("151"); //PRENSADO
            else
                productInventory = findProductInventoryByCode(articleOrder.getInvArticulos().getProductItemCode());

            if (productInventory != null){
                Double currentAmount = productInventory.getQuantity().doubleValue() + articleOrder.getTotal();
                productInventory.setQuantity(BigDecimal.valueOf(currentAmount));

                ProductInventoryRecord productInventoryRecord = new ProductInventoryRecord();
                productInventoryRecord.setId(getSecuenceNextValue("inv_product_register").longValue());
                productInventoryRecord.setProductItemCode(articleOrder.getInvArticulos().getProductItemCode());
                productInventoryRecord.setQuantity(BigDecimal.valueOf(articleOrder.getTotal()));
                productInventoryRecord.setType(ProductInventoryRecordType.DEVOLUTION);
                productInventoryRecord.setMovementType(ProductInventoryRecordType.DEVOLUTION.getType());
                productInventoryRecord.setDescription(productInventory.getProductItem().getFullName());
                productInventoryRecord.setProductInventory(productInventory);

                em.persist(productInventoryRecord);
                em.flush();
            }
        }

    }

    public void outputProducts(Pedidos pedido) {

        for ( ArticulosPedido articleOrder : pedido.getArticulosPedidos()){

            ProductInventory productInventory = null;
            if (articleOrder.getInvArticulos().getProductItemCode().equals("148")) //EDAM
                productInventory = findProductInventoryByCode("151"); //PRENSADO
            else
                productInventory = findProductInventoryByCode(articleOrder.getInvArticulos().getProductItemCode());

            if (productInventory != null) {
                Double stock = productInventory.getQuantity().doubleValue();
                stock = stock - articleOrder.getTotal().doubleValue();
                productInventory.setQuantity(BigDecimal.valueOf(stock));

                ProductInventoryRecord productInventoryRecord = new ProductInventoryRecord();
                productInventoryRecord.setId(getSecuenceNextValue("inv_product_register").longValue());
                productInventoryRecord.setProductItemCode(articleOrder.getInvArticulos().getProductItemCode());
                productInventoryRecord.setQuantity(BigDecimal.valueOf(articleOrder.getTotal()));
                productInventoryRecord.setType(ProductInventoryRecordType.DELIVERY_CUSTOMER_ORDER);
                productInventoryRecord.setMovementType(ProductInventoryRecordType.DELIVERY_CUSTOMER_ORDER.getType());
                productInventoryRecord.setDescription(productInventory.getProductItem().getFullName() + ", #" + pedido.getCodigo());
                productInventoryRecord.setProductInventory(productInventory);

                productInventory.getProductInventoryRecords().add(productInventoryRecord);
                pedido.setFlagstock(true);

                em.persist(productInventoryRecord);
                em.flush();
            }
        }
    }

    public ProductInventory findProductInventoryByCode(String productItemCode) {
        try {
            return (ProductInventory) em.createNamedQuery("ProductInventory.findByCode")
                    .setParameter("productItemCode", productItemCode)
                    .getSingleResult();
        }catch (NoResultException e){
            return null;
        }
    }

    public Integer getSecuenceNextValue(String table){
        Integer result;
        result = (Integer)em.createNativeQuery("SELECT valor FROM secuencia WHERE tabla = '"+table+"'").getSingleResult();
        em.createNativeQuery("update secuencia set valor = valor + 1 where tabla = '"+table+"'").executeUpdate();

        return result;
    }

    /**
     * Actualiza Articulo Substract
     * @param pedido
     */
    public void updateArticleSubtractTotalCost(Pedidos pedido){
        for ( ArticulosPedido article : pedido.getArticulosPedidos() ){
            double totalCost = article.getTotal().doubleValue() * article.getCu();
            double saldoMon  = totalCost * 0.87;
            updateArticleSubtractTotalCost(article.getInvArticulos().getProductItemCode(), totalCost, saldoMon);
        }
    }

    /**
     * Actualiza Articulo
     * @param ventadirecta
     */
    public void updateArticleSubtractTotalCost(Ventadirecta ventadirecta){
        for ( ArticulosPedido article : ventadirecta.getArticulosPedidos() ){
            double totalCost = article.getTotal().doubleValue() * article.getCu();
            double saldoMon  = totalCost * 0.87;
            updateArticleSubtractTotalCost(article.getInvArticulos().getProductItemCode(), totalCost, saldoMon);
        }
    }

    public void updateArticleSubtractTotalCost(String productItemCode, Double costSale, Double costoUniArticle){
        InvArticulos article = findByCodArt(productItemCode);
        Double saldo_mon = article.getSaldoMon();
        Double ct = article.getCt();

        article.setSaldoMon(saldo_mon - costoUniArticle);
        article.setCt(ct - costSale);

        em.flush();
    }

    /**
     * Actualiza Articulo
     * @param pedido
     */
    public void updateArticleSumTotalCost(Pedidos pedido){
        for ( ArticulosPedido article : pedido.getArticulosPedidos() ){
            double totalCost = article.getTotal().doubleValue() * article.getCu();
            double saldoMon  = totalCost * 0.87;
            updateArticleSumTotalCost(article.getInvArticulos().getProductItemCode(), totalCost, saldoMon);
        }
    }

    /**
     * Actualiza Articulo
     * @param ventadirecta
     */
    public void updateArticleSumTotalCost(Ventadirecta ventadirecta){
        for ( ArticulosPedido article : ventadirecta.getArticulosPedidos() ){
            double totalCost = article.getTotal().doubleValue() * article.getCu();
            double saldoMon  = totalCost * 0.87;
            updateArticleSumTotalCost(article.getInvArticulos().getProductItemCode(), totalCost, saldoMon);
        }
    }

    public void updateArticleSumTotalCost(String productItemCode, Double totalCost, Double saldoMon){
        InvArticulos article = findByCodArt(productItemCode);

        article.setSaldoMon(article.getSaldoMon() + saldoMon);
        article.setCt(article.getCt() + totalCost);

        em.flush();
    }

}
