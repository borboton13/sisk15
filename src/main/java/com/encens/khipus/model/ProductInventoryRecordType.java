/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.model;

/**
 *
 * @author Ariel
 */
public enum ProductInventoryRecordType {

    PRODUCTION("INPUT", "ProductInventory.production"),
    HIGH("INPUT", "ProductInventory.high"),
    DEVOLUTION("INPUT", "ProductInventory.devolution"),
    LOW("OUTPUT", "ProductInventory.low"),
    DELIVERY_CUSTOMER_ORDER("OUTPUT", "ProductInventory.deliveryCustomerOrder"),
    DELIVERY_CASH_SALE("OUTPUT", "ProductInventory.deliveryCashSale");

    private String type;
    private String resourceKey;

    ProductInventoryRecordType(String type, String resourceKey) {
        this.setType(type);
        this.resourceKey = resourceKey;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    public void setResourceKey(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}