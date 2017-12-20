/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.ms.controller;

import co.ms.controller.exceptions.IllegalOrphanException;
import co.ms.controller.exceptions.NonexistentEntityException;
import co.ms.controller.exceptions.RollbackFailureException;
import co.ms.entity.Denomination;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import co.ms.entity.Profit;
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
public class DenominationJpaController implements Serializable {

    public DenominationJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    @PersistenceUnit(unitName="co.ms.entity_Purse_war_1.0-SNAPSHOTPU")
    private EntityManagerFactory emf = null;
    @Resource
    private UserTransaction utx = null;

    public DenominationJpaController() {
        try {
            InitialContext ic = new InitialContext();
            this.utx = (UserTransaction) ic.lookup("java:comp/UserTransaction");
            this.emf = javax.persistence.Persistence.createEntityManagerFactory("co.ms.entity_Purse_war_1.0-SNAPSHOTPU");
        } catch (NamingException ex) {
            Logger.getLogger(DenominationJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Denomination denomination) throws RollbackFailureException, Exception {
        if (denomination.getProfitList() == null) {
            denomination.setProfitList(new ArrayList<Profit>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Profit> attachedProfitList = new ArrayList<Profit>();
            for (Profit profitListProfitToAttach : denomination.getProfitList()) {
                profitListProfitToAttach = em.getReference(profitListProfitToAttach.getClass(), profitListProfitToAttach.getId());
                attachedProfitList.add(profitListProfitToAttach);
            }
            denomination.setProfitList(attachedProfitList);
            em.persist(denomination);
            for (Profit profitListProfit : denomination.getProfitList()) {
                Denomination oldIdDenominationOfProfitListProfit = profitListProfit.getIdDenomination();
                profitListProfit.setIdDenomination(denomination);
                profitListProfit = em.merge(profitListProfit);
                if (oldIdDenominationOfProfitListProfit != null) {
                    oldIdDenominationOfProfitListProfit.getProfitList().remove(profitListProfit);
                    oldIdDenominationOfProfitListProfit = em.merge(oldIdDenominationOfProfitListProfit);
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

    public void edit(Denomination denomination) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Denomination persistentDenomination = em.find(Denomination.class, denomination.getId());
            List<Profit> profitListOld = persistentDenomination.getProfitList();
            List<Profit> profitListNew = denomination.getProfitList();
            List<String> illegalOrphanMessages = null;
            for (Profit profitListOldProfit : profitListOld) {
                if (!profitListNew.contains(profitListOldProfit)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Profit " + profitListOldProfit + " since its idDenomination field is not nullable.");
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
            denomination.setProfitList(profitListNew);
            denomination = em.merge(denomination);
            for (Profit profitListNewProfit : profitListNew) {
                if (!profitListOld.contains(profitListNewProfit)) {
                    Denomination oldIdDenominationOfProfitListNewProfit = profitListNewProfit.getIdDenomination();
                    profitListNewProfit.setIdDenomination(denomination);
                    profitListNewProfit = em.merge(profitListNewProfit);
                    if (oldIdDenominationOfProfitListNewProfit != null && !oldIdDenominationOfProfitListNewProfit.equals(denomination)) {
                        oldIdDenominationOfProfitListNewProfit.getProfitList().remove(profitListNewProfit);
                        oldIdDenominationOfProfitListNewProfit = em.merge(oldIdDenominationOfProfitListNewProfit);
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
                Integer id = denomination.getId();
                if (findDenomination(id) == null) {
                    throw new NonexistentEntityException("The denomination with id " + id + " no longer exists.");
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
            Denomination denomination;
            try {
                denomination = em.getReference(Denomination.class, id);
                denomination.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The denomination with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Profit> profitListOrphanCheck = denomination.getProfitList();
            for (Profit profitListOrphanCheckProfit : profitListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Denomination (" + denomination + ") cannot be destroyed since the Profit " + profitListOrphanCheckProfit + " in its profitList field has a non-nullable idDenomination field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(denomination);
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

    public List<Denomination> findDenominationEntities() {
        return findDenominationEntities(true, -1, -1);
    }

    public List<Denomination> findDenominationEntities(int maxResults, int firstResult) {
        return findDenominationEntities(false, maxResults, firstResult);
    }

    private List<Denomination> findDenominationEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Denomination.class));
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

    public Denomination findDenomination(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Denomination.class, id);
        } finally {
            em.close();
        }
    }

    public int getDenominationCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Denomination> rt = cq.from(Denomination.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
