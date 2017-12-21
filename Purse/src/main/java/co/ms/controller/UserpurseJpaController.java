/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.ms.controller;

import co.ms.controller.exceptions.IllegalOrphanException;
import co.ms.controller.exceptions.NonexistentEntityException;
import co.ms.controller.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import co.ms.entity.Profit;
import co.ms.entity.Userpurse;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;
import org.springframework.stereotype.Service;

/**
 *
 * @author ADMIN
 */
@Service("customUserDetailsService")
public class UserpurseJpaController implements Serializable {

    public UserpurseJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
//    @PersistenceUnit(unitName="co.ms.entity_Purse_war_1.0-SNAPSHOTPU")
    private EntityManagerFactory emf = null;
//    @Resource
    private UserTransaction utx = null;

    public UserpurseJpaController() {
        try {
            InitialContext ic = new InitialContext();
            this.utx = (UserTransaction) ic.lookup("java:comp/UserTransaction");
            this.emf = javax.persistence.Persistence.createEntityManagerFactory("co.ms.entity_Purse_war_1.0-SNAPSHOTPU");
        } catch (NamingException ex) {
            Logger.getLogger(UserpurseJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Userpurse userpurse) throws RollbackFailureException, Exception {
        if (userpurse.getProfitList() == null) {
            userpurse.setProfitList(new ArrayList<Profit>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Profit> attachedProfitList = new ArrayList<Profit>();
            for (Profit profitListProfitToAttach : userpurse.getProfitList()) {
                profitListProfitToAttach = em.getReference(profitListProfitToAttach.getClass(), profitListProfitToAttach.getId());
                attachedProfitList.add(profitListProfitToAttach);
            }
            userpurse.setProfitList(attachedProfitList);
            em.persist(userpurse);
            for (Profit profitListProfit : userpurse.getProfitList()) {
                Userpurse oldIdUsuarioOfProfitListProfit = profitListProfit.getIdUsuario();
                profitListProfit.setIdUsuario(userpurse);
                profitListProfit = em.merge(profitListProfit);
                if (oldIdUsuarioOfProfitListProfit != null) {
                    oldIdUsuarioOfProfitListProfit.getProfitList().remove(profitListProfit);
                    oldIdUsuarioOfProfitListProfit = em.merge(oldIdUsuarioOfProfitListProfit);
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

    public void edit(Userpurse userpurse) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Userpurse persistentUserpurse = em.find(Userpurse.class, userpurse.getId());
            List<Profit> profitListOld = persistentUserpurse.getProfitList();
            List<Profit> profitListNew = userpurse.getProfitList();
            List<String> illegalOrphanMessages = null;
            for (Profit profitListOldProfit : profitListOld) {
                if (!profitListNew.contains(profitListOldProfit)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Profit " + profitListOldProfit + " since its idUsuario field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Profit> attachedProfitListNew = new ArrayList<Profit>();
            for (Profit profitListNewProfitToAttach : profitListNew) {
                profitListNewProfitToAttach = em.getReference(profitListNewProfitToAttach.getClass(), profitListNewProfitToAttach.getId());
                attachedProfitListNew.add(profitListNewProfitToAttach);
            }
            profitListNew = attachedProfitListNew;
            userpurse.setProfitList(profitListNew);
            userpurse = em.merge(userpurse);
            for (Profit profitListNewProfit : profitListNew) {
                if (!profitListOld.contains(profitListNewProfit)) {
                    Userpurse oldIdUsuarioOfProfitListNewProfit = profitListNewProfit.getIdUsuario();
                    profitListNewProfit.setIdUsuario(userpurse);
                    profitListNewProfit = em.merge(profitListNewProfit);
                    if (oldIdUsuarioOfProfitListNewProfit != null && !oldIdUsuarioOfProfitListNewProfit.equals(userpurse)) {
                        oldIdUsuarioOfProfitListNewProfit.getProfitList().remove(profitListNewProfit);
                        oldIdUsuarioOfProfitListNewProfit = em.merge(oldIdUsuarioOfProfitListNewProfit);
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
                Integer id = userpurse.getId();
                if (findUserpurse(id) == null) {
                    throw new NonexistentEntityException("The userpurse with id " + id + " no longer exists.");
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
            Userpurse userpurse;
            try {
                userpurse = em.getReference(Userpurse.class, id);
                userpurse.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The userpurse with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Profit> profitListOrphanCheck = userpurse.getProfitList();
            for (Profit profitListOrphanCheckProfit : profitListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Userpurse (" + userpurse + ") cannot be destroyed since the Profit " + profitListOrphanCheckProfit + " in its profitList field has a non-nullable idUsuario field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(userpurse);
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

    public List<Userpurse> findUserpurseEntities() {
        return findUserpurseEntities(true, -1, -1);
    }

    public List<Userpurse> findUserpurseEntities(int maxResults, int firstResult) {
        return findUserpurseEntities(false, maxResults, firstResult);
    }

    private List<Userpurse> findUserpurseEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Userpurse.class));
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

    public Userpurse findUserpurse(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Userpurse.class, id);
        } finally {
            em.close();
        }
    }

    public Userpurse findUserpurse(String username, String password) {
        Query q = getEntityManager().createNamedQuery("Usercoin.findByCredentials", Userpurse.class);
        Userpurse user = null;

        try {
            return (Userpurse) q.setParameter("username", username).setParameter("password", password).getSingleResult();
        } catch (ClassCastException | NoResultException cce) {

        }
        return null;
    }

    public int getUserpurseCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Userpurse> rt = cq.from(Userpurse.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
