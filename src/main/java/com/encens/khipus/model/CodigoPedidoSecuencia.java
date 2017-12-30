package com.encens.khipus.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by Diego on 23/03/2015.
 */
@Entity
@Table(name = "codigopedidosecuencia")
public class CodigoPedidoSecuencia implements Serializable {
    @Id
    @NotNull
    @TableGenerator(name = "VentaCodigo_Gen"
            ,table="ID_GEN"
            ,pkColumnName = "GEN_NAME"
            ,valueColumnName = "GEN_VAL"
            ,initialValue = 1
            ,allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "VentaCodigo_Gen")
    @Column(name = "secuencia")
    private Long secuencia;

    public Long getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(Long secuencia) {
        this.secuencia = secuencia;
    }
}
