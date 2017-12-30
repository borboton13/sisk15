/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "dosificacion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Dosificacion.findAll", query = "SELECT d FROM Dosificacion d"),
    @NamedQuery(name = "Dosificacion.findByIddosificacion", query = "SELECT d FROM Dosificacion d WHERE d.iddosificacion = :iddosificacion"),
    @NamedQuery(name = "Dosificacion.findByNroautorizacion", query = "SELECT d FROM Dosificacion d WHERE d.nroautorizacion = :nroautorizacion"),
    @NamedQuery(name = "Dosificacion.findByFechavencimiento", query = "SELECT d FROM Dosificacion d WHERE d.fechavencimiento = :fechavencimiento"),
    @NamedQuery(name = "Dosificacion.findByLlave", query = "SELECT d FROM Dosificacion d WHERE d.llave = :llave"),
    @NamedQuery(name = "Dosificacion.findByEstado", query = "SELECT d FROM Dosificacion d WHERE d.estado = :estado"),
    @NamedQuery(name = "Dosificacion.findByFacturadel", query = "SELECT d FROM Dosificacion d WHERE d.facturadel = :facturadel"),
    @NamedQuery(name = "Dosificacion.findByFacturaal", query = "SELECT d FROM Dosificacion d WHERE d.facturaal = :facturaal"),
    @NamedQuery(name = "Dosificacion.findByPeriodo", query = "SELECT d FROM Dosificacion d WHERE d.fechavencimiento >= :fechaFactura and d.estado = 'ACTIVO'"),
    @NamedQuery(name = "Dosificacion.findByOffice", query = "SELECT d FROM Dosificacion d WHERE d.sucursal = :sucursal and d.estado = 'ACTIVO'"),
    @NamedQuery(name = "Dosificacion.findByNumeroactual", query = "SELECT d FROM Dosificacion d WHERE d.numeroactual = :numeroactual")})
public class Dosificacion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @NotNull
    @Column(name = "IDDOSIFICACION")
    private Long iddosificacion;
    @Column(name = "NROAUTORIZACION")
    private BigInteger nroautorizacion;
    @Column(name = "FECHAVENCIMIENTO")
    @Temporal(TemporalType.DATE)
    private Date fechavencimiento;
    @Column(name = "FECHAINICIO")
    @Temporal(TemporalType.DATE)
    private Date fechainicio;
    @Size(max = 100)
    @Column(name = "LLAVE")
    private String llave;
    @Size(max = 15)
    @Column(name = "ESTADO")
    private String estado;
    @Column(name = "FACTURADEL")
    private BigInteger facturadel;
    @Column(name = "FACTURAAL")
    private BigInteger facturaal;
    @Column(name = "EST_COD")
    private String estCod;
    @Column(name= "ETIQUETAEMPRESA")
    private String etiquetaEmpresa;
    @Column(name = "NUMEROACTUAL")
    private Integer numeroactual;
    @Column(name = "FECHACONTROL")
    @Temporal(TemporalType.DATE)
    private Date fechaControl;
    @Column(name = "NITEMPRESA")
    private String nitEmpresa;
    @Column(name= "ETIQUETALEY")
    private String etiquetaLey;
    @OneToMany(mappedBy = "dosificacion")
    private Collection<Impresionfactura> impresionfacturas;
    @JoinColumn(name = "IDSUCURSAL", referencedColumnName = "IDSUCURSAL")
    @ManyToOne(optional = true)
    private Sucursal sucursal;

    public Dosificacion() {
    }

    public Dosificacion(Long iddosificacion) {
        this.iddosificacion = iddosificacion;
    }

    public Long getIddosificacion() {
        return iddosificacion;
    }

    public void setIddosificacion(Long iddosificacion) {
        this.iddosificacion = iddosificacion;
    }

    public BigInteger getNroautorizacion() {
        return nroautorizacion;
    }

    public void setNroautorizacion(BigInteger nroautorizacion) {
        this.nroautorizacion = nroautorizacion;
    }

    public Date getFechavencimiento() {
        return fechavencimiento;
    }

    public void setFechavencimiento(Date fechavencimiento) {
        this.fechavencimiento = fechavencimiento;
    }

    public String getLlave() {
        return llave;
    }

    public void setLlave(String llave) {
        this.llave = llave;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigInteger getFacturadel() {
        return facturadel;
    }

    public void setFacturadel(BigInteger facturadel) {
        this.facturadel = facturadel;
    }

    public BigInteger getFacturaal() {
        return facturaal;
    }

    public void setFacturaal(BigInteger facturaal) {
        this.facturaal = facturaal;
    }

    public Integer getNumeroactual() {
        return numeroactual;
    }

    public void setNumeroactual(Integer numeroactual) {
        this.numeroactual = numeroactual;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (iddosificacion != null ? iddosificacion.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Dosificacion)) {
            return false;
        }
        Dosificacion other = (Dosificacion) object;
        if ((this.iddosificacion == null && other.iddosificacion != null) || (this.iddosificacion != null && !this.iddosificacion.equals(other.iddosificacion))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.encens.khipus.model.Dosificacion[ iddosificacion=" + iddosificacion + " ]";
    }

    @XmlTransient
    public Collection<Impresionfactura> getImpresionfacturas() {
        return impresionfacturas;
    }

    public void setImpresionfacturas(Collection<Impresionfactura> impresionfacturaCollection) {
        this.impresionfacturas = impresionfacturaCollection;
    }

    public String getEstCod() {
        return estCod;
    }

    public void setEstCod(String estCod) {
        this.estCod = estCod;
    }

    public String getNitEmpresa() {
        return nitEmpresa;
    }

    public void setNitEmpresa(String nitEmpresa) {
        this.nitEmpresa = nitEmpresa;
    }

    public String getEtiquetaEmpresa() {
        return etiquetaEmpresa;
    }

    public void setEtiquetaEmpresa(String etiquetaEmpresa) {
        this.etiquetaEmpresa = etiquetaEmpresa;
    }

    public Date getFechainicio() {
        return fechainicio;
    }

    public void setFechainicio(Date fechainicio) {
        this.fechainicio = fechainicio;
    }

    public Date getFechaControl() {
        return fechaControl;
    }

    public void setFechaControl(Date fechaControl) {
        this.fechaControl = fechaControl;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

    /**
     * @return the etiquetaLey
     */
    public String getEtiquetaLey() {
        return etiquetaLey;
    }

    /**
     * @param etiquetaLey the etiquetaLey to set
     */
    public void setEtiquetaLey(String etiquetaLey) {
        this.etiquetaLey = etiquetaLey;
    }
}
