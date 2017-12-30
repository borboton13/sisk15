package com.encens.khipus.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by Diego on 01/08/2015.
 */
@Entity
public class Tipodoc {
    @Id
    @Column(name = "IDTIPODOC", nullable = false, insertable = true, updatable = true)
    private long idtipodoc;
    @Basic
    @Column(name = "NOMBRE", nullable = true, insertable = true, updatable = true, length = 5)
    private String nombre;
    @Basic
    @Column(name = "DESCRIPCION", nullable = true, insertable = true, updatable = true, length = 512)
    private String descripcion;

    public long getIdtipodoc() {
        return idtipodoc;
    }

    public void setIdtipodoc(long idtipodoc) {
        this.idtipodoc = idtipodoc;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tipodoc tipodoc = (Tipodoc) o;

        if (idtipodoc != tipodoc.idtipodoc) return false;
        if (descripcion != null ? !descripcion.equals(tipodoc.descripcion) : tipodoc.descripcion != null) return false;
        if (nombre != null ? !nombre.equals(tipodoc.nombre) : tipodoc.nombre != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idtipodoc ^ (idtipodoc >>> 32));
        result = 31 * result + (nombre != null ? nombre.hashCode() : 0);
        result = 31 * result + (descripcion != null ? descripcion.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
