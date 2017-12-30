/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.model;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Collection;

/**
 *
 * @author
 */

@NamedQueries({
        @NamedQuery(name = "CompanyConfiguration.findByCompany", query = "select c from CompanyConfiguration c")
})

@Entity
@Table(name = "configuracion")
public class CompanyConfiguration implements Serializable {

    @Id
    @Column(name = "NO_CIA", nullable = false, updatable = false)
    private String companyNumber;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumns({
            @JoinColumn(name = "NO_CIA", referencedColumnName = "NO_CIA", nullable = false, updatable = false, insertable = false),
            @JoinColumn(name = "IUE_RET", referencedColumnName = "CUENTA", nullable = false, updatable = false, insertable = false)
    })
    private CashAccount iueRetention;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumns({
            @JoinColumn(name = "NO_CIA", referencedColumnName = "NO_CIA", nullable = false, updatable = false, insertable = false),
            @JoinColumn(name = "IT_RET", referencedColumnName = "CUENTA", nullable = false, updatable = false, insertable = false)
    })
    private CashAccount itRetention;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumns({
            @JoinColumn(name = "NO_CIA", referencedColumnName = "NO_CIA", nullable = false, updatable = false, insertable = false),
            @JoinColumn(name = "CTACOSTPT", referencedColumnName = "CUENTA", nullable = false, updatable = false, insertable = false)
    })
    private CashAccount ctaCostPT;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumns({
            @JoinColumn(name = "NO_CIA", referencedColumnName = "NO_CIA", nullable = false, updatable = false, insertable = false),
            @JoinColumn(name = "CTAALMPT", referencedColumnName = "CUENTA", nullable = false, updatable = false, insertable = false)
    })
    private CashAccount ctaAlmPT;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumns({
            @JoinColumn(name = "NO_CIA", referencedColumnName = "NO_CIA", nullable = false, updatable = false, insertable = false),
            @JoinColumn(name = "CTACOSTPV", referencedColumnName = "CUENTA", nullable = false, updatable = false, insertable = false)
    })
    private CashAccount ctaCostPV;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumns({
            @JoinColumn(name = "NO_CIA", referencedColumnName = "NO_CIA", nullable = false, updatable = false, insertable = false),
            @JoinColumn(name = "CTAALMPV", referencedColumnName = "CUENTA", nullable = false, updatable = false, insertable = false)
    })
    private CashAccount ctaAlmPV;


    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public CashAccount getIueRetention() {
        return iueRetention;
    }

    public void setIueRetention(CashAccount iueRetention) {
        this.iueRetention = iueRetention;
    }

    public CashAccount getItRetention() {
        return itRetention;
    }

    public void setItRetention(CashAccount itRetention) {
        this.itRetention = itRetention;
    }

    public CashAccount getCtaCostPT() {
        return ctaCostPT;
    }

    public void setCtaCostPT(CashAccount ctaCostPT) {
        this.ctaCostPT = ctaCostPT;
    }

    public CashAccount getCtaAlmPT() {
        return ctaAlmPT;
    }

    public void setCtaAlmPT(CashAccount ctaAlmPT) {
        this.ctaAlmPT = ctaAlmPT;
    }

    public CashAccount getCtaCostPV() {
        return ctaCostPV;
    }

    public void setCtaCostPV(CashAccount ctaCostPV) {
        this.ctaCostPV = ctaCostPV;
    }

    public CashAccount getCtaAlmPV() {
        return ctaAlmPV;
    }

    public void setCtaAlmPV(CashAccount ctaAlmPV) {
        this.ctaAlmPV = ctaAlmPV;
    }
}
