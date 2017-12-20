/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.test.controller;

import co.test.controller.exceptions.NonexistentEntityException;
import co.test.controller.exceptions.RollbackFailureException;
import co.test.entity.Coin;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import co.test.entity.Usercoin;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;

/**
 *
 * @author ADMIN
 */
public class CoinJpaController implements Serializable {

    public CoinJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    @PersistenceUnit(unitName="MarranitoPU")
    private EntityManagerFactory emf = null;
    @Resource
    private UserTransaction utx = null;

    public CoinJpaController() {
        try {
            InitialContext ic = new InitialContext();
            this.utx = (UserTransaction) ic.lookup("java:comp/UserTransaction");
            this.emf = javax.persistence.Persistence.createEntityManagerFactory("MarranitoPU");
        } catch (NamingException ex) {
            Logger.getLogger(co.test.controller.CoinJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Coin coin) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Usercoin idUsuario = coin.getIdUsuario();
            if (idUsuario != null) {
                idUsuario = em.getReference(idUsuario.getClass(), idUsuario.getId());
                coin.setIdUsuario(idUsuario);
            }
            em.persist(coin);
            if (idUsuario != null) {
                idUsuario.getCoinList().add(coin);
                idUsuario = em.merge(idUsuario);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Coin coin) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Coin persistentCoin = em.find(Coin.class, coin.getId());
            Usercoin idUsuarioOld = persistentCoin.getIdUsuario();
            Usercoin idUsuarioNew = coin.getIdUsuario();
            if (idUsuarioNew != null) {
                idUsuarioNew = em.getReference(idUsuarioNew.getClass(), idUsuarioNew.getId());
                coin.setIdUsuario(idUsuarioNew);
            }
            coin = em.merge(coin);
            if (idUsuarioOld != null && !idUsuarioOld.equals(idUsuarioNew)) {
                idUsuarioOld.getCoinList().remove(coin);
                idUsuarioOld = em.merge(idUsuarioOld);
            }
            if (idUsuarioNew != null && !idUsuarioNew.equals(idUsuarioOld)) {
                idUsuarioNew.getCoinList().add(coin);
                idUsuarioNew = em.merge(idUsuarioNew);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = coin.getId();
                if (findCoin(id) == null) {
                    throw new NonexistentEntityException("The coin with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Coin coin;
            try {
                coin = em.getReference(Coin.class, id);
                coin.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The coin with id " + id + " no longer exists.", enfe);
            }
            Usercoin idUsuario = coin.getIdUsuario();
            if (idUsuario != null) {
                idUsuario.getCoinList().remove(coin);
                idUsuario = em.merge(idUsuario);
            }
            em.remove(coin);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Coin> findCoinEntities() {
        return findCoinEntities(true, -1, -1);
    }

    public List<Coin> findCoinEntities(int maxResults, int firstResult) {
        return findCoinEntities(false, maxResults, firstResult);
    }

    private List<Coin> findCoinEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Coin.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Coin findCoin(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Coin.class, id);
        } finally {
            em.close();
        }
    }

    public int getCoinCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Coin> rt = cq.from(Coin.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
