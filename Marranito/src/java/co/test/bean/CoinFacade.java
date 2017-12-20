/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.test.bean;

import co.test.entity.Coin;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author ADMIN
 */
@Stateless
public class CoinFacade extends AbstractFacade<Coin> {

    @PersistenceContext(unitName = "MarranitoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CoinFacade() {
        super(Coin.class);
    }

    public List<Coin> findRangeByUser(int[] range, String idUsername) {
        Query q = getEntityManager().createNamedQuery("Coin.findByUserId", Coin.class);
        List<Coin> coins = q.setParameter("id", idUsername).getResultList();
        return coins;
    }

}
