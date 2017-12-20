package co.test.facade;

import co.test.entity.Coin;
import co.test.facade.util.JsfUtil;
import co.test.facade.util.PaginationHelper;
import co.test.bean.CoinFacade;
import co.test.bean.UsercoinFacade;
import co.test.entity.Usercoin;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

@Named("coinController")
@SessionScoped
public class CoinController implements Serializable {

    private Coin current;
    private Usercoin credential;

    public Usercoin getCredential() {
        if (credential == null) {
            credential = new Usercoin(null, null, null);
        }
        return credential;
    }

    public void setCredential(Usercoin credential) {
        this.credential = credential;
    }
    private DataModel items = null;
    @EJB
    private co.test.bean.CoinFacade ejbFacade;
    @EJB
    private co.test.bean.UsercoinFacade ejbFacadeUser;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private BigInteger totalCoins;
    private BigInteger totalSavings;

    public CoinController() {
        long startTime = System.nanoTime();
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Coin controller init start {0} ", new Object[]{startTime});
        credential = new Usercoin(null, null, null);
        long endTime = System.nanoTime() - startTime;
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Coin controller init end {0} ", new Object[]{endTime});
    }

    public Coin getSelected() {
        if (current == null) {
            current = new Coin();
            selectedItemIndex = -1;
        }
        return current;
    }

    private CoinFacade getFacade() {
        return ejbFacade;
    }

    public UsercoinFacade getEjbFacadeUser() {
        return ejbFacadeUser;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRangeByUser(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}, getCredential().getUsername()));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        long startTime = System.nanoTime();
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Coin controller prepareList start {0} ", new Object[]{startTime});
        recreateModel();
        long endTime = System.nanoTime() - startTime;
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Coin controller prepareList end {0} ", new Object[]{endTime});
        return "List";
    }

    public String prepareLogin() {
        long startTime = System.nanoTime();
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Coin controller prepareLogin start {0} ", new Object[]{startTime});
        boolean validCredentials = credential != null
                ? (credential.getUsername() != null
                ? (credential.getPassword() != null) : false)
                : false;
        if (validCredentials) {
            credential = getEjbFacadeUser().findUserByCredentials(credential.getUsername(), credential.getPassword());
            if (credential != null) {

            }
        }
        long endTime = System.nanoTime() - startTime;
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Coin controller prepareLogin end {0} ", new Object[]{endTime});
        return "List";
    }

    public String prepareLogout() {
        long startTime = System.nanoTime();
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Coin controller prepareLogout start {0} ", new Object[]{startTime});
        this.credential = null;
        recreatePagination();
        recreateModel();
        long endTime = System.nanoTime() - startTime;
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Coin controller prepareLogout end {0} ", new Object[]{endTime});
        return "/index";
    }

    public String prepareView() {
        current = (Coin) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Coin();
        current.setIdUsuario(credential);
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleNew").getString("CoinCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleNew").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Coin) getItems().getRowData();
        current.setIdUsuario(credential);
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleNew").getString("CoinUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleNew").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Coin) getItems().getRowData();
        current.setIdUsuario(credential);
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/BundleNew").getString("CoinDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/BundleNew").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
            current.setIdUsuario(credential);
        }
    }

    public String add() {
        long startTime = System.nanoTime();
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Coin controller add start {0} ", new Object[]{startTime});
        current = (Coin) getItems().getRowData();
        current.setCantidad(BigInteger.ONE.add(current.getCantidad()));
        current.setIdUsuario(credential);
        String toReturn = null;
        toReturn = "List";
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("CoinUpdated"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        } finally {
            long endTime = System.nanoTime() - startTime;
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Coin controller add end {0} ", new Object[]{endTime});
            return toReturn;
        }

    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public Coin getCoin(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = Coin.class)
    public static class CoinControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            CoinController controller = (CoinController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "coinController");
            return controller.getCoin(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Coin) {
                Coin o = (Coin) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Coin.class.getName());
            }
        }

    }

    public BigInteger getTotalCoins() {
        totalSavings = BigInteger.ZERO;
        totalCoins = BigInteger.ZERO;
        for (Object item : getItems()) {
            Coin c = (Coin) item;
            totalSavings = totalSavings.add(c.getCantidad().multiply(c.getValor()));
            totalCoins = totalCoins.add(c.getCantidad());
        }
        return totalCoins;
    }

    public void setTotalCoins(BigInteger totalCoins) {
        this.totalCoins = totalCoins;
    }

    public BigInteger getTotalSavings() {
        return totalSavings;
    }

    public void setTotalSavings(BigInteger totalSavings) {
        this.totalSavings = totalSavings;
    }

}
