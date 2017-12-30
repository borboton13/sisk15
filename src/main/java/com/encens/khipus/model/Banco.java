package com.encens.khipus.model;

import javax.persistence.*;

/**
 * Created by Diego on 03/08/2015.
 */
@Entity
@Table(name = "ck_ctas_bco", schema = "", catalog = "khipus")
public class Banco {
    private String ctaBco;
    private String noCia;
    private String cuenta;
    private String codBco;
    private String moneda;
    private String descri;
    private String estado;

    @Basic
    @Column(name = "cta_bco", nullable = true, insertable = true, updatable = true, length = 20)
    public String getCtaBco() {
        return ctaBco;
    }

    public void setCtaBco(String ctaBco) {
        this.ctaBco = ctaBco;
    }

    @Basic
    @Column(name = "no_cia", nullable = true, insertable = true, updatable = true, length = 255)
    public String getNoCia() {
        return noCia;
    }

    public void setNoCia(String noCia) {
        this.noCia = noCia;
    }

    @Id
    @Column(name = "cuenta", nullable = false, insertable = true, updatable = true, length = 31)
    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    @Basic
    @Column(name = "cod_bco", nullable = true, insertable = true, updatable = true, length = 6)
    public String getCodBco() {
        return codBco;
    }

    public void setCodBco(String codBco) {
        this.codBco = codBco;
    }

    @Basic
    @Column(name = "moneda", nullable = true, insertable = true, updatable = true, length = 255)
    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    @Basic
    @Column(name = "descri", nullable = true, insertable = true, updatable = true, length = 100)
    public String getDescri() {
        return descri;
    }

    public void setDescri(String descri) {
        this.descri = descri;
    }

    @Basic
    @Column(name = "estado", nullable = true, insertable = true, updatable = true, length = 3)
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Banco ckCtasBco = (Banco) o;

        if (codBco != null ? !codBco.equals(ckCtasBco.codBco) : ckCtasBco.codBco != null) return false;
        if (ctaBco != null ? !ctaBco.equals(ckCtasBco.ctaBco) : ckCtasBco.ctaBco != null) return false;
        if (cuenta != null ? !cuenta.equals(ckCtasBco.cuenta) : ckCtasBco.cuenta != null) return false;
        if (descri != null ? !descri.equals(ckCtasBco.descri) : ckCtasBco.descri != null) return false;
        if (estado != null ? !estado.equals(ckCtasBco.estado) : ckCtasBco.estado != null) return false;
        if (moneda != null ? !moneda.equals(ckCtasBco.moneda) : ckCtasBco.moneda != null) return false;
        if (noCia != null ? !noCia.equals(ckCtasBco.noCia) : ckCtasBco.noCia != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ctaBco != null ? ctaBco.hashCode() : 0;
        result = 31 * result + (noCia != null ? noCia.hashCode() : 0);
        result = 31 * result + (cuenta != null ? cuenta.hashCode() : 0);
        result = 31 * result + (codBco != null ? codBco.hashCode() : 0);
        result = 31 * result + (moneda != null ? moneda.hashCode() : 0);
        result = 31 * result + (descri != null ? descri.hashCode() : 0);
        result = 31 * result + (estado != null ? estado.hashCode() : 0);
        return result;
    }
    
    @Override
    public String toString() {
        return descri;
    }
}
