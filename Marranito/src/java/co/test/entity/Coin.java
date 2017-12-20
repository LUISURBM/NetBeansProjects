/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.test.entity;

import java.io.Serializable;
import java.math.BigInteger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author LUIS
 */
@Entity
@Table(catalog = "", schema = "APP")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Coin.findAll", query = "SELECT c FROM Coin c")
    , @NamedQuery(name = "Coin.findByValor", query = "SELECT c FROM Coin c WHERE c.valor = :valor")
    , @NamedQuery(name = "Coin.findByCantidad", query = "SELECT c FROM Coin c WHERE c.cantidad = :cantidad")
    , @NamedQuery(name = "Coin.findById", query = "SELECT c FROM Coin c WHERE c.id = :id")
    , @NamedQuery(name = "Coin.findByUserId", query = "SELECT c FROM Coin c WHERE c.idUsuario.username = :id")})
public class Coin implements Serializable {

    private BigInteger valor;
    private BigInteger cantidad;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Integer id;
    @JoinColumn(name = "ID_USUARIO", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Usercoin idUsuario;

    private static final long serialVersionUID = 1L;


    public Coin() {
    }

    public Coin(Integer id) {
        this.id = id;
    }

    public BigInteger getValor() {
        return valor;
    }

    public void setValor(BigInteger valor) {
        this.valor = valor;
    }

    public BigInteger getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigInteger cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Coin)) {
            return false;
        }
        Coin other = (Coin) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "co.test.entity.Coin[ id=" + id + " ]";
    }

    public Usercoin getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Usercoin idUsuario) {
        this.idUsuario = idUsuario;
    }

}
