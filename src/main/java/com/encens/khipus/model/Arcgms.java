package com.encens.khipus.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

/**
 * Created by Diego on 23/06/2015.
 */
@Entity
public class Arcgms implements Serializable{


    @Id
    @NotNull
    @TableGenerator(name = "Arcgms_Gen"
            ,table="ID_GEN"
            ,pkColumnName = "GEN_NAME"
            ,valueColumnName = "GEN_VAL"
            ,initialValue = 1
            ,allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Arcgms_Gen")
    @Column(name = "cuenta")
    private String cuenta;
    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    @Basic
    @javax.persistence.Column(name = "descri", nullable = true, insertable = true, updatable = true, length = 100)
    private String descri;
    public String getDescri() {
        return descri;
    }

    public void setDescri(String descri) {
        this.descri = descri;
    }

    @Basic
    @javax.persistence.Column(name = "cn_ana", nullable = true, insertable = true, updatable = true, length = 1)
    private String cnAna;
    public String getCnAna() {
        return cnAna;
    }

    public void setCnAna(String cnAna) {
        this.cnAna = cnAna;
    }

    @Basic
    @javax.persistence.Column(name = "cn_nivel", nullable = true, insertable = true, updatable = true)
    private Integer cnNivel;
    public Integer getCnNivel() {
        return cnNivel;
    }

    public void setCnNivel(Integer cnNivel) {
        this.cnNivel = cnNivel;
    }

    @Basic
    @javax.persistence.Column(name = "cn_dv", nullable = true, insertable = true, updatable = true)
    private Integer cnDv;
    public Integer getCnDv() {
        return cnDv;
    }

    public void setCnDv(Integer cnDv) {
        this.cnDv = cnDv;
    }

    @Basic
    @javax.persistence.Column(name = "cn_tip", nullable = true, insertable = true, updatable = true, length = 1)
    private String cnTip;
    public String getCnTip() {
        return cnTip;
    }

    public void setCnTip(String cnTip) {
        this.cnTip = cnTip;
    }


    @Basic
    @javax.persistence.Column(name = "cn_act", nullable = true, insertable = true, updatable = true)
    private Integer cnAct;
    public Integer getCnAct() {
        return cnAct;
    }

    public void setCnAct(Integer cnAct) {
        this.cnAct = cnAct;
    }

    @Basic
    @javax.persistence.Column(name = "no_cia", nullable = true, insertable = true, updatable = true, length = 2)
    private String noCia;
    public String getNoCia() {
        return noCia;
    }

    public void setNoCia(String noCia) {
        this.noCia = noCia;
    }

    @Basic
    @javax.persistence.Column(name = "clase", nullable = true, insertable = true, updatable = true, length = 1)
    private String clase;
    public String getClase() {
        return clase;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }

    @Basic
    @javax.persistence.Column(name = "tipo", nullable = true, insertable = true, updatable = true, length = 2)
    private String tipo;
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Basic
    @javax.persistence.Column(name = "activa", nullable = true, insertable = true, updatable = true, length = 255)
    private String activa;
    public String getActiva() {
        return activa;
    }

    public void setActiva(String activa) {
        this.activa = activa;
    }

    @Basic
    @javax.persistence.Column(name = "permite_iva", nullable = true, insertable = true, updatable = true, length = 255)
    private String permiteIva;
    public String getPermiteIva() {
        return permiteIva;
    }

    public void setPermiteIva(String permiteIva) {
        this.permiteIva = permiteIva;
    }

    @Basic
    @javax.persistence.Column(name = "ind_presup", nullable = true, insertable = true, updatable = true, length = 255)
    private String indPresup;
    public String getIndPresup() {
        return indPresup;
    }

    public void setIndPresup(String indPresup) {
        this.indPresup = indPresup;
    }

    @Basic
    @javax.persistence.Column(name = "creditos", nullable = true, insertable = true, updatable = true, precision = 2)
    private BigDecimal creditos;
    public BigDecimal getCreditos() {
        return creditos;
    }

    public void setCreditos(BigDecimal creditos) {
        this.creditos = creditos;
    }

    @Basic
    @javax.persistence.Column(name = "moneda", nullable = true, insertable = true, updatable = true, length = 255)
    private String moneda;
    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    @Basic
    @javax.persistence.Column(name = "debitos", nullable = true, insertable = true, updatable = true, precision = 2)
    private BigDecimal debitos;
    public BigDecimal getDebitos() {
        return debitos;
    }

    public void setDebitos(BigDecimal debitos) {
        this.debitos = debitos;
    }

    @Basic
    @javax.persistence.Column(name = "saldo_mes_ant_dol", nullable = true, insertable = true, updatable = true, precision = 6)
    private BigDecimal saldoMesAntDol;
    public BigDecimal getSaldoMesAntDol() {
        return saldoMesAntDol;
    }

    public void setSaldoMesAntDol(BigDecimal saldoMesAntDol) {
        this.saldoMesAntDol = saldoMesAntDol;
    }


    @Basic
    @javax.persistence.Column(name = "saldo_per_ant_dol", nullable = true, insertable = true, updatable = true, precision = 6)
    private BigDecimal saldoPerAntDol;
    public BigDecimal getSaldoPerAntDol() {
        return saldoPerAntDol;
    }

    public void setSaldoPerAntDol(BigDecimal saldoPerAntDol) {
        this.saldoPerAntDol = saldoPerAntDol;
    }

    @Basic
    @javax.persistence.Column(name = "creditos_dol", nullable = true, insertable = true, updatable = true, precision = 6)
    private BigDecimal creditosDol;
    public BigDecimal getCreditosDol() {
        return creditosDol;
    }

    public void setCreditosDol(BigDecimal creditosDol) {
        this.creditosDol = creditosDol;
    }

    @Basic
    @javax.persistence.Column(name = "debitos_dol", nullable = true, insertable = true, updatable = true, precision = 6)
    private BigDecimal debitosDol;
    public BigDecimal getDebitosDol() {
        return debitosDol;
    }

    public void setDebitosDol(BigDecimal debitosDol) {
        this.debitosDol = debitosDol;
    }

    @Basic
    @javax.persistence.Column(name = "gru_cta", nullable = true, insertable = true, updatable = true, length = 6)
    private String gruCta;
    public String getGruCta() {
        return gruCta;
    }

    public void setGruCta(String gruCta) {
        this.gruCta = gruCta;
    }

    @Basic
    @javax.persistence.Column(name = "permiso_con", nullable = true, insertable = true, updatable = true, length = 255)
    private String permisoCon;
    public String getPermisoCon() {
        return permisoCon;
    }

    public void setPermisoCon(String permisoCon) {
        this.permisoCon = permisoCon;
    }

    @Basic
    @javax.persistence.Column(name = "exije_cc", nullable = true, insertable = true, updatable = true, length = 255)
    private String exijeCc;
    public String getExijeCc() {
        return exijeCc;
    }

    public void setExijeCc(String exijeCc) {
        this.exijeCc = exijeCc;
    }

    @Basic
    @javax.persistence.Column(name = "permiso_afijo", nullable = true, insertable = true, updatable = true, length = 255)
    private String permisoAfijo;
    public String getPermisoAfijo() {
        return permisoAfijo;
    }

    public void setPermisoAfijo(String permisoAfijo) {
        this.permisoAfijo = permisoAfijo;
    }

    @Basic
    @javax.persistence.Column(name = "permiso_cxp", nullable = true, insertable = true, updatable = true, length = 255)
    private String permisoCxp;
    public String getPermisoCxp() {
        return permisoCxp;
    }

    public void setPermisoCxp(String permisoCxp) {
        this.permisoCxp = permisoCxp;
    }

    @Basic
    @javax.persistence.Column(name = "permiso_cxc", nullable = true, insertable = true, updatable = true, length = 255)
    private String permisoCxc;
    public String getPermisoCxc() {
        return permisoCxc;
    }

    public void setPermisoCxc(String permisoCxc) {
        this.permisoCxc = permisoCxc;
    }

    @Basic
    @javax.persistence.Column(name = "permiso_che", nullable = true, insertable = true, updatable = true, length = 255)
    private String permisoChe;
    public String getPermisoChe() {
        return permisoChe;
    }

    public void setPermisoChe(String permisoChe) {
        this.permisoChe = permisoChe;
    }

    @Basic
    @javax.persistence.Column(name = "permiso_inv", nullable = true, insertable = true, updatable = true, length = 255)
    private String permisoInv;
    public String getPermisoInv() {
        return permisoInv;
    }

    public void setPermisoInv(String permisoInv) {
        this.permisoInv = permisoInv;
    }

    @Basic
    @javax.persistence.Column(name = "f_inactiva", nullable = true, insertable = true, updatable = true)
    private Date fInactiva;
    public Date getfInactiva() {
        return fInactiva;
    }

    public void setfInactiva(Date fInactiva) {
        this.fInactiva = fInactiva;
    }

    @Basic
    @javax.persistence.Column(name = "ind_mov", nullable = true, insertable = true, updatable = true, length = 255)
    private String indMov;
    public String getIndMov() {
        return indMov;
    }

    public void setIndMov(String indMov) {
        this.indMov = indMov;
    }

    @Basic
    @javax.persistence.Column(name = "saldo_mes_ant", nullable = true, insertable = true, updatable = true, precision = 2)
    private BigDecimal saldoMesAnt;
    public BigDecimal getSaldoMesAnt() {
        return saldoMesAnt;
    }

    public void setSaldoMesAnt(BigDecimal saldoMesAnt) {
        this.saldoMesAnt = saldoMesAnt;
    }

    @Basic
    @javax.persistence.Column(name = "saldo_per_ant", nullable = true, insertable = true, updatable = true, precision = 2)
    private BigDecimal saldoPerAnt;
    public BigDecimal getSaldoPerAnt() {
        return saldoPerAnt;
    }

    public void setSaldoPerAnt(BigDecimal saldoPerAnt) {
        this.saldoPerAnt = saldoPerAnt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Arcgms arcgms = (Arcgms) o;

        if (activa != null ? !activa.equals(arcgms.activa) : arcgms.activa != null) return false;
        if (clase != null ? !clase.equals(arcgms.clase) : arcgms.clase != null) return false;
        if (cnAct != null ? !cnAct.equals(arcgms.cnAct) : arcgms.cnAct != null) return false;
        if (cnAna != null ? !cnAna.equals(arcgms.cnAna) : arcgms.cnAna != null) return false;
        if (cnDv != null ? !cnDv.equals(arcgms.cnDv) : arcgms.cnDv != null) return false;
        if (cnNivel != null ? !cnNivel.equals(arcgms.cnNivel) : arcgms.cnNivel != null) return false;
        if (cnTip != null ? !cnTip.equals(arcgms.cnTip) : arcgms.cnTip != null) return false;
        if (creditos != null ? !creditos.equals(arcgms.creditos) : arcgms.creditos != null) return false;
        if (creditosDol != null ? !creditosDol.equals(arcgms.creditosDol) : arcgms.creditosDol != null) return false;
        if (cuenta != null ? !cuenta.equals(arcgms.cuenta) : arcgms.cuenta != null) return false;
        if (debitos != null ? !debitos.equals(arcgms.debitos) : arcgms.debitos != null) return false;
        if (debitosDol != null ? !debitosDol.equals(arcgms.debitosDol) : arcgms.debitosDol != null) return false;
        if (descri != null ? !descri.equals(arcgms.descri) : arcgms.descri != null) return false;
        if (exijeCc != null ? !exijeCc.equals(arcgms.exijeCc) : arcgms.exijeCc != null) return false;
        if (fInactiva != null ? !fInactiva.equals(arcgms.fInactiva) : arcgms.fInactiva != null) return false;
        if (gruCta != null ? !gruCta.equals(arcgms.gruCta) : arcgms.gruCta != null) return false;
        if (indMov != null ? !indMov.equals(arcgms.indMov) : arcgms.indMov != null) return false;
        if (indPresup != null ? !indPresup.equals(arcgms.indPresup) : arcgms.indPresup != null) return false;
        if (moneda != null ? !moneda.equals(arcgms.moneda) : arcgms.moneda != null) return false;
        if (noCia != null ? !noCia.equals(arcgms.noCia) : arcgms.noCia != null) return false;
        if (permisoAfijo != null ? !permisoAfijo.equals(arcgms.permisoAfijo) : arcgms.permisoAfijo != null)
            return false;
        if (permisoChe != null ? !permisoChe.equals(arcgms.permisoChe) : arcgms.permisoChe != null) return false;
        if (permisoCon != null ? !permisoCon.equals(arcgms.permisoCon) : arcgms.permisoCon != null) return false;
        if (permisoCxc != null ? !permisoCxc.equals(arcgms.permisoCxc) : arcgms.permisoCxc != null) return false;
        if (permisoCxp != null ? !permisoCxp.equals(arcgms.permisoCxp) : arcgms.permisoCxp != null) return false;
        if (permisoInv != null ? !permisoInv.equals(arcgms.permisoInv) : arcgms.permisoInv != null) return false;
        if (permiteIva != null ? !permiteIva.equals(arcgms.permiteIva) : arcgms.permiteIva != null) return false;
        if (saldoMesAnt != null ? !saldoMesAnt.equals(arcgms.saldoMesAnt) : arcgms.saldoMesAnt != null) return false;
        if (saldoMesAntDol != null ? !saldoMesAntDol.equals(arcgms.saldoMesAntDol) : arcgms.saldoMesAntDol != null)
            return false;
        if (saldoPerAnt != null ? !saldoPerAnt.equals(arcgms.saldoPerAnt) : arcgms.saldoPerAnt != null) return false;
        if (saldoPerAntDol != null ? !saldoPerAntDol.equals(arcgms.saldoPerAntDol) : arcgms.saldoPerAntDol != null)
            return false;
        if (tipo != null ? !tipo.equals(arcgms.tipo) : arcgms.tipo != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = cuenta != null ? cuenta.hashCode() : 0;
        result = 31 * result + (descri != null ? descri.hashCode() : 0);
        result = 31 * result + (cnAna != null ? cnAna.hashCode() : 0);
        result = 31 * result + (cnNivel != null ? cnNivel.hashCode() : 0);
        result = 31 * result + (cnDv != null ? cnDv.hashCode() : 0);
        result = 31 * result + (cnTip != null ? cnTip.hashCode() : 0);
        result = 31 * result + (cnAct != null ? cnAct.hashCode() : 0);
        result = 31 * result + (noCia != null ? noCia.hashCode() : 0);
        result = 31 * result + (clase != null ? clase.hashCode() : 0);
        result = 31 * result + (tipo != null ? tipo.hashCode() : 0);
        result = 31 * result + (activa != null ? activa.hashCode() : 0);
        result = 31 * result + (permiteIva != null ? permiteIva.hashCode() : 0);
        result = 31 * result + (indPresup != null ? indPresup.hashCode() : 0);
        result = 31 * result + (creditos != null ? creditos.hashCode() : 0);
        result = 31 * result + (moneda != null ? moneda.hashCode() : 0);
        result = 31 * result + (debitos != null ? debitos.hashCode() : 0);
        result = 31 * result + (saldoMesAntDol != null ? saldoMesAntDol.hashCode() : 0);
        result = 31 * result + (saldoPerAntDol != null ? saldoPerAntDol.hashCode() : 0);
        result = 31 * result + (creditosDol != null ? creditosDol.hashCode() : 0);
        result = 31 * result + (debitosDol != null ? debitosDol.hashCode() : 0);
        result = 31 * result + (gruCta != null ? gruCta.hashCode() : 0);
        result = 31 * result + (permisoCon != null ? permisoCon.hashCode() : 0);
        result = 31 * result + (exijeCc != null ? exijeCc.hashCode() : 0);
        result = 31 * result + (permisoAfijo != null ? permisoAfijo.hashCode() : 0);
        result = 31 * result + (permisoCxp != null ? permisoCxp.hashCode() : 0);
        result = 31 * result + (permisoCxc != null ? permisoCxc.hashCode() : 0);
        result = 31 * result + (permisoChe != null ? permisoChe.hashCode() : 0);
        result = 31 * result + (permisoInv != null ? permisoInv.hashCode() : 0);
        result = 31 * result + (fInactiva != null ? fInactiva.hashCode() : 0);
        result = 31 * result + (indMov != null ? indMov.hashCode() : 0);
        result = 31 * result + (saldoMesAnt != null ? saldoMesAnt.hashCode() : 0);
        result = 31 * result + (saldoPerAnt != null ? saldoPerAnt.hashCode() : 0);
        return result;
    }
}
