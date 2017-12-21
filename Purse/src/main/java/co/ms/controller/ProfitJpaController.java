/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.ms.controller;

import co.ms.controller.exceptions.NonexistentEntityException;
import co.ms.controller.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import co.ms.entity.Denomination;
import co.ms.entity.Profit;
import co.ms.entity.Userpurse;
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
public class ProfitJpaController implements Serializable {

    public ProfitJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
//    @PersistenceUnit(unitName="co.ms.entity_Purse_war_1.0-SNAPSHOTPU")
    private EntityManagerFactory emf = null;
//    @Resource
    private UserTransaction utx = null;

    public ProfitJpaController() {
        try {
            InitialContext ic = new InitialContext();
            this.utx = (UserTransaction) ic.lookup("java:comp/UserTransaction");
            this.emf = javax.persistence.Persistence.createEntityManagerFactory("co.ms.entity_Purse_war_1.0-SNAPSHOTPU");
        } catch (NamingException ex) {
            Logger.getLogger(ProfitJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Profit profit) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Denomination idDenomination = profit.getIdDenomination();
            if (idDenomination != null) {
                idDenomination = em.getReference(idDenomination.getClass(), idDenomination.getId());
                profit.setIdDenomination(idDenomination);
            }
            Userpurse idUsuario = profit.getIdUsuario();
            if (idUsuario != null) {
                idUsuario = em.getReference(idUsuario.getClass(), idUsuario.getId());
                profit.setIdUsuario(idUsuario);
            }
            em.persist(profit);
            if (idDenomination != null) {
                idDenomination.getProfitList().add(profit);
                idDenomination = em.merge(idDenomination);
            }
            if (idUsuario != null) {
                idUsuario.getProfitList().add(profit);
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

    public void edit(Profit profit) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Profit persistentProfit = em.find(Profit.class, profit.getId());
            Denomination idDenominationOld = persistentProfit.getIdDenomination();
            Denomination idDenominationNew = profit.getIdDenomination();
            Userpurse idUsuarioOld = persistentProfit.getIdUsuario();
            Userpurse idUsuarioNew = profit.getIdUsuario();
            if (idDenominationNew != null) {
                idDenominationNew = em.getReference(idDenominationNew.getClass(), idDenominationNew.getId());
                profit.setIdDenomination(idDenominationNew);
            }
            if (idUsuarioNew != null) {
                idUsuarioNew = em.getReference(idUsuarioNew.getClass(), idUsuarioNew.getId());
                profit.setIdUsuario(idUsuarioNew);
            }
            profit = em.merge(profit);
            if (idDenominationOld != null && !idDenominationOld.equals(idDenominationNew)) {
                idDenominationOld.getProfitList().remove(profit);
                idDenominationOld = em.merge(idDenominationOld);
            }
            if (idDenominationNew != null && !idDenominationNew.equals(idDenominationOld)) {
                idDenominationNew.getProfitList().add(profit);
                idDenominationNew = em.merge(idDenominationNew);
            }
            if (idUsuarioOld != null && !idUsuarioOld.equals(idUsuarioNew)) {
                idUsuarioOld.getProfitList().remove(profit);
                idUsuarioOld = em.merge(idUsuarioOld);
            }
            if (idUsuarioNew != null && !idUsuarioNew.equals(idUsuarioOld)) {
                idUsuarioNew.getProfitList().add(profit);
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
                Integer id = profit.getId();
                if (findProfit(id) == null) {
                    throw new NonexistentEntityException("The profit with id " + id + " no longer exists.");
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
            Profit profit;
            try {
                profit = em.getReference(Profit.class, id);
                profit.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The profit with id " + id + " no longer exists.", enfe);
            }
            Denomination idDenomination = profit.getIdDenomination();
            if (idDenomination != null) {
                idDenomination.getProfitList().remove(profit);
                idDenomination = em.merge(idDenomination);
            }
            Userpurse idUsuario = profit.getIdUsuario();
            if (idUsuario != null) {
                idUsuario.getProfitList().remove(profit);
                idUsuario = em.merge(idUsuario);
            }
            em.remove(profit);
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

    public List<Profit> findProfitEntities() {
        return findProfitEntities(true, -1, -1);
    }

    public List<Profit> findProfitEntities(int maxResults, int firstResult) {
        return findProfitEntities(false, maxResults, firstResult);
    }

    private List<Profit> findProfitEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Profit.class));
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

    public Profit findProfit(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Profit.class, id);
        } finally {
            em.close();
        }
    }

    public int getProfitCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Profit> rt = cq.from(Profit.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
