/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.ejb;

import com.encens.khipus.model.*;
import com.encens.khipus.util.BigDecimalUtil;
import com.encens.khipus.util.JSFUtil;

import java.math.BigDecimal;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Diego
 */
@Stateless
public class PedidosFacade extends AbstractFacade<Pedidos> {
    @PersistenceContext(unitName = "khipusPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PedidosFacade() {
        super(Pedidos.class);
    }

    public List<Pedidos> findReposicionesPorPersona(Persona personaElegida) {
        List<Pedidos> pedidos = new ArrayList<>();
        try{
            //todo:cambiar yo no habra el estado rechazado
            //listar por el campo "por_reponer" 
            pedidos =(List<Pedidos>)em.createQuery("select pe from Pedidos pe " +
                    "inner join pe.articulosPedidos articulos " +
                    "where articulos.estado = 'RECHAZADO' AND pe.cliente =:cliente")
                                            .setParameter("cliente",personaElegida)
                                            .getResultList();
        }catch (NoResultException e){
            return pedidos;
        }        
        return pedidos;
    }

    public Pedidos findById(Long id){
        return (Pedidos)em.createQuery("select ped from Pedidos ped where ped.idpedidos =:idPedido")
                .setParameter("idPedido",id)
                .getSingleResult();
    }

    public String getSiguienteCodigo() {

        String numero = "0";
        try{
            numero = (String)em.createNativeQuery("SELECT getNextSeq('SECUENCIAPEDIDO')")
                    .getSingleResult();
        }catch (NoResultException e){
            return "0";
        }
        return numero;

    }

    public List<Object[]> recepcionDePedidos(){
        List<Object[]> resultado = new ArrayList<>();
        try {
            resultado = (List<Object[]>)em.createQuery("select concat(pe.codigo,'-',pe.cliente.nom,' ',pe.cliente.ap,' ',pe.cliente.am,pe.cliente.razonsocial) as CLIENTE\n" +
                    "               ,articulos.invArticulos.nombrecorto as PRODUCTO\n" +
                    "               ,articulos.cantidad + articulos.reposicion + articulos.promocion as CANTIDAD\n" +
                    "               ,pe.cliente.territoriotrabajo.nombre \n" +
                    "        from Pedidos pe join pe.articulosPedidos articulos" +
                    "        where pe.estado = 'PENDIENTE' and pe.estado = 'PREPARAR'")
                    .getResultList();

        }catch (NoResultException e){
            return  resultado;
        }
        return  resultado;
    }

    public Integer getTotalPedidos(Date fechaEntrega,Territoriotrabajo territoriotrabajo){
        Integer cantidad=0;
        try{
            if(fechaEntrega == null && territoriotrabajo == null) {
                cantidad = ((List<Pedidos>)em.createQuery("select  pe from Pedidos pe where pe.estado != 'ANULADO' and pe.estado != 'CONTABILIZADO' ").getResultList()).size();
            }
            else{
                if(territoriotrabajo != null)
                    cantidad = ((List<Pedidos>)em.createQuery("select  pe from Pedidos pe "
                        + "where pe.fechaEntrega =:fechaEntrega and pe.cliente.territoriotrabajo =:territoriotrabajo and pe.estado != 'ANULADO' and pe.estado != 'CONTABILIZADO'")
                        .setParameter("fechaEntrega", fechaEntrega)
                        .setParameter("territoriotrabajo",territoriotrabajo)
                        .getResultList()).size();
                else
                    cantidad = ((List<Pedidos>)em.createQuery("select  pe from Pedidos pe "
                            + "where pe.fechaEntrega =:fechaEntrega and pe.estado != 'ANULADO' and pe.estado != 'CONTABILIZADO' ")
                            .setParameter("fechaEntrega", fechaEntrega)
                            .getResultList()).size();
            }
        }catch (NoResultException e){
            return cantidad;
        }
        return cantidad;
    }
    
    public Integer getTotalPedidos(Date fechaEntrega, List<String> selectedTerritorios){
        List<Object[]> resultado = new ArrayList<>();
        String query = "";
        for (int i=0; i < selectedTerritorios.size(); i++ ) {
            if(selectedTerritorios.size() == 1)
                query = query + " and pe.cliente.territoriotrabajo.idterritoriotrabajo = " + selectedTerritorios.get(i) + " ";
            else {
                if(i==0)
                    query = query + " and ( pe.cliente.territoriotrabajo.idterritoriotrabajo = " + selectedTerritorios.get(i) + "\n";
                else{
                    if(i < selectedTerritorios.size()-1)
                        query = query + " or pe.cliente.territoriotrabajo.idterritoriotrabajo = " + selectedTerritorios.get(i) + "\n";
                    else
                        query = query + " or pe.cliente.territoriotrabajo.idterritoriotrabajo = " + selectedTerritorios.get(i) + ") ";
                }
            }   
        }
        try {   
                resultado = (List<Object[]>)em.createQuery(""+
                    " select pe " +
                    " from Pedidos pe " +
                    " where pe.fechaEntrega =:fechaEntrega " +
                    query +
                    /*" and pe.estado != 'ANULADO' and pe.estado != 'CONTABILIZADO'")*/
                    " and pe.estado != 'ANULADO' and pe.tipoventa = 'CREDIT' ")
                    .setParameter("fechaEntrega", fechaEntrega, TemporalType.DATE)
                    .getResultList();

        }catch (NoResultException e){
            return  0;
        }
        return  resultado.size();
    }

    public BigDecimal getImporteTotal(Date fechaEntrega, List<String> selectedTerritorios){
        BigDecimal result = BigDecimal.ZERO;
        List<Pedidos> resultado = new ArrayList<>();
        String query = "";
        for (int i=0; i < selectedTerritorios.size(); i++ ) {
            if(selectedTerritorios.size() == 1)
                query = query + " and pe.cliente.territoriotrabajo.idterritoriotrabajo = " + selectedTerritorios.get(i) + " ";
            else {
                if(i==0)
                    query = query + " and ( pe.cliente.territoriotrabajo.idterritoriotrabajo = " + selectedTerritorios.get(i) + "\n";
                else{
                    if(i < selectedTerritorios.size()-1)
                        query = query + " or pe.cliente.territoriotrabajo.idterritoriotrabajo = " + selectedTerritorios.get(i) + "\n";
                    else
                        query = query + " or pe.cliente.territoriotrabajo.idterritoriotrabajo = " + selectedTerritorios.get(i) + ") ";
                }
            }
        }
        try {
            resultado = (List<Pedidos>)em.createQuery(""+
                    " select pe " +
                    " from Pedidos pe " +
                    " where pe.fechaEntrega =:fechaEntrega " +
                    query +
                    " and pe.estado != 'ANULADO' and pe.tipoventa = 'CREDIT' ")
                    .setParameter("fechaEntrega", fechaEntrega, TemporalType.DATE)
                    .getResultList();


            //System.out.println("----> CANT: " + resultado.size());
            //System.out.println("----> PEDIDOS: " + resultado);
            //System.out.println("----> PEDIDO 0: " + resultado.get(0));


            for (Pedidos pedido : resultado){
                if (pedido.getCliente().getTerritoriotrabajo().getIdterritoriotrabajo() == 24 || pedido.getCliente().getTerritoriotrabajo().getIdterritoriotrabajo() == 25){
                    for (ArticulosPedido articulo : pedido.getArticulosPedidos()){
                        Double monto = articulo.getCantidad() * articulo.getInvArticulos().getPrecioVenta();
                        result = BigDecimalUtil.sum(result, BigDecimalUtil.toBigDecimal(monto));
                    }
                }else {
                    result = BigDecimalUtil.sum(result, BigDecimalUtil.toBigDecimal(pedido.getTotalimporte()));
                }
            }



        }catch (NoResultException e){
            return BigDecimal.ZERO;
        }
        return  result;
    }

    public List<Object[]> recepcionDePedidos(Date fechaEntrega,Territoriotrabajo territoriotrabajo){
        List<Object[]> resultado = new ArrayList<>();
        try {
            if(territoriotrabajo != null)
                resultado = (List<Object[]>)em.createQuery("select concat(pe.codigo,'-',pe.cliente.nom,' ',pe.cliente.ap,' ',pe.cliente.am,pe.cliente.razonsocial) as CLIENTE\n" +
                    "               ,articulos.invArticulos.nombrecorto as PRODUCTO\n" +
                    "               ,articulos.cantidad + articulos.reposicion + articulos.promocion as CANTIDAD\n" +
                    "               ,pe.cliente.territoriotrabajo.nombre as DISTRIBUIDOR\n" +
                    "        from Pedidos pe join pe.articulosPedidos articulos" +
                    "        where pe.fechaEntrega =:fechaEntrega and pe.cliente.territoriotrabajo =:territoriotrabajo " +
                    "              and pe.estado != 'ANULADO' and pe.estado != 'CONTABILIZADO'")
                    .setParameter("fechaEntrega", fechaEntrega, TemporalType.DATE)
                    .setParameter("territoriotrabajo",territoriotrabajo)
                    .getResultList();
            else
                resultado = (List<Object[]>)em.createQuery("select concat(pe.codigo,'-',pe.cliente.nom,' ',pe.cliente.ap,' ',pe.cliente.am,pe.cliente.razonsocial) as CLIENTE\n" +
                        "               ,articulos.invArticulos.nombrecorto as PRODUCTO\n" +
                        "               ,articulos.cantidad + articulos.reposicion + articulos.promocion as CANTIDAD\n" +
                        "               ,pe.cliente.territoriotrabajo.nombre as DISTRIBUIDOR\n" +
                        "        from Pedidos pe join pe.articulosPedidos articulos" +
                        "        where pe.fechaEntrega =:fechaEntrega and pe.estado != 'ANULADO' and pe.estado != 'CONTABILIZADO'")
                        .setParameter("fechaEntrega", fechaEntrega, TemporalType.DATE)
                        .getResultList();

        }catch (NoResultException e){
            return  resultado;
        }
        return  resultado;
    }
    
    public List<Object[]> recepcionDePedidos(Date fechaEntrega, List<String> selectedTerritorios){
        List<Object[]> resultado = new ArrayList<>();
        
        String query = "";
        for (int i=0; i < selectedTerritorios.size(); i++ ) {

            
            if(selectedTerritorios.size() == 1)
                query = query + " and pe.cliente.territoriotrabajo.idterritoriotrabajo = " + selectedTerritorios.get(i) + " ";
            else {
                if(i==0)
                    query = query + " and ( pe.cliente.territoriotrabajo.idterritoriotrabajo = " + selectedTerritorios.get(i) + "\n";
                else{
                    if(i < selectedTerritorios.size()-1)
                        query = query + " or pe.cliente.territoriotrabajo.idterritoriotrabajo = " + selectedTerritorios.get(i) + "\n";
                    else
                        query = query + " or pe.cliente.territoriotrabajo.idterritoriotrabajo = " + selectedTerritorios.get(i) + ") ";
                }
            }   
        }
        



        
        try {
                resultado = (List<Object[]>)em.createQuery(""+
                    " select concat(pe.cliente.nom,' ',pe.cliente.ap,' ',pe.cliente.am,' ',pe.codigo) as CLIENTE\n" +
                    "               ,articulos.invArticulos.nombrecorto as PRODUCTO\n" +
                    "               ,articulos.cantidad + articulos.reposicion + articulos.promocion as CANTIDAD\n" +
                    "               ,pe.cliente.territoriotrabajo.nombre as DISTRIBUIDOR\n" +
                    " from Pedidos pe join pe.articulosPedidos articulos" +
                    " where pe.fechaEntrega =:fechaEntrega " +
                    query +
                    " and pe.estado != 'ANULADO' and pe.usuario.idusuario <> 5 " +
                    " and pe.tipoventa = 'CREDIT' ") /** MODIFYID **/
                    .setParameter("fechaEntrega", fechaEntrega, TemporalType.DATE)
                    .getResultList();

        }catch (NoResultException e){
            return  resultado;
        }
        return  resultado;
    }

    public List<RecepcionPedido> recepcionDePedidosTodos(){
        List<Object[]> datas = new ArrayList<>();
        List<RecepcionPedido> recepcionPedidos = new ArrayList<>();
        try {
            datas = (List<Object[]>)em.createQuery("select concat(pe.codigo,'-',pe.cliente.nom,' ',pe.cliente.ap,' ',pe.cliente.am,pe.cliente.razonsocial) as CLIENTE\n" +
                    "               ,articulos.invArticulos.nombrecorto as PRODUCTO\n" +
                    "               ,articulos.cantidad + articulos.reposicion + articulos.promocion as CANTIDAD\n" +
                    "               ,pe.cliente.territoriotrabajo.nombre as DISTRIBUIDOR\n" +
                    "        from Pedidos pe join pe.articulosPedidos articulos")
                    .getResultList();

        }catch (NoResultException e){
            return  recepcionPedidos;
        }
        for(Object[] data: datas)
        {
            RecepcionPedido recepcionPedido = new RecepcionPedido((String)data[0],(String)data[1],(Integer)data[2],(String)data[3]);
            recepcionPedidos.add(recepcionPedido);
        }
        return  recepcionPedidos;
    }

    public List<RecepcionPedido> recepcionDePedidosPorFechaTerritorio(Date fechaEntrega,Territoriotrabajo territoriotrabajo){
        List<Object[]> datas = new ArrayList<>();
        List<RecepcionPedido> recepcionPedidos = new ArrayList<>();
        try {
            datas = (List<Object[]>)em.createQuery("select concat(pe.codigo,'-',pe.cliente.nom,' ',pe.cliente.ap,' ',pe.cliente.am,pe.cliente.razonsocial) as CLIENTE\n" +
                    "               ,articulos.invArticulos.descri as PRODUCTO\n" +
                    "               ,articulos.cantidad + articulos.reposicion + articulos.promocion as CANTIDAD\n" +
                    "               ,pe.cliente.territoriotrabajo.distribuidor.nom as DISTRIBUIDOR\n" +
                    "        from Pedidos pe join pe.articulosPedidos articulos" +
                    "        where pe.fechaEntrega =:fechaEntrega" +
                    "        or pe.cliente.territoriotrabajo =:territoriotrabajo")
                    .setParameter("fechaEntrega", fechaEntrega, TemporalType.DATE)
                    .setParameter("territoriotrabajo",territoriotrabajo)
                    .getResultList();

        }catch (NoResultException e){
            return  recepcionPedidos;
        }
        for(Object[] data: datas)
        {
            RecepcionPedido recepcionPedido = new RecepcionPedido((String)data[0],(String)data[1],(Integer)data[2],(String)data[3]);
            recepcionPedidos.add(recepcionPedido);
        }
        return  recepcionPedidos;
    }

    public List<Pedidos> findPedidosOrdDecs() {
        List<Pedidos> result = new ArrayList<>();
        try{
            em.flush();
            result = (List<Pedidos>)em.createQuery("select pe from Pedidos pe order by pe.idpedidos desc").getResultList();
        }catch (NoResultException e){
            return result;
        }
        return result;
    }

    public List<Pedidos> findPedidos(Date startDate, Date endDate) {
        List<Pedidos> result = new ArrayList<>();
        try{
            em.flush();
            result = (List<Pedidos>)em.createQuery("select pe from Pedidos pe " +
                    "where pe.fechaEntrega between :startDate and :endDate " +
                    "and pe.movimiento is not null " +
                    "order by pe.idpedidos desc")
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .getResultList();
        }catch (NoResultException e){
            return result;
        }
        return result;
    }

    public List<Pedidos> findPedidosFrom(Date dateFrom) {
        List<Pedidos> result = new ArrayList<>();
        try{
            em.flush();
            result = (List<Pedidos>)em.createQuery("select pe from Pedidos pe " +
                    "where pe.fechaEntrega >=:dateFrom " +
                    "order by pe.idpedidos desc")
                    .setParameter("dateFrom", dateFrom)
                    .getResultList();
        }catch (NoResultException e){
            return result;
        }
        return result;
    }

    public List<Pedidos> findPedidosFromLac(Date dateFrom) {
        List<Pedidos> result = new ArrayList<>();
        try{
            em.flush();
            result = (List<Pedidos>)em.createQuery("select pe from Pedidos pe " +
                    "where pe.fechaEntrega >=:dateFrom " +
                    "and pe.usuario.usuario not in ('cisc') " +
                    "and pe.tipoventa = 'CREDIT' " +
                    "order by pe.idpedidos desc")
                    .setParameter("dateFrom", dateFrom)
                    .getResultList();
        }catch (NoResultException e){
            return result;
        }
        return result;
    }

    /**  **/
    public List<Pedidos> findPedidosAgain() {
        List<Pedidos> result = new ArrayList<>();
        try{
            em.flush();
            result = (List<Pedidos>)em.createQuery("select pe from Pedidos pe " +
                    "where pe.descripcion = 'AGAIN'")
                    .getResultList();
        }catch (NoResultException e){
            return result;
        }
        return result;
    }

    /** TODO **/
    public List<Pedidos> findPedidosFromCisc(Date dateFrom, Usuario usuario) {
        List<Pedidos> result = new ArrayList<>();
        try{
            em.flush();
            result = (List<Pedidos>)em.createQuery("select pe from Pedidos pe " +
                    "where pe.fechaEntrega >=:dateFrom " +
                    "and pe.usuario =:usuario " +
                    "order by pe.idpedidos desc ")
                    .setParameter("dateFrom", dateFrom)
                    .setParameter("usuario", usuario)
                    .getResultList();
        }catch (NoResultException e){
            return result;
        }
        return result;
    }

    
    public void anularAsiento(SfTmpenc asiento){

        if (asiento != null){
            asiento.setEstado("ANL");

            em.merge(asiento);
            em.flush();
        }
    }
    
    public void revertirStock(Pedidos pedido){
        
        for ( ArticulosPedido articuloPedido : pedido.getArticulosPedidos() ){
            inputProducts(articuloPedido.getInvArticulos().getProductItemCode(), articuloPedido.getTotal().doubleValue(), ProductInventoryRecordType.DEVOLUTION);
        }
        
    }
    
    public void inputProducts(String productItemCode, Double quantity, ProductInventoryRecordType productInventoryRecordType) {

        ProductInventory productInventory = findProductInventoryByCode(productItemCode);
        Double currentAmount = productInventory.getQuantity().doubleValue() + quantity;
        productInventory.setQuantity(BigDecimal.valueOf(currentAmount));

        ProductInventoryRecord productInventoryRecord = new ProductInventoryRecord();
        productInventoryRecord.setId(getSecuencia("inv_product_register"));
        updateSecuencia("inv_product_register");
        productInventoryRecord.setProductItemCode(productItemCode);
        productInventoryRecord.setQuantity(BigDecimal.valueOf(quantity));
        productInventoryRecord.setType(productInventoryRecordType);
        productInventoryRecord.setMovementType(productInventoryRecordType.getType());
        productInventoryRecord.setDescription(productInventory.getProductItem().getDescri());
        productInventoryRecord.setProductInventory(productInventory);

        em.persist(productInventoryRecord);
        em.flush();
    }
    
    public ProductInventory findProductInventoryByCode(String productItemCode) {
        try {
            return (ProductInventory) em.createQuery("SELECT p "
                                                   + "FROM ProductInventory p "
                                                   + "WHERE p.productItemCode =:productItemCode")
                    .setParameter("productItemCode", productItemCode)
                    .getSingleResult();
        }catch (NoResultException e){
            return null;
        }
    }

    public BigDecimal findInventoryByCode(String cod_art){

        BigDecimal result = BigDecimal.ZERO;
        try {
            result = (BigDecimal) em.createNativeQuery("SELECT i.saldo_uni "
                    + "FROM inv_inventario i "
                    + "WHERE i.cod_art = '"+ cod_art +"'")
                    .getSingleResult();
        }catch (NoResultException e){
            e.printStackTrace();
        }
        return result;
    }
    
    public long getSecuencia(String nombreTabla){
        Integer result = 0;
        try {
            result = (Integer) em.createNativeQuery("SELECT s.valor "
                                                   + "FROM secuencia s "
                                                   + "WHERE s.tabla = '"+nombreTabla+"'")
                    .getSingleResult();
        }catch (NoResultException e){
            e.printStackTrace();
        }
        return result.longValue();
    }
    
    public void updateSecuencia(String nombreTabla){
        int result = 0;
        try {
            result = em.createNativeQuery("UPDATE secuencia set valor = valor + 1 "
                               + "WHERE  tabla = '"+nombreTabla+"'").executeUpdate();
            
        }catch (NoResultException e){
            e.printStackTrace();
        }

    }

    /** Suma inventario INV_INVENTARIO, INV_INVENTARIO_DETALLE **/
    public void sumarInventario(Pedidos pedido){

        for ( ArticulosPedido articulo : pedido.getArticulosPedidos() ){

            em.createNativeQuery(" UPDATE inv_inventario SET saldo_uni = saldo_uni + " + articulo.getTotal() +
                    " WHERE cod_art = " + articulo.getInvArticulos().getProductItemCode())
                    .executeUpdate();

            em.createNativeQuery(" UPDATE inv_inventario_detalle id " +
                    " JOIN inv_inventario i ON id.cod_art = i.cod_art " +
                    " SET id.cantidad = i.saldo_uni " +
                    " WHERE i.cod_art = " + articulo.getInvArticulos().getProductItemCode())
                    .executeUpdate();
        }
    }

    public boolean insertarDescuento(Pedidos pedido){

        String nrodoc = pedido.getCliente().getNroDoc();
        Long id = (Long) em.createNativeQuery("SELECT MAX(idmovimientosalarioproductor)+1 FROM movimientosalarioproductor").getSingleResult();

        Long identidad = null;
        Long idzonaproductiva = null;

        try { /** MODIFYID **/
            identidad = (Long) em.createNativeQuery("SELECT e.identidad FROM entidad e WHERE e.noidentificacion = "+nrodoc).getSingleResult();
            idzonaproductiva = (Long) em.createNativeQuery("SELECT idzonaproductiva FROM productormateriaprima WHERE idproductormateriaprima = "+identidad).getSingleResult();
        }catch (NoResultException e){
            JSFUtil.addErrorMessage("Error, datos inconsistentes..."); // ???
            return false;
        }

        System.out.println("-----> identidad: " + identidad);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Double valor = pedido.getTotalimporte();
        String fecha = df.format(pedido.getFechaEntrega());
        String descripcion = pedido.getIdtipopedido().getNombre() + " venta a credito Nro. " + pedido.getCodigo();

        String idtipomovimiento = "3"; //Lacteos MODIFYID

        if (pedido.getIdtipopedido().getNombre().equals("DESC_VETERINARIO"))
            idtipomovimiento = "4"; //Veterinario MODIFYID


        String query =  "INSERT INTO movimientosalarioproductor (idmovimientosalarioproductor, fecha, descripcion, estado, valor, idcompania, idzonaproductiva, idproductormateriaprima, idtipomovimientoproductor) " +
                        "VALUES (" + id + ",'" + fecha + "','" + descripcion + "','PENDING'," + valor + ", 1 , " + idzonaproductiva + ", " + identidad + "," + idtipomovimiento + ");";

        em.createNativeQuery(query).executeUpdate();
        em.createNativeQuery("UPDATE SECUENCIA SET VALOR=(SELECT MAX(E.IDMOVIMIENTOSALARIOPRODUCTOR)+1 FROM MOVIMIENTOSALARIOPRODUCTOR E) WHERE TABLA='MOVIMIENTOSALARIOPRODUCTOR'").executeUpdate();

        return  true;
    }

    public class RecepcionPedido{
        private String cliente;
        private String producto;
        private Integer cantidad;
        private String distribuidor;

        public RecepcionPedido(String cliente, String producto, Integer cantidad, String distribuidor) {
            this.cliente = cliente;
            this.producto = producto;
            this.cantidad = cantidad;
            this.distribuidor = distribuidor;
        }

        public String getCliente() {
            return cliente;
        }

        public void setCliente(String cliente) {
            this.cliente = cliente;
        }

        public String getProducto() {
            return producto;
        }

        public void setProducto(String producto) {
            this.producto = producto;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }

        public String getDistribuidor() {
            return distribuidor;
        }

        public void setDistribuidor(String distribuidor) {
            this.distribuidor = distribuidor;
        }
    }
}
