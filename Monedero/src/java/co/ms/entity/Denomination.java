/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.ms.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author ADMIN
 */
@Entity
@Table(catalog = "", schema = "APP")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Denomination.findAll", query = "SELECT d FROM Denomination d")
    , @NamedQuery(name = "Denomination.findByValor", query = "SELECT d FROM Denomination d WHERE d.valor = :valor")
    , @NamedQuery(name = "Denomination.findByDnmType", query = "SELECT d FROM Denomination d WHERE d.dnmType = :dnmType")
    , @NamedQuery(name = "Denomination.findById", query = "SELECT d FROM Denomination d WHERE d.id = :id")})
public class Denomination implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigInteger valor;
    @Size(max = 7)
    @Column(name = "DNM_TYPE", length = 7)
    private String dnmType;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Integer id;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idDenomination", fetch = FetchType.LAZY)
    private List<Profit> profitList;

    public Denomination() {
    }

    public Denomination(Integer id) {
        this.id = id;
    }

    public BigInteger getValor() {
        return valor;
    }

    public void setValor(BigInteger valor) {
        this.valor = valor;
    }

    public String getDnmType() {
        return dnmType;
    }

    public void setDnmType(String dnmType) {
        this.dnmType = dnmType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @XmlTransient
    public List<Profit> getProfitList() {
        return profitList;
    }

    public void setProfitList(List<Profit> profitList) {
        this.profitList = profitList;
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
        if (!(object instanceof Denomination)) {
            return false;
        }
        Denomination other = (Denomination) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "co.ms.entity.Denomination[ id=" + id + " ]";
    }
    
}
