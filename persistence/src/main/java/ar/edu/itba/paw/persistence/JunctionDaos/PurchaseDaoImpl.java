package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.enums.TransactionType;
import ar.edu.itba.paw.interfaces.persistence.PurchaseDao;
import ar.edu.itba.paw.models.Entities.Product;
import ar.edu.itba.paw.models.Entities.Purchase;
import ar.edu.itba.paw.models.Entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;

@Repository
public class PurchaseDaoImpl implements PurchaseDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public Purchase createPurchase(long productId, long userId, long unitsBought) {
        LOGGER.debug("Inserting Purchase for product with id {} by user {}", productId, userId);
        Purchase purchase = new Purchase.Builder()
                .product(em.find(Product.class, productId))
                .user(em.find(User.class, userId))
                .units(unitsBought)
                .purchaseDate(new java.sql.Date(System.currentTimeMillis()))
                .build();
        em.persist(purchase);
        return purchase;
    }

    @Override
    public Optional<Purchase> findPurchase(long purchaseId) {
        LOGGER.debug("Selecting Purchase  {}", purchaseId);
        return Optional.ofNullable(em.find(Purchase.class, purchaseId));
    }

    @Override
    public Set<Purchase> getPurchases(long userId, TransactionType type, int page, int size) {
        LOGGER.debug("Selecting Purchases By Criteria For User {}", userId);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> idQuery = cb.createQuery(Long.class);
        Root<Purchase> purchaseRoot = idQuery.from(Purchase.class);

        Join<Purchase, Product> productJoin = purchaseRoot.join("product");
        Join<Purchase, User> userJoin = purchaseRoot.join("user");
        idQuery.select(purchaseRoot.get("purchaseId"));

        Predicate predicate;

        switch (type) {
            case purchase:
                predicate = cb.equal(userJoin.get("userId"), userId);
                break;
            case sale:
                predicate = cb.equal(productJoin.get("seller").get("userId"), userId);
                break;
            default:
                throw new IllegalArgumentException("Invalid type");
        }

        TypedQuery<Long> idTypedQuery = em.createQuery(idQuery);
        idTypedQuery.setFirstResult((page - 1) * size);
        idTypedQuery.setMaxResults(size);

        List<Long> purchaseIds = idTypedQuery.getResultList();

        if (purchaseIds.isEmpty()) {
            return Collections.emptySet();
        }

        CriteriaQuery<Purchase> purchaseQuery = cb.createQuery(Purchase.class);
        Root<Purchase> purchaseRootFetch = purchaseQuery.from(Purchase.class);

        purchaseQuery.select(purchaseRootFetch);
        purchaseQuery.where(purchaseRootFetch.get("purchaseId").in(purchaseIds));
        purchaseQuery.orderBy(cb.desc(purchaseRootFetch.get("purchaseDate")));

        TypedQuery<Purchase> purchaseTypedQuery = em.createQuery(purchaseQuery);
        List<Purchase> purchases = purchaseTypedQuery.getResultList();

        return new HashSet<>(purchases);
    }

    @Override
    public int countPurchases(long userId, String type) {
        LOGGER.debug("Counting Purchases By Seller {}", userId);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Purchase> purchaseRoot = query.from(Purchase.class);

        Join<Purchase, Product> productJoin = purchaseRoot.join("product");
        Join<Purchase, User> userJoin = purchaseRoot.join("user");

        query.select(cb.count(purchaseRoot));
        if("purchase".equalsIgnoreCase(type)) {
            query.where(cb.equal(userJoin.get("userId"), userId));
        } else if("sale".equalsIgnoreCase(type)) {
            query.where(cb.equal(productJoin.get("seller").get("userId"), userId));
        } else {
            throw new IllegalArgumentException("Invalid type");
        }

        TypedQuery<Long> typedQuery = em.createQuery(query);
        return typedQuery.getSingleResult().intValue();
    }

}
