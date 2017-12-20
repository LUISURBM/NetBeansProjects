/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.ms.bean;

import co.ms.entity.Denomination;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author ADMIN
 */
@Stateless
public class DenominationFacade extends AbstractFacade<Denomination> {

    @PersistenceContext(unitName = "co.ms.entity_Purse_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DenominationFacade() {
        super(Denomination.class);
    }
    
}
