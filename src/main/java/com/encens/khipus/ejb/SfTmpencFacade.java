/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.ejb;

import com.encens.khipus.model.*;
import com.encens.khipus.util.BigDecimalUtil;
import com.encens.khipus.util.DateUtils;
import org.omg.CORBA.PUBLIC_MEMBER;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.ParseException;import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author Diego
 */
@Stateless
public class SfTmpencFacade extends AbstractFacade<SfTmpenc> {
    @PersistenceContext(unitName = "khipusPU")
    private EntityManager em;

    @PersistenceContext(unitName = "khipusPU")
    private EntityManager em2;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SfTmpencFacade() {
        super(SfTmpenc.class);
    }

    public String getSiguienteNumeroTransacccion() {
        String numero = "0";
        try{
            numero = (String)em.createNativeQuery("SELECT getNextSeq('ASIENTO')").getSingleResult();
        }catch (NoResultException e){
            return "0";
        }
        return numero;
    }

    public String getSiguienteNumeroPorNombre(String nombre) {
        String numero = "0";
        try{
            numero = (String)em.createNativeQuery("SELECT getNextSeq('"+nombre+"')")
                    .getSingleResult();
        }catch (NoResultException e){
            return "0";
        }
        return numero;
    }

    public List<SfTmpenc> findKardex() {
        List<SfTmpenc> sfTmpencs = new ArrayList<>();
        try{
            sfTmpencs = (List<SfTmpenc>)em.createQuery("select  asiento.fecha\n" +
                    "               ,asiento.tipoDoc\n" +
                    "               ,asiento.noDoc\n" +
                    "               ,asiento.nombreCliente\n" +
                    "               ,asiento.debe\n" +
                    "               ,asiento.haber\n" +
                    "        from SfTmpenc asiento\n" +
                    "        join asiento.pedidos pedido\n" +
                    "        join asiento.pagos  pago\n" +
                    "        join asiento.ventadirectas venta\n" +
                    "        where asiento.cliente.tipoPersona = 'cliente' and asiento.cliente.tipoPersona = 'institucion'\n" +
                    "        group by asiento.cliente.piId\n")
                        .getResultList();
        }catch (NoResultException e){
            return sfTmpencs;
        }
        return sfTmpencs;
    }

    public List<SfTmpenc> findKardexByClienteAndRangoFecha(Persona cliente,Date fechaIni,Date fechaFin){
        List<SfTmpenc> kardex = new ArrayList<>();
        try{
            kardex = (List<SfTmpenc>)em.createQuery("select  asiento.fecha\n" +
                    "               ,asiento.tipoDoc\n" +
                    "               ,asiento.noDoc\n" +
                    "               ,asiento.nombreCliente\n" +
                    "               ,asiento.debe\n" +
                    "               ,asiento.haber\n" +
                    "        from SfTmpenc asiento\n" +
                    "        join asiento.pedidos pedido\n" +
                    "        join asiento.pagos  pago\n" +
                    "        join asiento.ventadirectas venta\n" +
                    "        where asiento.cliente.piId =:idCliente\n" +
                    "        and asiento.fecha between :fechaIni and :fechaFin\n" +
                    "        group by asiento.cliente.piId")
                    .setParameter("idCliente",cliente.getPiId())
                    .setParameter("fechaIni",fechaIni)
                    .setParameter("fechaFin",fechaFin)
                    .getResultList();
        }catch (NoResultException e)
        {
            return kardex;
        }
        return kardex;
    }

    /** TMP **/
    public void actualizarCI(String id){
        SfTmpenc asiento = null;
        Long idTmpenc = new Long(id);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String fechaInicio = "2016-08-01";
        String fechaFin = "2016-08-31";
        Date ini = null;
        Date fin = null;

        List<Ventadirecta> ventas = new ArrayList<>();

        try {
            ini = sdf.parse(fechaInicio);
            fin = sdf.parse(fechaFin);
            } catch (ParseException e) {e.printStackTrace();}
        try{

            //Double totalD = new Double(0);
            //Double totalH = new Double(0);
            BigDecimal totalD = new BigDecimal(0);
            BigDecimal totalH = new BigDecimal(0);

            ventas = (List<Ventadirecta>)em.createQuery(" SELECT v FROM Ventadirecta v " +
                           " WHERE v.movimiento is not null " +
                           " AND v.fechaPedido between :fechaIni AND :fechaFin " +
                           " AND v.estado <> 'ANULADO' ")
                           .setParameter("fechaIni", ini)
                           .setParameter("fechaFin", fin)
                           .getResultList();

            System.out.println("["+ventas.size()+"]");
            for (Ventadirecta venta:ventas){

            //if (venta.getAsiento().getNoDoc().equals("5990")){
                /*for (SfTmpdet det:venta.getAsiento().getAsientos()){
                    //totalD += det.getDebe().doubleValue();
                    //totalH += det.getHaber().doubleValue();
                    totalD = BigDecimalUtil.sum(totalD, det.getDebe(), 2);
                    totalH = BigDecimalUtil.sum(totalH, det.getHaber(), 2);
                }*/

                /*Double caja      = new Double(0);
                Double it        = new Double(0);
                Double importe87 = new Double(0);
                Double iva       = new Double(0);*/

                BigDecimal caja     = new BigDecimal("0.00");
                BigDecimal it       = new BigDecimal("0.00");
                BigDecimal importe87 = new BigDecimal("0.00");
                BigDecimal iva = new BigDecimal("0.00");

                for (SfTmpdet det:venta.getAsiento().getAsientos()){
                    if (det.getCuenta().equals("1110110100")){
                        caja = det.getDebe();
                    }

                    if (det.getCuenta().equals("4470510700")){
                        //it = BigDecimalUtil.roundDouble(caja*0.03, 2);
                        //det.setDebe(BigDecimalUtil.toBigDecimal(it));
                        it = BigDecimalUtil.multiply(caja, new BigDecimal("0.03"), 2);
                        det.setDebe(it);
                    }

                    if (det.getCuenta().equals("5420110201") || det.getCuenta().equals("5610110400") || det.getCuenta().equals("5420110100")){
                       //importe87 = BigDecimalUtil.roundDouble(caja*0.87, 2);
                       //det.setHaber(BigDecimalUtil.toBigDecimal(importe87));
                       importe87 = BigDecimalUtil.multiply(caja, new BigDecimal("0.87"), 2);
                       det.setHaber(importe87);
                    }

                    if (det.getCuenta().equals("2420410200")){
                        //iva = BigDecimalUtil.roundDouble(caja*0.13, 2);
                        //det.setHaber(BigDecimalUtil.toBigDecimal(iva));
                        iva = BigDecimalUtil.multiply(caja, new BigDecimal("0.13"), 2);
                        det.setHaber(iva);
                    }

                    if (det.getCuenta().equals("2420410100")){
                        //det.setHaber(BigDecimalUtil.toBigDecimal(it));
                        det.setHaber(it);
                    }
                }
            }
            em.flush();
            System.out.println("----> FOR DIFERENCES...");
            System.out.println("......START......");
            ventas = (List<Ventadirecta>)em2.createQuery(" SELECT v FROM Ventadirecta v " +
                           " WHERE v.movimiento is not null " +
                           " AND v.fechaPedido between :fechaIni AND :fechaFin " +
                           " AND v.estado <> 'ANULADO' ")
                           .setParameter("fechaIni", ini)
                           .setParameter("fechaFin", fin)
                           .getResultList();

            System.out.println("["+ventas.size()+"]");

            for (Ventadirecta venta:ventas){
                totalD = new BigDecimal("0.00");
                totalH = new BigDecimal("0.00");

                for (SfTmpdet det:venta.getAsiento().getAsientos()){
                    //totalD += det.getDebe().doubleValue();
                    //totalH += det.getHaber().doubleValue();
                    totalD = BigDecimalUtil.sum(totalD, det.getDebe(), 2);
                    totalH = BigDecimalUtil.sum(totalH, det.getHaber(), 2);
                }

                //totalD = BigDecimalUtil.roundDouble(totalD, 2);
                //totalH = BigDecimalUtil.roundDouble(totalH, 2);

                //System.out.println("---> " + venta.getAsiento().getTipoDoc() + venta.getAsiento().getNoDoc() + " - " + BigDecimalUtil.subtract(totalD, totalH, 2));

                /** Diferencias **/
                for (SfTmpdet det:venta.getAsiento().getAsientos()){
                    if (det.getCuenta().equals("5420110201") || det.getCuenta().equals("5610110400") || det.getCuenta().equals("5420110100")){
                        //Double newValue = det.getHaber().doubleValue() + (totalD - totalH);
                        //det.setHaber(BigDecimalUtil.toBigDecimal(newValue));
                        BigDecimal diff = BigDecimalUtil.subtract(totalD, totalH, 2);
                        BigDecimal newValue  = BigDecimalUtil.sum(det.getHaber(), diff , 2);
                        det.setHaber(newValue);

                        em2.flush();
                    }
                }

            }
            System.out.println("......END......");
        //}
        }catch (NoResultException e){}
    }

    public Collection<Kardex> getKardexcliente(Persona personaElegida, Date fechaIni, Date fechaFin,String cuenta,Sucursal sucursal) {
        List<Kardex> kardex = new ArrayList<>();
        List<Object[]> datos = new ArrayList<>();
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        String fechaInicio = formato.format(fechaIni);
        String fechaFinal = formato.format(fechaFin);
        /* Antes
        String sql = "SELECT * FROM\n" +
                "(" +
                "SELECT enc.fecha,enc.tipo_doc,enc.no_doc,enc.glosa,det.debe,0 AS haber, enc.id_tmpenc FROM sf_tmpenc enc\n" +
                            "JOIN sf_tmpdet det ON det.id_tmpenc = enc.id_tmpenc\n" +
                            "JOIN pedidos ped ON ped.id_tmpenc = enc.id_tmpenc \n" +
                            "WHERE det.cuenta = '" + cuenta + "'\n" +
                            "AND ped.estado <> 'ANULADO'\n" +
                            "AND enc.fecha BETWEEN '" + fechaInicio + "' AND '" + fechaFinal + "' \n" +
                            "AND ped.IDCLIENTE = '" + personaElegida.getPiId() + "'\n" +
                            "and ped.IDSUCURSAL = '" +sucursal.getIdsucursal()+ "'\n" +
                            "UNION\n" +
                            "SELECT enc.fecha,enc.tipo_doc,enc.no_doc,enc.glosa,0 AS debe,det.haber, enc.id_tmpenc FROM sf_tmpenc enc\n" +
                            "JOIN sf_tmpdet det ON det.id_tmpenc = enc.id_tmpenc\n" +
                            "JOIN pago pago ON pago.id_tmpenc = enc.id_tmpenc \n" +
                            "WHERE det.cuenta = '" + cuenta + "'\n" +
                            "AND enc.fecha BETWEEN '" + fechaInicio + "' AND '" + fechaFinal + "' \n" +
                            "AND pago.IDPERSONACLIENTE = '" + personaElegida.getPiId() + "'\n" +
                            "and pago.IDSUCURSAL = '" +sucursal.getIdsucursal()+ "'\n" +
                            ") AS kardex\n" +
                            "ORDER BY kardex.fecha ASC";*/

        String sql =
                "SELECT enc.fecha, enc.tipo_doc, enc.no_doc, enc.glosa, det.debe, det.haber, enc.id_tmpenc " +
                "FROM sf_tmpdet det " +
                "JOIN sf_tmpenc enc ON enc.id_tmpenc = det.id_tmpenc " +
                "WHERE det.idpersonacliente = '" + personaElegida.getPiId() + "' " +
                "AND det.cuenta = '" + cuenta + "' " +
                "AND enc.estado <> 'ANL' " +
                "AND enc.fecha BETWEEN '" + fechaInicio + "' AND '" + fechaFinal + "' " +
                "ORDER BY enc.fecha ASC";
        try{
            datos = (List<Object[]>)em.createNativeQuery(sql)
                    .getResultList();
        }catch (NoResultException e){
                return kardex;
        }
        Double debe = 0.0;
        Double haber = 0.0;
        Double saldo = 0.0;
        Double totalDebe = 0.0;
        Double totalHaber = 0.0;
        Double totalSaldo = 0.0;
        for(Object[] dato:datos)
        {
            /*debe  = ((BigDecimal)dato[4]).doubleValue();
            haber = ((BigDecimal)dato[5]).doubleValue();*/
            debe  = ((BigDecimal)dato[4]).doubleValue();
            haber = ((BigDecimal)dato[5]).doubleValue();
            saldo += debe;
            saldo -= haber;
            totalDebe += debe;
            totalHaber += haber;
            totalSaldo += saldo;
            Kardex kar = new Kardex();
            kar.setFecha((Date)dato[0]);
            kar.setTipoDoc((String) dato[1]);
            kar.setNoDoc((String) dato[2]);
            kar.setGlosa((String)dato[3]);
            kar.setDebe(debe);
            kar.setHaber(haber);
            kar.setSaldo(saldo);
            kar.setTotalDebe(totalDebe);
            kar.setTotalHaber(totalHaber);
            //kar.setTotalSaldo(totalSaldo);
            kar.setTotalSaldo(saldo);
            kardex.add(kar);
        }

        return kardex;
    }

    public Collection<Recaudacion> getRecaudacionesUsuario(Usuario usuario, Date fechaIni, Date fechaFin, String cuenta) {
        List<Recaudacion> recaudaciones = new ArrayList<>();
        List<Object[]> datos = new ArrayList<>();
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        String fechaInicio = formato.format(fechaIni);
        String fechaFinal = formato.format(fechaFin);
        /*String sql = "SELECT * FROM\n" +
                "(\n" +
                "SELECT enc.fecha,enc.tipo_doc,enc.no_doc,enc.glosa,det.debe,0 AS haber FROM sf_tmpenc enc\n" +
                "JOIN sf_tmpdet det \n" +
                "ON det.id_tmpenc = enc.id_tmpenc\n" +
                "WHERE det.debe IS NOT NULL\n" +
                "AND det.cuenta = "+cuenta+"\n" +
                "and enc.idusuario = "+usuario.getIdusuario()+"\n" +
                "AND enc.fecha BETWEEN '"+fechaInicio+"' AND '"+fechaFinal+"'\n" +
                "UNION\n" +
                "SELECT enc.fecha,enc.tipo_doc,enc.no_doc,enc.glosa,0 AS debe,det.haber FROM sf_tmpenc enc\n" +
                "JOIN sf_tmpdet det \n" +
                "ON det.id_tmpenc = enc.id_tmpenc\n" +
                "WHERE det.haber IS NOT NULL\n" +
                "AND det.cuenta = "+cuenta+"\n" +
                "and enc.idusuario = "+usuario.getIdusuario()+"\n" +
                "AND enc.fecha BETWEEN '"+fechaInicio+"' AND '"+fechaFinal+"'\n" +
                ")AS recaudacion\n" +
                "ORDER BY recaudacion.fecha DESC";*/
        String sql = "SELECT * FROM\n" +
                "(\n" +
                "SELECT enc.fecha,enc.tipo_doc,enc.no_doc,enc.glosa,det.debe, det.haber FROM sf_tmpenc enc\n" +
                "JOIN sf_tmpdet det \n" +
                "ON det.id_tmpenc = enc.id_tmpenc\n" +
                "WHERE det.cuenta = "+cuenta+"\n"+ 
                "AND enc.estado <> 'ANL'\n" +
                "and enc.idusuario = "+usuario.getIdusuario()+"\n" +
                "AND enc.fecha BETWEEN '"+fechaInicio+"' AND '"+fechaFinal+"'\n" +
                ")AS recaudacion\n" +
                "ORDER BY recaudacion.fecha, recaudacion.no_doc ASC";
        try{
            datos = (List<Object[]>)em.createNativeQuery(sql)
                    .getResultList();
        }catch (NoResultException e){
            return recaudaciones;
        }
        System.out.println("------SIZE DATOS: " + datos.size());
        Double debe = 0.0;
        Double haber = 0.0;
        Double saldo = 0.0;
        Double totalDebe = 0.0;
        Double totalHaber = 0.0;
        Double totalSaldo = 0.0;
        for(Object[] dato:datos)
        {
            debe  = ((BigDecimal)dato[4]).doubleValue();
            haber = ((BigDecimal)dato[5]).doubleValue();
            saldo += debe;
            saldo -= haber;
            totalDebe += debe;
            totalHaber += haber;
            totalSaldo += saldo;
            Recaudacion recaudacion = new Recaudacion();
            recaudacion.setFecha((Date) dato[0]);
            recaudacion.setTipoDoc((String) dato[1]);
            recaudacion.setNoDoc((String) dato[2]);
            recaudacion.setGlosa((String) dato[3]);
            recaudacion.setIngreso(debe);
            recaudacion.setEgreso(haber);
            recaudacion.setSaldo(saldo);
            recaudacion.setTotalDebe(totalDebe);
            recaudacion.setTotalHaber(totalHaber);
            recaudacion.setTotalSaldo(totalSaldo);
            recaudaciones.add(recaudacion);
        }
        return recaudaciones;
    }
    
    public Collection<ResumenRecaudacion> getResumenRecaudaciones(Usuario usuario, Date fechaIni, Date fechaFin, String cuenta) {
        List<ResumenRecaudacion> resumenRecaudacionList = new ArrayList<>();
        List<Object[]> datos = new ArrayList<>();
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        String fechaInicio = formato.format(fechaIni);
        String fechaFinal = formato.format(fechaFin);
        
        String sql = "" +
                    " SELECT d.cuenta, a.descri, SUM(d.debe) AS DEBE, SUM(d.haber) AS haber " +
                    " FROM sf_tmpdet d " +
                    " LEFT JOIN sf_tmpenc e ON d.id_tmpenc = e.id_tmpenc " +
                    " LEFT JOIN arcgms a    ON d.cuenta = a.cuenta " +
                    " WHERE e.tipo_doc = 'CI' " +
                    " AND e.fecha BETWEEN '"+fechaInicio+"' AND '"+fechaFinal+"' " +
                    " AND e.estado <> 'ANL' " +
                    " AND e.idusuario = " + usuario.getIdusuario() +
                    " GROUP BY d.cuenta, a.descri ";
        try{
            datos = (List<Object[]>)em.createNativeQuery(sql)
                    .getResultList();
        }catch (NoResultException e){
            return resumenRecaudacionList;
        }
        
        System.out.println("------SIZE DATOS: " + datos.size());
        
        /*
        Double debe = 0.0;
        Double haber = 0.0;
        Double saldo = 0.0;
        Double totalDebe = 0.0;
        Double totalHaber = 0.0;
        Double totalSaldo = 0.0;
        */
        
        for(Object[] dato:datos)
        {
            ResumenRecaudacion resumenRecaudacion = new ResumenRecaudacion();
            resumenRecaudacion.setCuenta((String)dato[0]);
            resumenRecaudacion.setDescripcion((String)dato[1]);
            resumenRecaudacion.setTotalDebe(((BigDecimal)dato[2]).doubleValue());
            resumenRecaudacion.setTotalHaber(((BigDecimal)dato[3]).doubleValue());
            
            resumenRecaudacionList.add(resumenRecaudacion);
        }
        return resumenRecaudacionList;
        
    }

    public Collection<ResumenPago> getResumenPago(Pago pago) {
        List<ResumenPago> resumenPagoList = new ArrayList<>();
        List<Object[]> datos = new ArrayList<>();

        String sql = "" +
                " SELECT d.cuenta, a.descri, d.debe, d.haber " +
                " FROM sf_tmpdet d " +
                " LEFT JOIN sf_tmpenc e ON d.id_tmpenc  = e.id_tmpenc " +
                " LEFT JOIN arcgms a    ON d.cuenta     = a.cuenta " +
                " WHERE e.id_tmpenc = " + pago.getAsiento().getIdTmpenc().toString();
        try{
            datos = (List<Object[]>)em.createNativeQuery(sql)
                    .getResultList();
        }catch (NoResultException e){
            return resumenPagoList;
        }


        for(Object[] dato:datos)
        {
            ResumenPago resumenPago = new ResumenPago();
            resumenPago.setCuenta((String)dato[0]);
            resumenPago.setDescripcion((String)dato[1]);
            resumenPago.setDebe(((BigDecimal) dato[2]).doubleValue());
            resumenPago.setHaber(((BigDecimal) dato[3]).doubleValue());

            resumenPagoList.add(resumenPago);
        }
        return resumenPagoList;
    }
    
    public void anularAsiento(SfTmpenc asiento){

        if (asiento != null){
            asiento.setEstado("ANL");
            System.out.println("-----------> ANULAR VENTA ASIENTO 2: " + asiento.getTipoDoc() + " - " + asiento.getNoDoc());

            em.merge(asiento);
            em.flush();
        }

    }

    public void saveSFtmpenc(SfTmpenc sfTmpenc){
        em.persist(sfTmpenc);
        em.flush();
    }

    public void mergeVentaContado(Ventadirecta ventadirecta){
        em.merge(ventadirecta);
    }

    public void restarInventario(Ventadirecta ventadirecta){

        for ( ArticulosPedido articulo : ventadirecta.getArticulosPedidos() ){

            em.createNativeQuery(" UPDATE inv_inventario SET saldo_uni = saldo_uni - " + articulo.getCantidad() +
                                 " WHERE cod_art = " + articulo.getInvArticulos().getProductItemCode())
                                 .executeUpdate();

            em.createNativeQuery(" UPDATE inv_inventario_detalle id " +
                                 " JOIN inv_inventario i ON id.cod_art = i.cod_art " +
                                 " SET id.cantidad = i.saldo_uni " +
                                 " WHERE i.cod_art = " + articulo.getInvArticulos().getProductItemCode())
                                 .executeUpdate();
        }
    }

    public void restarInventario(Pedidos pedidos){

        for ( ArticulosPedido articulo : pedidos.getArticulosPedidos() ){

            em.createNativeQuery(" UPDATE inv_inventario SET saldo_uni = saldo_uni - " + articulo.getTotal() +
                    " WHERE cod_art = " + articulo.getInvArticulos().getProductItemCode())
                    .executeUpdate();

            em.createNativeQuery(" UPDATE inv_inventario_detalle id " +
                    " JOIN inv_inventario i ON id.cod_art = i.cod_art " +
                    " SET id.cantidad = i.saldo_uni " +
                    " WHERE i.cod_art = " + articulo.getInvArticulos().getProductItemCode())
                    .executeUpdate();
        }
    }

    public void sumarInventario(Ventadirecta ventadirecta){

        for ( ArticulosPedido articulo : ventadirecta.getArticulosPedidos() ){

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

    public Double getBalance(Date startDate, String cashAccountCode, Persona personaElegida){

        System.out.println("TOTAL BALANCE 1");

        List<SfTmpdet> voucherDetailList = new ArrayList<SfTmpdet>();
        List<Object[]> datos = new ArrayList<>();

        String start = DateUtils.format(startDate, "yyyy-MM-dd");
        System.out.println("..............patter yyyy-MM-dd: " + start);
        try {
            /** Consulta Error Datos incompletos por inconsistencia **/
            /*voucherDetailList = em.createQuery("select voucherDetail from SfTmpdet voucherDetail " +
                    " left join voucherDetail.sfTmpenc voucher " +
                    " where voucher.fecha < :startdate " +
                    " and voucher.estado <> 'ANL' " +
                    " and voucherDetail.cuenta = :cashAccountCode " +
                    " and voucher.cliente = :persona ")
                    .setParameter("startdate", startDate)
                    .setParameter("cashAccountCode", cashAccountCode)
                    .setParameter("persona", personaElegida)
                    .getResultList();*/

            String sql =
                    "SELECT enc.fecha, enc.tipo_doc, enc.no_doc, enc.glosa, det.debe, det.haber, enc.id_tmpenc " +
                            "FROM sf_tmpdet det " +
                            "JOIN sf_tmpenc enc ON enc.id_tmpenc = det.id_tmpenc " +
                            "WHERE det.idpersonacliente = '" + personaElegida.getPiId() + "' " +
                            "AND det.cuenta = '" + cashAccountCode + "' " +
                            "AND enc.estado <> 'ANL' " +
                            "AND enc.fecha < '" + start + "'";

            datos = (List<Object[]>)em.createNativeQuery(sql)
                    .getResultList();

        }catch (NoResultException e){
            return null;
        }

        Double balance = new Double("0.00");
        Double balanceD = new Double("0.00");
        Double balanceC = new Double("0.00");
        for(Object[] dato:datos){
            balanceD = balanceD + ((BigDecimal)dato[4]).doubleValue();
            balanceC = balanceC + ((BigDecimal)dato[5]).doubleValue();
        }
        balance = (balanceD - balanceC);
        /*
        Double balance = new Double("0.00");
        Double balanceD = new Double("0.00");
        Double balanceC = new Double("0.00");
        System.out.println("TOTAL BALANCE 3");
        for (SfTmpdet voucherDetail : voucherDetailList){
            balanceD = balanceD + voucherDetail.getDebe().doubleValue();
            balanceC = balanceC + voucherDetail.getHaber().doubleValue();
        }
        balance = (balanceD - balanceC);
        */
        return balance;
    }

}
