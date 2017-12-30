package com.encens.khipus.model;

import java.util.Date;

/**
 * Created by Diego on 23/07/2015.
 */
public class Recaudacion {
    private Date fecha;
    private String tipoDoc;
    private String noDoc;
    private String glosa;
    private Double ingreso;
    private Double egreso;
    private Double saldo;
    private Double totalDebe;
    private Double totalHaber;
    private Double totalSaldo;

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getTipoDoc() {
        return tipoDoc;
    }

    public void setTipoDoc(String tipoDoc) {
        this.tipoDoc = tipoDoc;
    }

    public String getNoDoc() {
        return noDoc;
    }

    public void setNoDoc(String noDoc) {
        this.noDoc = noDoc;
    }

    public String getGlosa() {
        return glosa;
    }

    public void setGlosa(String glosa) {
        this.glosa = glosa;
    }

    public Double getIngreso() {
        return ingreso;
    }

    public void setIngreso(Double ingreso) {
        this.ingreso = ingreso;
    }

    public Double getEgreso() {
        return egreso;
    }

    public void setEgreso(Double egreso) {
        this.egreso = egreso;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public Double getTotalDebe() {
        return totalDebe;
    }

    public void setTotalDebe(Double totalDebe) {
        this.totalDebe = totalDebe;
    }

    public Double getTotalHaber() {
        return totalHaber;
    }

    public void setTotalHaber(Double totalHaber) {
        this.totalHaber = totalHaber;
    }

    public Double getTotalSaldo() {
        return totalSaldo;
    }

    public void setTotalSaldo(Double totalSaldo) {
        this.totalSaldo = totalSaldo;
    }
}
