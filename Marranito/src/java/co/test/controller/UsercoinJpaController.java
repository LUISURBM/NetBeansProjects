/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.test.controller;

import co.test.controller.exceptions.IllegalOrphanException;
import co.test.controller.exceptions.NonexistentEntityException;
import co.test.controller.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import co.test.entity.Coin;
import co.test.entity.Usercoin;
import java.util.ArrayList;
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
public class UsercoinJpaController implements Serializable {

    public UsercoinJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    @PersistenceUnit(unitName="MarranitoPU")
    private EntityManagerFactory emf = null;
    @Resource
    private UserTransaction utx = null;

    public UsercoinJpaController() {
        try {
            InitialContext ic = new InitialContext();
            this.utx = (UserTransaction) ic.lookup("java:comp/UserTransaction");
            this.emf = javax.persistence.Persistence.createEntityManagerFactory("MarranitoPU");
        } catch (NamingException ex) {
            Logger.getLogger(co.test.controller.UsercoinJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usercoin usercoin) throws RollbackFailureException, Exception {
        if (usercoin.getCoinList() == null) {
            usercoin.setCoinList(new ArrayList<Coin>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Coin> attachedCoinList = new ArrayList<Coin>();
            for (Coin coinListCoinToAttach : usercoin.getCoinList()) {
                coinListCoinToAttach = em.getReference(coinListCoinToAttach.getClass(), coinListCoinToAttach.getId());
                attachedCoinList.add(coinListCoinToAttach);
            }
            usercoin.setCoinList(attachedCoinList);
            em.persist(usercoin);
            for (Coin coinListCoin : usercoin.getCoinList()) {
                Usercoin oldIdUsuarioOfCoinListCoin = coinListCoin.getIdUsuario();
                coinListCoin.setIdUsuario(usercoin);
                coinListCoin = em.merge(coinListCoin);
                if (oldIdUsuarioOfCoinListCoin != null) {
                    oldIdUsuarioOfCoinListCoin.getCoinList().remove(coinListCoin);
                    oldIdUsuarioOfCoinListCoin = em.merge(oldIdUsuarioOfCoinListCoin);
                }
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

    public void edit(Usercoin usercoin) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Usercoin persistentUsercoin = em.find(Usercoin.class, usercoin.getId());
            List<Coin> coinListOld = persistentUsercoin.getCoinList();
            List<Coin> coinListNew = usercoin.getCoinList();
            List<String> illegalOrphanMessages = null;
            for (Coin coinListOldCoin : coinListOld) {
                if (!coinListNew.contains(coinListOldCoin)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Coin " + coinListOldCoin + " since its idUsuario field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Coin> attachedCoinListNew = new ArrayList<Coin>();
            for (Coin coinListNewCoinToAttach : coinListNew) {
                coinListNewCoinToAttach = em.getReference(coinListNewCoinToAttach.getClass(), coinListNewCoinToAttach.getId());
                attachedCoinListNew.add(coinListNewCoinToAttach);
            }
            coinListNew = attachedCoinListNew;
            usercoin.setCoinList(coinListNew);
            usercoin = em.merge(usercoin);
            for (Coin coinListNewCoin : coinListNew) {
                if (!coinListOld.contains(coinListNewCoin)) {
                    Usercoin oldIdUsuarioOfCoinListNewCoin = coinListNewCoin.getIdUsuario();
                    coinListNewCoin.setIdUsuario(usercoin);
                    coinListNewCoin = em.merge(coinListNewCoin);
                    if (oldIdUsuarioOfCoinListNewCoin != null && !oldIdUsuarioOfCoinListNewCoin.equals(usercoin)) {
                        oldIdUsuarioOfCoinListNewCoin.getCoinList().remove(coinListNewCoin);
                        oldIdUsuarioOfCoinListNewCoin = em.merge(oldIdUsuarioOfCoinListNewCoin);
                    }
                }
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
                Integer id = usercoin.getId();
                if (findUsercoin(id) == null) {
                    throw new NonexistentEntityException("The usercoin with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Usercoin usercoin;
            try {
                usercoin = em.getReference(Usercoin.class, id);
                usercoin.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usercoin with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Coin> coinListOrphanCheck = usercoin.getCoinList();
            for (Coin coinListOrphanCheckCoin : coinListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usercoin (" + usercoin + ") cannot be destroyed since the Coin " + coinListOrphanCheckCoin + " in its coinList field has a non-nullable idUsuario field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(usercoin);
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

    public List<Usercoin> findUsercoinEntities() {
        return findUsercoinEntities(true, -1, -1);
    }

    public List<Usercoin> findUsercoinEntities(int maxResults, int firstResult) {
        return findUsercoinEntities(false, maxResults, firstResult);
    }

    private List<Usercoin> findUsercoinEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usercoin.class));
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

    public Usercoin findUsercoin(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usercoin.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsercoinCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usercoin> rt = cq.from(Usercoin.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
