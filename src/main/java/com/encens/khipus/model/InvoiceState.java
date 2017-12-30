package com.encens.khipus.model;

/**
 * Created by Ariel on 04/02/2016.
 */
public enum InvoiceState {

    ANNULLED("A", "ANULADA"),
    ACTIVE("V", "VIGENTE"),
    LOST("E", "EXTRAVIADA");

    private String type;
    private String resourceKey;

    InvoiceState(String type, String resourceKey){
        this.type = type;
        this.resourceKey = resourceKey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
