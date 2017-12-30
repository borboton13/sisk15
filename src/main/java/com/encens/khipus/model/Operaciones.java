package com.encens.khipus.model;

import javax.persistence.*;

/**
 * Created by Diego on 02/08/2015.
 */
@Entity
public class Operaciones extends Persistence{
    @Id
    @Column(name = "IDOPERACIONES", nullable = false, insertable = true, updatable = true)
    private int idoperaciones;
    @Basic
    @Column(name = "descripcion", nullable = true, insertable = true, updatable = true, length = 100)
    private String descripcion;
    @Basic
    @Column(name = "NOMBRE", nullable = true, insertable = true, updatable = true, length = 30)
    private String nombre;

    public int getIdoperaciones() {
        return idoperaciones;
    }

    public void setIdoperaciones(int idoperaciones) {
        this.idoperaciones = idoperaciones;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Operaciones that = (Operaciones) o;

        if (idoperaciones != that.idoperaciones) return false;
        if (descripcion != null ? !descripcion.equals(that.descripcion) : that.descripcion != null) return false;
        if (nombre != null ? !nombre.equals(that.nombre) : that.nombre != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idoperaciones;
        result = 31 * result + (descripcion != null ? descripcion.hashCode() : 0);
        result = 31 * result + (nombre != null ? nombre.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
