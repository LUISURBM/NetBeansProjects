/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.ms.entity;

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
 * @author ADMIN
 */
@Entity
@Table(catalog = "", schema = "APP")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Profit.findAll", query = "SELECT p FROM Profit p")
    , @NamedQuery(name = "Profit.findByCantidad", query = "SELECT p FROM Profit p WHERE p.cantidad = :cantidad")
    , @NamedQuery(name = "Profit.findById", query = "SELECT p FROM Profit p WHERE p.id = :id")})
public class Profit implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigInteger cantidad;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Integer id;
    @JoinColumn(name = "ID_DENOMINATION", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Denomination idDenomination;
    @JoinColumn(name = "ID_USUARIO", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Userpurse idUsuario;

    public Profit() {
    }

    public Profit(Integer id) {
        this.id = id;
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

    public Denomination getIdDenomination() {
        return idDenomination;
    }

    public void setIdDenomination(Denomination idDenomination) {
        this.idDenomination = idDenomination;
    }

    public Userpurse getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Userpurse idUsuario) {
        this.idUsuario = idUsuario;
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
        if (!(object instanceof Profit)) {
            return false;
        }
        Profit other = (Profit) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "co.ms.entity.Profit[ id=" + id + " ]";
    }
    
}
