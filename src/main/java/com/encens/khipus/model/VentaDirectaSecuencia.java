package com.encens.khipus.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by Diego on 23/03/2015.
 */
@Entity
@Table(name = "ventadirectasecuencia")
public class VentaDirectaSecuencia implements Serializable {
    @Id
    @NotNull
    @TableGenerator(name = "VentaDirecta_Gen"
            ,table="ID_GEN"
            ,pkColumnName = "GEN_NAME"
            ,valueColumnName = "GEN_VAL"
            ,initialValue = 1
            ,allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "VentaDirecta_Gen")
    @Column(name = "secuencia")
    private Long secuencia;

    public Long getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(Long secuencia) {
        this.secuencia = secuencia;
    }
}
