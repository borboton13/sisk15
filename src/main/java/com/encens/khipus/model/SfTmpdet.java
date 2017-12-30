/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.model;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "sf_tmpdet")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SfTmpdet.findAll", query = "SELECT s FROM SfTmpdet s"),
    @NamedQuery(name = "SfTmpdet.findByTimemillis", query = "SELECT s FROM SfTmpdet s WHERE s.timemillis = :timemillis"),
    @NamedQuery(name = "SfTmpdet.findByCodUni", query = "SELECT s FROM SfTmpdet s WHERE s.codUni = :codUni"),
    @NamedQuery(name = "SfTmpdet.findByCodCc", query = "SELECT s FROM SfTmpdet s WHERE s.codCc = :codCc"),
    @NamedQuery(name = "SfTmpdet.findByHaber", query = "SELECT s FROM SfTmpdet s WHERE s.haber = :haber"),
    @NamedQuery(name = "SfTmpdet.findByMoneda", query = "SELECT s FROM SfTmpdet s WHERE s.moneda = :moneda"),
    @NamedQuery(name = "SfTmpdet.findByDebe", query = "SELECT s FROM SfTmpdet s WHERE s.debe = :debe"),
    @NamedQuery(name = "SfTmpdet.findByTc", query = "SELECT s FROM SfTmpdet s WHERE s.tc = :tc")})
public class SfTmpdet implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @NotNull
    @TableGenerator(name = "sf_tmpdet"
            ,table="secuencia"
            ,pkColumnName = "tabla"
            ,valueColumnName = "valor"
            ,initialValue = 1
            ,allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "sf_tmpdet")
    @Column(name = "id_tmpdet")
    private Long idTmpdet;
    @Size(max = 255)
    @Column(name = "timemillis")
    private String timemillis;
    @Size(max = 255)
    @Column(name = "cod_uni")
    private String codUni;
    @Size(max = 255)
    @Column(name = "cod_cc")
    private String codCc;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "haber")
    private BigDecimal haber;
    @Size(max = 255)
    @Column(name = "moneda")
    private String moneda;
    @Column(name = "debe")
    private BigDecimal debe;
    @Column(name = "tc")
    private BigDecimal tc;
    @Basic(optional = false)
    @Size(min = 1, max = 2)
    @Column(name = "no_cia")
    private String noCia;
    @Basic(optional = false)
    @Size(min = 1, max = 10)
    @Column(name = "no_trans")
    private String noTrans;
    @Basic(optional = false)
    @Size(min = 1, max = 31)
    @Column(name = "cuenta")
    private String cuenta;

    @JoinColumn(name = "id_tmpenc",referencedColumnName = "id_tmpenc")
    @ManyToOne(optional = false)
    private SfTmpenc sfTmpenc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDPERSONACLIENTE", referencedColumnName = "IDPERSONACLIENTE")
    private Persona client;

    public SfTmpdet() {
    }

    public String getTimemillis() {
        return timemillis;
    }

    public void setTimemillis(String timemillis) {
        this.timemillis = timemillis;
    }

    public String getCodUni() {
        return codUni;
    }

    public void setCodUni(String codUni) {
        this.codUni = codUni;
    }

    public String getCodCc() {
        return codCc;
    }

    public void setCodCc(String codCc) {
        this.codCc = codCc;
    }

    public BigDecimal getHaber() {
        return haber;
    }

    public void setHaber(BigDecimal haber) {
        this.haber = haber;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public BigDecimal getDebe() {
        return debe;
    }

    public void setDebe(BigDecimal debe) {
        this.debe = debe;
    }

    public BigDecimal getTc() {
        return tc;
    }

    public void setTc(BigDecimal tc) {
        this.tc = tc;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idTmpdet != null ? idTmpdet.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SfTmpdet)) {
            return false;
        }
        SfTmpdet other = (SfTmpdet) object;
        if ((this.idTmpdet == null && other.idTmpdet != null) || (this.idTmpdet != null && !this.idTmpdet.equals(other.idTmpdet))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.encens.khipus.model.SfTmpdet[ sfTmpdetPK=" + idTmpdet + " ]";
    }

    public Long getIdTmpdet() {
        return idTmpdet;
    }

    public void setIdTmpdet(Long idTmpdet) {
        this.idTmpdet = idTmpdet;
    }

    public String getNoCia() {
        return noCia;
    }

    public void setNoCia(String noCia) {
        this.noCia = noCia;
    }

    public String getNoTrans() {
        return noTrans;
    }

    public void setNoTrans(String noTrans) {
        this.noTrans = noTrans;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public SfTmpenc getSfTmpenc() {
        return sfTmpenc;
    }

    public void setSfTmpenc(SfTmpenc sfTmpenc) {
        this.sfTmpenc = sfTmpenc;
    }

    public Persona getClient() {
        return client;
    }

    public void setClient(Persona client) {
        this.client = client;
    }
}
