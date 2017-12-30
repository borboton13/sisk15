/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "usuario")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Usuario.findAll", query = "SELECT u FROM Usuario u"),
    @NamedQuery(name = "Usuario.findByIdusuario", query = "SELECT u FROM Usuario u WHERE u.idusuario = :idusuario"),
    @NamedQuery(name = "Usuario.findByFechacreacion", query = "SELECT u FROM Usuario u WHERE u.fechacreacion = :fechacreacion"),
    @NamedQuery(name = "Usuario.findByEmail", query = "SELECT u FROM Usuario u WHERE u.email = :email"),
    @NamedQuery(name = "Usuario.findByNumerousuario", query = "SELECT u FROM Usuario u WHERE u.numerousuario = :numerousuario"),
    @NamedQuery(name = "Usuario.findByUsuariofinanzas", query = "SELECT u FROM Usuario u WHERE u.usuariofinanzas = :usuariofinanzas"),
    @NamedQuery(name = "Usuario.findByClave", query = "SELECT u FROM Usuario u WHERE u.clave = :clave"),
    @NamedQuery(name = "Usuario.findByUsuario", query = "SELECT u FROM Usuario u WHERE u.usuario = :usuario"),
    @NamedQuery(name = "Usuario.findByVersion", query = "SELECT u FROM Usuario u WHERE u.version = :version")})

public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;
    @JoinColumn(name = "IDCOMPANIA", referencedColumnName = "IDCOMPANIA")
    @ManyToOne
    private Compania idcompania;
    @Id
    @Column(name = "IDUSUARIO")
    private Long idusuario;

    @Basic(optional = false)
    @NotNull
    @Column(name = "FECHACREACION")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechacreacion;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 100)
    @Column(name = "EMAIL")
    private String email;

    @Size(max = 4)
    @Column(name = "NUMEROUSUARIO")
    private String numerousuario;

    @Column(name = "USUARIOFINANZAS")
    private Integer usuariofinanzas;

    @Size(max = 200)
    @Column(name = "CLAVE")
    private String clave;

    @Size(max = 50)
    @Column(name = "USUARIO")
    private String usuario;

    @Column(name = "VERSION")
    private BigInteger version;

    @OneToMany(mappedBy = "usuario")
    private Collection<Pedidos> pedidos;

    @OneToMany(mappedBy = "usuario")
    private Collection<Ventadirecta> ventadirectas;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuario")
    private Collection<Impresionfactura> facturas;

    @OneToMany(mappedBy = "usuario")
    private Collection<Pago> pagosByIdusuario;

    @JoinColumn(name = "IDSUCURSAL", referencedColumnName = "IDSUCURSAL")
    @ManyToOne(optional = true)
    private Sucursal sucursal;

    public Usuario() {
    }

    public Usuario(Long idusuario) {
        this.idusuario = idusuario;
    }

    public Usuario(Long idusuario, Date fechacreacion) {
        this.idusuario = idusuario;
        this.fechacreacion = fechacreacion;
    }

    public Long getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(Long idusuario) {
        this.idusuario = idusuario;
    }

    public Date getFechacreacion() {
        return fechacreacion;
    }

    public void setFechacreacion(Date fechacreacion) {
        this.fechacreacion = fechacreacion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumerousuario() {
        return numerousuario;
    }

    public void setNumerousuario(String numerousuario) {
        this.numerousuario = numerousuario;
    }

    public Integer getUsuariofinanzas() {
        return usuariofinanzas;
    }

    public void setUsuariofinanzas(Integer usuariofinanzas) {
        this.usuariofinanzas = usuariofinanzas;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public BigInteger getVersion() {
        return version;
    }

    public void setVersion(BigInteger version) {
        this.version = version;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idusuario != null ? idusuario.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Usuario)) {
            return false;
        }
        Usuario other = (Usuario) object;
        if ((this.idusuario == null && other.idusuario != null) || (this.idusuario != null && !this.idusuario.equals(other.idusuario))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.encens.khipus.Usuario[ idusuario=" + idusuario + " ]";
    }

    /*@XmlTransient
    public Collection<Rol> getRolCollection() {
        return rolCollection;
    }

    public void setRolCollection(Collection<Rol> rolCollection) {
        this.rolCollection = rolCollection;
    }
*/
    public Compania getIdcompania() {
        return idcompania;
    }

    public void setIdcompania(Compania idcompania) {
        this.idcompania = idcompania;
    }

    @XmlTransient
    public Collection<Pedidos> getPedidos() {
        return pedidos;
    }

    public void setPedidos(Collection<Pedidos> pedidosCollection) {
        this.pedidos = pedidosCollection;
    }

    @XmlTransient
    public Collection<Impresionfactura> getFacturas() {
        return facturas;
    }

    public void setFacturas(Collection<Impresionfactura> impresionfacturaCollection) {
        this.facturas = impresionfacturaCollection;
    }

    public Collection<Ventadirecta> getVentadirectas() {
        return ventadirectas;
    }

    public void setVentadirectas(Collection<Ventadirecta> ventadirectas) {
        this.ventadirectas = ventadirectas;
    }

    public Collection<Pago> getPagosByIdusuario() {
        return pagosByIdusuario;
    }

    public void setPagosByIdusuario(Collection<Pago> pagosByIdusuario) {
        this.pagosByIdusuario = pagosByIdusuario;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }
}
