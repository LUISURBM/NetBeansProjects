/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.test.bean;

import co.test.entity.Coin;
import co.test.entity.Usercoin;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author ADMIN
 */
@Stateless
public class UsercoinFacade extends AbstractFacade<Usercoin> {

    @PersistenceContext(unitName = "MarranitoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsercoinFacade() {
        super(Usercoin.class);
    }

    public Usercoin findUserByCredentials(String username, String password) {
        Query q = getEntityManager().createNamedQuery("Usercoin.findByCredentials", Usercoin.class);
        Usercoin user = null;

        try {
            user = (Usercoin) q.setParameter("username", username).setParameter("password", password).getSingleResult();
        } catch (ClassCastException | NoResultException cce) {

        }
        return user;
    }

    public List<Coin> findRangeByUser(int[] range, String username) {
        Query q = getEntityManager().createNamedQuery("Coin.findByUserId", Usercoin.class);
        List<Coin> userCoin = null;
        q.setMaxResults(range[1] - range[0] + 1);
        q.setFirstResult(range[0]);

        try {
            userCoin = q.setParameter("id", username).getResultList();
        } catch (ClassCastException | NoResultException cce) {

        }
        return userCoin;
    }
//
//    public List<Coin> findRangeByUser(int[] range, String username) {
//        Query q = getEntityManager().createNativeQuery("SELECT c.usuario, c.valor, c.cantidad, c.id FROM Coin c where c.usuario = '" + username + "'");
//        List<Object[]> coins = q.getResultList();
//        List<Coin> monedas = new ArrayList<Coin>();
//        for (Object[] a : coins) {
//            System.out.println("Coin "
//                    + a[3]
//                    + " "
//                    + a[1]);
//            Coin c = new Coin(Integer.parseInt(a[3].toString()));
//            c.setCantidad(new BigInteger(a[2].toString()));
//            c.setValor(new BigInteger(a[1].toString()));
//            c.setUsuario(a[0].toString());
//            monedas.add(c);
//        }
//        return monedas;
//    }

}
