package com.encens.khipus.model;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by Diego on 17/06/2015.
 */
@Entity
@Table(name = "pago")
public class Pago implements Serializable {

    @Id
    @Column(name = "id_pago", nullable = false, insertable = true, updatable = true)
    @TableGenerator(name = "Pago_Gen"
            ,table="ID_GEN"
            ,pkColumnName = "GEN_NAME"
            ,valueColumnName = "GEN_VAL"
            ,initialValue = 1
            ,allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Pago_Gen")
    private long idPago;

    @Basic
    @Column(name = "fecha", nullable = false, insertable = true, updatable = true)
    private Timestamp fecha;

    @Basic
    @Column(name = "pago", nullable = true, insertable = true, updatable = true)
    private Double pago;

    @Basic
    @Column(name = "descripcion", nullable = true, insertable = true, updatable = true, length = 150)
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "IDPERSONACLIENTE", referencedColumnName = "IDPERSONACLIENTE", nullable = false)
    private Persona persona;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_tmpenc", referencedColumnName = "id_tmpenc")
    private SfTmpenc asiento;

    @ManyToOne
    @JoinColumn(name = "idusuario", referencedColumnName = "idusuario", nullable = false)
    private Usuario usuario;

    @JoinColumn(name = "IDSUCURSAL", referencedColumnName = "IDSUCURSAL")
    @ManyToOne(optional = true)
    private Sucursal sucursal;

    public long getIdPago() {
        return idPago;
    }

    public void setIdPago(long idPago) {
        this.idPago = idPago;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public Double getPago() {
        return pago;
    }

    public void setPago(Double monto) {
        this.pago = monto;
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

        Pago pago = (Pago) o;

        if (idPago != pago.idPago) return false;
        if (descripcion != null ? !descripcion.equals(pago.descripcion) : pago.descripcion != null) return false;
        if (fecha != null ? !fecha.equals(pago.fecha) : pago.fecha != null) return false;
        if (pago != null ? !pago.equals(pago.pago) : pago.pago != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idPago ^ (idPago >>> 32));
        result = 31 * result + (fecha != null ? fecha.hashCode() : 0);
        result = 31 * result + (pago != null ? pago.hashCode() : 0);
        result = 31 * result + (descripcion != null ? descripcion.hashCode() : 0);
        return result;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona personaclienteByIdpersonacliente) {
        this.persona = personaclienteByIdpersonacliente;
    }

    public SfTmpenc getAsiento() {
        return asiento;
    }

    public void setAsiento(SfTmpenc sfTmpencByIdTmpenc) {
        this.asiento = sfTmpencByIdTmpenc;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuarioByIdusuario) {
        this.usuario = usuarioByIdusuario;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }
}
