package com.encens.khipus.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Diego on 11/08/2015.
 */
@Entity
public class Sucursal {
    @Id
    @Column(name = "IDSUCURSAL", nullable = false, insertable = true, updatable = true)
    private long idsucursal;
    @Basic
    @Column(name = "NOMBRE", nullable = true, insertable = true, updatable = true, length = 150)
    private String nombre;
    @Basic
    @Column(name = "descripcion", nullable = true, insertable = true, updatable = true, length = 30)
    private String descripcion;
    @OneToMany(cascade = CascadeType.PERSIST,mappedBy = "sucursal")
    private Collection<Dosificacion> dosificaciones = new ArrayList<>();
    @OneToMany(cascade = CascadeType.PERSIST,mappedBy = "sucursal")
    private Collection<Usuario> usuarios = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "sucursal")
    private Collection<Pago> pagos = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "sucursal")
    private Collection<Pedidos> pedidos = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "sucursal")
    private Collection<Ventadirecta> ventadirectas = new ArrayList<>();

    public long getIdsucursal() {
        return idsucursal;
    }

    public void setIdsucursal(long idsucursal) {
        this.idsucursal = idsucursal;
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

        Sucursal sucursal = (Sucursal) o;

        if (idsucursal != sucursal.idsucursal) return false;
        if (descripcion != null ? !descripcion.equals(sucursal.descripcion) : sucursal.descripcion != null)
            return false;
        if (nombre != null ? !nombre.equals(sucursal.nombre) : sucursal.nombre != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idsucursal ^ (idsucursal >>> 32));
        result = 31 * result + (nombre != null ? nombre.hashCode() : 0);
        result = 31 * result + (descripcion != null ? descripcion.hashCode() : 0);
        return result;
    }

    public Collection<Dosificacion> getDosificaciones() {
        return dosificaciones;
    }

    public void setDosificaciones(Collection<Dosificacion> dosificaciones) {
        this.dosificaciones = dosificaciones;
    }

    public Collection<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(Collection<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public Collection<Pago> getPagos() {
        return pagos;
    }

    public void setPagos(Collection<Pago> pagos) {
        this.pagos = pagos;
    }

    public Collection<Pedidos> getPedidos() {
        return pedidos;
    }

    public void setPedidos(Collection<Pedidos> pedidos) {
        this.pedidos = pedidos;
    }

    public Collection<Ventadirecta> getVentadirectas() {
        return ventadirectas;
    }

    public void setVentadirectas(Collection<Ventadirecta> ventadirectas) {
        this.ventadirectas = ventadirectas;
    }
}
