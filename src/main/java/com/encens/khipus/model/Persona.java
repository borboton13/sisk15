/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.model;

import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "personacliente")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_persona")
@DiscriminatorValue(value = "persona")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Personas.findAll", query = "SELECT p FROM Persona p"),
    @NamedQuery(name = "Personas.findByPiId", query = "SELECT p FROM Persona p WHERE p.piId = :piId"),
    @NamedQuery(name = "Personas.findByNroDoc", query = "SELECT p FROM Persona p WHERE p.nroDoc = :nroDoc"),
    @NamedQuery(name = "Personas.findByAp", query = "SELECT p FROM Persona p WHERE p.ap = :ap"),
    @NamedQuery(name = "Personas.findByAm", query = "SELECT p FROM Persona p WHERE p.am = :am"),
    @NamedQuery(name = "Personas.findByNom", query = "SELECT p FROM Persona p WHERE p.nom = :nom"),
    @NamedQuery(name = "Personas.findBySexo", query = "SELECT p FROM Persona p WHERE p.sexo = :sexo"),
    @NamedQuery(name = "Personas.findByEstCivil", query = "SELECT p FROM Persona p WHERE p.estCivil = :estCivil"),
    @NamedQuery(name = "Personas.findByFechaNac", query = "SELECT p FROM Persona p WHERE p.fechaNac = :fechaNac"),
    @NamedQuery(name = "Personas.findByCemCod", query = "SELECT p FROM Persona p WHERE p.cemCod = :cemCod"),
    @NamedQuery(name = "Personas.findByOcuCod", query = "SELECT p FROM Persona p WHERE p.ocuCod = :ocuCod"),
    @NamedQuery(name = "Personas.findByTdoCod", query = "SELECT p FROM Persona p WHERE p.tdoCod = :tdoCod"),
    @NamedQuery(name = "Personas.findBySisCod", query = "SELECT p FROM Persona p WHERE p.sisCod = :sisCod")})
public class Persona implements Serializable {

    @Size(max = 45)
    @Column(name = "TIPO_PERSONA")
    private String tipoPersona;

    @Column(name = "TELEFONO")
    private Integer telefono;

    @Size(max = 100)
    @Column(name = "OBSERVACION")
    private String observacion;

    @Column(name = "PORCENTAJEGARANTIA")
    private Double porcentajeGarantia = 0.0;

    @JoinColumn(name = "IDTERRITORIOTRABAJO", referencedColumnName = "IDTERRITORIOTRABAJO")
    @ManyToOne
    private Territoriotrabajo territoriotrabajo;

    @OneToMany(mappedBy = "cliente")
    private Collection<Pedidos> pedidosCliente;

    @OneToMany(mappedBy = "cliente")
    private Collection<Ventadirecta> pedidosVentadirecta;

    @OneToMany(mappedBy = "distribuidor")
    private Collection<Territoriotrabajo> territorios;

    @OneToMany(mappedBy = "persona")
    private Collection<Ventacliente> ventaclientes = new ArrayList<>();

    @OneToMany(mappedBy = "persona", cascade = CascadeType.PERSIST)
    private Collection<Pago> pagos = new ArrayList<>();

    @JoinColumn(name = "IDDEPARTAMENTO",referencedColumnName = "IDDEPARTAMENTO")
    @ManyToOne(optional = true)
    private Departamento departamento;

    private static final long serialVersionUID = 1L;
    @Id
    @NotNull
    @TableGenerator(name = "Persona_Gen"
            ,table="ID_GEN"
            ,pkColumnName = "GEN_NAME"
            ,valueColumnName = "GEN_VAL"
            ,initialValue = 1
            ,allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Persona_Gen")
    @Column(name = "IDPERSONACLIENTE")
    private Long piId;
    @Size(max = 20)
    @Column(name = "NRO_DOC")
    private String nroDoc;
    @Size(max = 65)
    @Column(name = "AP")
    private String ap = "";
    @Size(max = 65)
    @Column(name = "AM")
    private String am = "";
    @Size( max = 100)
    @Column(name = "NOM")
    private String nom = "";
    @Column(name = "SEXO")
    private String sexo;
    @Size(max = 1)
    @Column(name = "EST_CIVIL")
    private String estCivil;
    @Column(name = "FECHA_NAC")
    @Temporal(TemporalType.DATE)
    private Date fechaNac;
    @Size(max = 15)
    @Column(name = "CEM_COD")
    private String cemCod;
    @Size(max = 15)
    @Column(name = "OCU_COD")
    private String ocuCod;
    @Size(max = 15)
    @Column(name = "TDO_COD")
    private String tdoCod;
    @Size(max = 20)
    @Column(name = "SIS_COD")
    private String sisCod;
    @Size(max = 100)
    @Column(name = "DIRECCION")
    private String direccion;
    @Size(max = 40)
    @Column(name = "NIT")
    private String nit;
    @Column(name = "RAZONSOCIAL")
    private String razonsocial = "";
    @Column(name = "PORCENTAJECOMISION")
    private Double porcentajeComision = 0.0;
    @Size(max = 10)
    @Column(name = "CODIGOCLIENTE")
    private String codigo;
    @Column(name = "con_factura")
    private Boolean confactura = true;
    @JoinColumn(name = "IDTIPOCLIENTE", referencedColumnName = "IDTIPOCLIENTE")
    @ManyToOne
    private Tipocliente tipocliente;
    @Transient
    private String nombreCompleto;

    @Column(name = "ESPERSONA")
    private Boolean espersona = true;

    public Persona() {
    }

    public Persona(Long piId) {
        this.piId = piId;
    }

    public Persona(Long piId, String nom) {
        this.piId = piId;
        this.nom = nom;
    }

    public Long getPiId() {
        return piId;
    }

    public void setPiId(Long piId) {
        this.piId = piId;
    }

    public String getNroDoc() {
        return nroDoc;
    }

    public void setNroDoc(String nroDoc) {
        this.nroDoc = nroDoc;
    }

    public String getAp() {
        return ap;
    }

    public void setAp(String ap) {
        this.ap = ap;
    }

    public String getAm() {
        return am;
    }

    public void setAm(String am) {
        this.am = am;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getSexo() {
        if(this.sexo == null)
            this.sexo = "MASCULINO";
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getEstCivil() {
        return estCivil;
    }

    public void setEstCivil(String estCivil) {
        this.estCivil = estCivil;
    }

    public Date getFechaNac() {
        return fechaNac;
    }

    public void setFechaNac(Date fechaNac) {
        this.fechaNac = fechaNac;
    }

    public String getCemCod() {
        return cemCod;
    }

    public void setCemCod(String cemCod) {
        this.cemCod = cemCod;
    }

    public String getOcuCod() {
        return ocuCod;
    }

    public void setOcuCod(String ocuCod) {
        this.ocuCod = ocuCod;
    }

    public String getTdoCod() {
        return tdoCod;
    }

    public void setTdoCod(String tdoCod) {
        this.tdoCod = tdoCod;
    }

    public String getSisCod() {
        return sisCod;
    }

    public void setSisCod(String sisCod) {
        this.sisCod = sisCod;
    }

    public String getTipoPersona() {
        return tipoPersona;
    }

    public void setTipoPersona(String tipoPersona) {
        this.tipoPersona = tipoPersona;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }


    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getRazonsocial() {
        return razonsocial;
        /*
        String result = "";
        if (razonsocial == null || razonsocial.equals(""))
            result = nom+" "+ap+" "+am;
        else
            result = razonsocial;
            
        return result;
                */
    }

    public void setRazonsocial(String razonsocial) {
        this.razonsocial = razonsocial;
    }

    public Double getPorcentajeComision() {
        return porcentajeComision;
    }

    public void setPorcentajeComision(Double descuento) {
        this.porcentajeComision = descuento;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }


    public Tipocliente getTipocliente() {
        return tipocliente;
    }

    public void setTipocliente(Tipocliente tipocliente) {
        this.tipocliente = tipocliente;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (piId != null ? piId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Persona)) {
            return false;
        }
        Persona other = (Persona) object;
        if ((this.piId == null && other.piId != null) || (this.piId != null && !this.piId.equals(other.piId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.encens.khipus.model.Personas[ piId=" + piId + " ]";
    }

    public String getNombreCompleto(){        
        if(this.piId == null)
            return "";        
        if(!StringUtils.isEmpty(nom))
            return nom + " " + ap + " " + am;
        else
            return razonsocial;
    }

    public Integer getTelefono() {
        return telefono;
    }

    public void setTelefono(Integer telefono) {
        this.telefono = telefono;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Double getPorcentajeGarantia() {
        return porcentajeGarantia;
    }

    public void setPorcentajeGarantia(Double porcentajegarantia) {
        this.porcentajeGarantia = porcentajegarantia;
    }

    public Territoriotrabajo getTerritoriotrabajo() {
        return territoriotrabajo;
    }

    public void setTerritoriotrabajo(Territoriotrabajo idterritoriotrabajo) {
        this.territoriotrabajo = idterritoriotrabajo;
    }

    @XmlTransient
    public Collection<Pedidos> getPedidosCliente() {
        return pedidosCliente;
    }

    public void setPedidosCliente(Collection<Pedidos> pedidosCollection1) {
        this.pedidosCliente = pedidosCollection1;
    }

    @XmlTransient
    public Collection<Territoriotrabajo> getTerritorios() {
        return territorios;
    }

    public void setTerritorios(Collection<Territoriotrabajo> territoriotrabajoCollection) {
        this.territorios = territoriotrabajoCollection;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public Boolean getConfactura() {
        return confactura;
    }

    public void setConfactura(Boolean confactura) {
        this.confactura = confactura;
    }

    public Collection<Ventadirecta> getPedidosVentadirecta() {
        return pedidosVentadirecta;
    }

    public void setPedidosVentadirecta(Collection<Ventadirecta> pedidosVentadirecta) {
        this.pedidosVentadirecta = pedidosVentadirecta;
    }

    public Collection<Ventacliente> getVentaclientes() {
        return ventaclientes;
    }

    public void setVentaclientes(Collection<Ventacliente> ventaclientes) {
        this.ventaclientes = ventaclientes;
    }


    public Boolean getEspersona() {
        return espersona;
    }

    public void setEspersona(Boolean espersona) {
        this.espersona = espersona;
    }
    public Collection<Pago> getPagos() {
        return pagos;
    }

    public void setPagos(Collection<Pago> pagos) {
        this.pagos = pagos;
    }
}
