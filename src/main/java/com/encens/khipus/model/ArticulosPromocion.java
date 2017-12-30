package com.encens.khipus.model;


import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Diego on 19/04/2015.
 */
@Entity
@Table(name = "articulospromocion")
public class ArticulosPromocion implements Serializable {
    @Id
    @TableGenerator(name = "ArticulosPromocion_Gen"
            ,table="ID_GEN"
            ,pkColumnName = "GEN_NAME"
            ,valueColumnName = "GEN_VAL"
            ,initialValue = 1
            ,allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "ArticulosPromocion_Gen")
    @Column(name = "idarticulospromocion")
    private Long id;
    @Column(name = "CANTIDAD")
    private Integer cantidad = 0;
    @Column(name = "PRECIOTOTAL")
    private Double precioTotal = 0.0;
    @JoinColumn(name="IDPROMOCION",referencedColumnName = "IDPROMOCION")
    @ManyToOne(optional = false)
    private Promocion promocion;
    @JoinColumn(name="IDVENTAARTICULO",referencedColumnName = "IDVENTAARTICULO")
    @ManyToOne(optional = false)
    private Ventaarticulo ventaarticulo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Promocion getPromocion() {
        return promocion;
    }

    public void setPromocion(Promocion promocion) {
        this.promocion = promocion;
    }

    public Ventaarticulo getVentaarticulo() {
        return ventaarticulo;
    }

    public void setVentaarticulo(Ventaarticulo ventaarticulo) {
        this.ventaarticulo = ventaarticulo;
    }

    public Double getPrecioTotal() {
        //precioTotal = cantidad * ventaarticulo.getPrecio();
        return precioTotal;
    }

    public void setPrecioTotal(Double precioTotal) {
        this.precioTotal = precioTotal;
    }
}
