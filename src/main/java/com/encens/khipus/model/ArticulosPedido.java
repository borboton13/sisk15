/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encens.khipus.model;

import com.encens.khipus.util.MoneyUtil;

import java.io.Serializable;
import java.math.BigInteger;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "articulos_pedido")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ArticulosPedido.findAll", query = "SELECT a FROM ArticulosPedido a"),
    @NamedQuery(name = "ArticulosPedido.findByIdarticulospedido", query = "SELECT a FROM ArticulosPedido a WHERE a.idarticulospedido = :idarticulospedido"),
    @NamedQuery(name = "ArticulosPedido.findByCantidad", query = "SELECT a FROM ArticulosPedido a WHERE a.cantidad = :cantidad"),
    @NamedQuery(name = "ArticulosPedido.findByCodAlm", query = "SELECT a FROM ArticulosPedido a WHERE a.codAlm = :codAlm"),
    @NamedQuery(name = "ArticulosPedido.findByPrecio", query = "SELECT a FROM ArticulosPedido a WHERE a.precio = :precio"),
    @NamedQuery(name = "ArticulosPedido.findByReposicion", query = "SELECT a FROM ArticulosPedido a WHERE a.reposicion = :reposicion"),
    @NamedQuery(name = "ArticulosPedido.findByTotal", query = "SELECT a FROM ArticulosPedido a WHERE a.total = :total"),
    @NamedQuery(name = "ArticulosPedido.findByPromocion", query = "SELECT a FROM ArticulosPedido a WHERE a.promocion = :promocion"),
    @NamedQuery(name = "ArticulosPedido.findByCaja", query = "SELECT a FROM ArticulosPedido a WHERE a.caja = :caja"),
    @NamedQuery(name = "ArticulosPedido.findByPrecioInv", query = "SELECT a FROM ArticulosPedido a WHERE a.precioInv = :precioInv"),
    @NamedQuery(name = "ArticulosPedido.findByTotalInv", query = "SELECT a FROM ArticulosPedido a WHERE a.totalInv = :totalInv")})
@TableGenerator(name = "ArticulosPedido_Gen"
        ,table="ID_GEN"
        ,pkColumnName = "GEN_NAME"
        ,valueColumnName = "GEN_VAL")
public class ArticulosPedido implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "ArticulosPedido_Gen")
    @Column(name = "IDARTICULOSPEDIDO")
    private Long idarticulospedido;

    @Column(name = "CANTIDAD")
    private Integer cantidad = 0;

    @Column(name = "COD_ALM")
    private String codAlm;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation

    @Column(name = "PRECIO")
    private Double precio = 0.0;

    @Column(name = "IMPORTE")
    private Double importe = 0.0;

    @Column(name = "costo_uni")
    private Double unitCost;

    @Column(name = "CU")
    private Double cu;

    @Column(name = "REPOSICION")
    private Integer reposicion = 0;

    @Column(name= "POR_REPONER")
    private Integer porReponer = 0;

    @Column(name = "TOTAL")
    private Integer total = 0;

    @Column(name = "PROMOCION")
    private Integer promocion = 0;

    @Column(name = "CAJA")
    private BigInteger caja;

    @Column(name = "PRECIO_INV")
    private Double precioInv = 0.0;

    @Column(name = "TOTAL_INV")
    private Integer totalInv = 0;

    @Column(name ="ESTADO")
    private String estado;

    @Column(name="TIPO")
    private String tipo;

    @JoinColumn(name = "IDPEDIDOS",referencedColumnName = "IDPEDIDOS")
    @ManyToOne(optional = false,cascade={CascadeType.PERSIST, CascadeType.MERGE},fetch = FetchType.EAGER)
    private Pedidos pedidos;

    @JoinColumn(name = "IDVENTADIRECTA",referencedColumnName = "IDVENTADIRECTA")
    @ManyToOne(optional = false,cascade={CascadeType.PERSIST, CascadeType.MERGE},fetch = FetchType.EAGER)
    private Ventadirecta ventadirecta;

    @JoinColumns({
            @JoinColumn(name = "COD_ART", referencedColumnName = "COD_ART"),
            @JoinColumn(name = "NO_CIA", referencedColumnName = "NO_CIA")})
    @ManyToOne
    private InvArticulos invArticulos;

    public ArticulosPedido() {
    }

    public ArticulosPedido(Long idarticulospedido) {
        this.idarticulospedido = idarticulospedido;
    }


    public Long getIdarticulospedido() {
        return idarticulospedido;
    }

    public void setIdarticulospedido(Long idarticulospedido) {
        this.idarticulospedido = idarticulospedido;
    }

    public Integer getCantidad() {
        if(cantidad == null)
            cantidad =0;
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getCodAlm() {
        return codAlm;
    }

    public void setCodAlm(String codAlm) {
        this.codAlm = codAlm;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getReposicion() {
        return reposicion;
    }

    public void setReposicion(Integer reposicion) {
        this.reposicion = reposicion;
    }

    public Integer getTotal() {
        total = reposicion + cantidad + promocion;
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getPromocion() {
        return promocion;
    }

    public void setPromocion(Integer promocion) {
        this.promocion = promocion;
    }

    public BigInteger getCaja() {
        return caja;
    }

    public void setCaja(BigInteger caja) {
        this.caja = caja;
    }

    public Double getPrecioInv() {
        return precioInv;
    }

    public void setPrecioInv(Double precioInv) {
        this.precioInv = precioInv;
    }

    public Integer getTotalInv() {
        return totalInv;
    }

    public void setTotalInv(Integer totalInv) {
        this.totalInv = totalInv;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idarticulospedido != null ? idarticulospedido.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ArticulosPedido)) {
            return false;
        }
        /*ArticulosPedido other = (ArticulosPedido) object;
        if ((this.idarticulospedido == null && other.idarticulospedido != null) || (this.idarticulospedido != null && !this.idarticulospedido.equals(other.idarticulospedido))) {
            return false;
        }*/

        return this.getInvArticulos().getInvArticulosPK().getCodArt().equals(((ArticulosPedido) object).getInvArticulos().getInvArticulosPK().getCodArt());
    }

    @Override
    public String toString() {
        return invArticulos.getDescri();
    }

    public InvArticulos getInvArticulos() {
        return invArticulos;
    }

    public void setInvArticulos(InvArticulos invArticulos) {
        this.invArticulos = invArticulos;
    }

    public Double getImporte() {
        if(cantidad != null && precio != null)
        {
            MoneyUtil moneyUtil = new MoneyUtil();
            importe= moneyUtil.redondear(cantidad * precio,2);
        }
        else
            importe = 0.0;
        return importe;
    }

    public void setImporte(Double impor) {
        MoneyUtil moneyUtil = new MoneyUtil();
        this.importe =moneyUtil.redondear(impor,2);
    }

    public Pedidos getPedidos() {
        return pedidos;
    }

    public void setPedidos(Pedidos pedidos) {
        this.pedidos = pedidos;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getPorReponer() {
        return porReponer;
    }

    public void setPorReponer(Integer porReponer) {
        if(porReponer >0.0)
        {
            this.estado = "RECHAZADO";
        }else{
            this.estado = "";
        }
        this.porReponer = porReponer;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Ventadirecta getVentadirecta() {
        return ventadirecta;
    }

    public void setVentadirecta(Ventadirecta ventadirecta) {
        this.ventadirecta = ventadirecta;
    }

    public Double getCu() {
        return cu;
    }

    public void setCu(Double cu) {
        this.cu = cu;
    }

    public Double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(Double unitCost) {
        this.unitCost = unitCost;
    }
}
