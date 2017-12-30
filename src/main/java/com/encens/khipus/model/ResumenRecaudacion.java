package com.encens.khipus.model;

/**
 * Created by Diego on 23/07/2015.
 */
public class ResumenRecaudacion {
    
    private String cuenta;
    private String descripcion;
    private Double totalDebe;
    private Double totalHaber;
    

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

    /**
     * @return the cuenta
     */
    public String getCuenta() {
        return cuenta;
    }

    /**
     * @param cuenta the cuenta to set
     */
    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

   
}
