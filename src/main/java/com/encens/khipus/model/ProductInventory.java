/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.model;


import java.io.Serializable;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collection;

/**
 *
 * @author Ariel
 */
@TableGenerator(name = "ProductInventory.tableGenerator",
        table = "secuencia",
        pkColumnName = "tabla",
        valueColumnName = "valor",
        pkColumnValue = "inv_product",
        allocationSize = 1)


@NamedQueries({
        @NamedQuery(name = "ProductInventory.findByCode",
                query = "select productInventory from ProductInventory productInventory where productInventory.productItemCode =:productItemCode")
})


@Entity
@Table(name = "inv_product")
public class ProductInventory implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "ProductInventory.tableGenerator")
    @Column(name = "ID_INV_PROD", nullable = false)
    private Long id;

    @Column(name = "NO_CIA", length = 2, nullable = false)
    private String companyNumber;

    @Column(name = "COD_ALM", length = 6, nullable = false)
    private String warehouseCode;

    @Column(name = "COD_ART", length = 6, nullable = false)
    private String productItemCode;

    @Column(name = "COD_CC", length = 8, nullable = false)
    private String costCenterCode;

    /*
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IDUNIDADNEGOCIO", updatable = true, insertable = true)
    private BusinessUnit executorUnit;
    */

    @Column(name = "CANTIDAD", precision = 12, scale = 2)
    private BigDecimal quantity;

    @Version
    @Column(name = "VERSION")
    private long version;

    @Transient
    private String productItemFullName;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "NO_CIA", referencedColumnName = "NO_CIA", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "COD_ART", referencedColumnName = "COD_ART", nullable = false, insertable = false, updatable = false)
    })
    private InvArticulos productItem;

    /*
    @JoinColumns({
        @JoinColumn(name = "NO_CIA", referencedColumnName = "NO_CIA", insertable = false, updatable = false),
        @JoinColumn(name = "COD_GRU", referencedColumnName = "COD_GRU")})
    @ManyToOne(optional = false)
    private InvGrupos invGrupos;
    */
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "productInventory")
    private Collection<ProductInventoryRecord> productInventoryRecords;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getProductItemCode() {
        return productItemCode;
    }

    public void setProductItemCode(String productItemCode) {
        this.productItemCode = productItemCode;
    }

    public String getCostCenterCode() {
        return costCenterCode;
    }

    public void setCostCenterCode(String costCenterCode) {
        this.costCenterCode = costCenterCode;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public InvArticulos getProductItem() {
        return productItem;
    }

    public void setProductItem(InvArticulos productItem) {
        this.productItem = productItem;
        if (null != productItem) {
            setProductItemCode(productItem.getInvArticulosPK().getCodArt());
        } else {
            setProductItemCode(null);
        }
    }

    public Collection<ProductInventoryRecord> getProductInventoryRecords() {
        return productInventoryRecords;
    }

    public void setProductInventoryRecords(Collection<ProductInventoryRecord> productInventoryRecords) {
        this.productInventoryRecords = productInventoryRecords;
    }

    public String getProductItemFullName() {
        if (productItemFullName == null && getProductItem() != null) {
            productItemFullName = getProductItem().getFullName();
        }
        return productItemFullName;
    }

    public void setProductItemFullName(String productItemFullName) {
        this.productItemFullName = productItemFullName;
    }
}
