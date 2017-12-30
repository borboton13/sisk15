package com.encens.khipus.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Diego on 17/06/2015.
 */
@Entity
@Table(name = "sf_confenc")
public class SfConfenc implements Serializable {

    @Basic
    @Column(name = "descripcion", nullable = true, insertable = true, updatable = true)
    private String descripcion;
    @Basic
    @Column(name = "tipo_doc", nullable = true, insertable = true, updatable = true)
    private String tipoDoc;
    @Basic
    @Column(name = "glosa", nullable = true, insertable = true, updatable = true)
    private String glosa;
    @Basic
    @Column(name = "idusuario", nullable = false, insertable = true, updatable = true)
    private long idusuario;
    @Basic
    @Column(name = "OPERACION")
    private String operacion;
    @OneToMany(mappedBy = "sfConfenc",cascade = CascadeType.PERSIST)
    private Collection<SfConfdet> asientos = new ArrayList<>();
    @JoinColumn(name = "cuenta",referencedColumnName = "cuenta")
    @ManyToOne
    private Arcgms cuenta;
    @Id
    @Column(name = "id_sf_confenc", nullable = false, insertable = true, updatable = true)
    @TableGenerator(name = "SfConfenc_Gen"
            ,table="ID_GEN"
            ,pkColumnName = "GEN_NAME"
            ,valueColumnName = "GEN_VAL"
            ,initialValue = 1
            ,allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "SfConfenc_Gen")
    private long idSfConfenc;
    public long getIdSfConfenc() {
        return idSfConfenc;
    }

    public void setIdSfConfenc(long idSfConfenc) {
        this.idSfConfenc = idSfConfenc;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipoDoc() {
        return tipoDoc;
    }

    public void setTipoDoc(String tipoDoc) {
        this.tipoDoc = tipoDoc;
    }

    public String getGlosa() {
        return glosa;
    }

    public void setGlosa(String glosa) {
        this.glosa = glosa;
    }

    public long getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(long idusuario) {
        this.idusuario = idusuario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SfConfenc sfConfenc = (SfConfenc) o;

        if (idSfConfenc != sfConfenc.idSfConfenc) return false;
        if (idusuario != sfConfenc.idusuario) return false;
        if (descripcion != null ? !descripcion.equals(sfConfenc.descripcion) : sfConfenc.descripcion != null)
            return false;
        if (glosa != null ? !glosa.equals(sfConfenc.glosa) : sfConfenc.glosa != null) return false;
        if (tipoDoc != null ? !tipoDoc.equals(sfConfenc.tipoDoc) : sfConfenc.tipoDoc != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idSfConfenc ^ (idSfConfenc >>> 32));
        result = 31 * result + (descripcion != null ? descripcion.hashCode() : 0);
        result = 31 * result + (tipoDoc != null ? tipoDoc.hashCode() : 0);
        result = 31 * result + (glosa != null ? glosa.hashCode() : 0);
        result = 31 * result + (int) (idusuario ^ (idusuario >>> 32));
        return result;
    }

    public Collection<SfConfdet> getAsientos() {
        return asientos;
    }

    public void setAsientos(Collection<SfConfdet> sfConfdetsByIdSfConfenc) {
        this.asientos = sfConfdetsByIdSfConfenc;
    }

    public Arcgms getCuenta() {
        return cuenta;
    }

    public void setCuenta(Arcgms arcgms) {
        this.cuenta = arcgms;
    }

    public String getOperacion() {
        return operacion;
    }

    public void setOperacion(String opracion) {
        this.operacion = opracion;
    }
}
