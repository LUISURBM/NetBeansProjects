/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.ms.bean;

import co.ms.entity.Userpurse;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author ADMIN
 */
@Stateless
public class UserpurseFacade extends AbstractFacade<Userpurse> {

    @PersistenceContext(unitName = "MonederoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UserpurseFacade() {
        super(Userpurse.class);
    }
    
}
