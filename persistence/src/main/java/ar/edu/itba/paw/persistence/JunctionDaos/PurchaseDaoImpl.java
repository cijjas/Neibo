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
import javax.persistence.Query;
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
        LOGGER.debug("Selecting Purchase {}", purchaseId);

        return Optional.ofNullable(em.find(Purchase.class, purchaseId));
    }

    @Override
    public Optional<Purchase> findPurchase(long purchaseId, long userId, long neighborhoodId) {
        LOGGER.debug("Selecting Purchase with purchaseId {}, userId {}, neighborhoodId {}", purchaseId, userId, neighborhoodId);

        TypedQuery<Purchase> query = em.createQuery(
                "SELECT p FROM Purchase p WHERE p.id = :purchaseId AND p.user.id = :userId AND p.product.seller.neighborhood.id = :neighborhoodId",
                Purchase.class
        );

        query.setParameter("purchaseId", purchaseId);
        query.setParameter("userId", userId);
        query.setParameter("neighborhoodId", neighborhoodId);

        List<Purchase> result = query.getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<Purchase> getPurchases(long userId, Long transactionTypeId, int page, int size) {
        LOGGER.debug("Selecting Purchases By Criteria For User {} for Transaction Type {}", userId, transactionTypeId);

        StringBuilder queryStringBuilder = new StringBuilder();
        queryStringBuilder.append("SELECT p.* ")
                .append("FROM products_users_purchases p ")
                .append("JOIN products pr ON p.productid = pr.productid ")
                .append("JOIN users u ON p.userid = u.userid ");

        if (transactionTypeId != null) {
            TransactionType tType = TransactionType.fromId(transactionTypeId);

            switch (tType) {
                case PURCHASE:
                    queryStringBuilder.append("WHERE p.userid = :userId ");
                    break;
                case SALE:
                    queryStringBuilder.append("WHERE pr.sellerid = :userId ");
                    break;
            }
        } else {
            queryStringBuilder.append("WHERE p.userid = :userId OR pr.sellerid = :userId ");
        }

        queryStringBuilder.append("ORDER BY p.purchasedate DESC ")
                .append("LIMIT :size OFFSET :offset");

        String queryString = queryStringBuilder.toString();

        Query nativeQuery = em.createNativeQuery(queryString, Purchase.class);
        nativeQuery.setParameter("userId", userId);
        nativeQuery.setParameter("size", size);
        nativeQuery.setParameter("offset", (page - 1) * size);

        return nativeQuery.getResultList();
    }

    @Override
    public int countPurchases(long userId, Long transactionTypeId) {
        LOGGER.debug("Counting Purchases By Seller {} for Transactions of Type {}", userId, transactionTypeId);

        StringBuilder queryStringBuilder = new StringBuilder();
        queryStringBuilder.append("SELECT COUNT(p.*) ")
                .append("FROM products_users_purchases p ")
                .append("JOIN products pr ON p.productid = pr.productid ")
                .append("JOIN users u ON p.userid = u.userid ");

        if (transactionTypeId != null) {
            TransactionType tType = TransactionType.fromId(transactionTypeId);

            switch (tType) {
                case PURCHASE:
                    queryStringBuilder.append("WHERE p.userid = :userId ");
                    break;
                case SALE:
                    queryStringBuilder.append("WHERE pr.sellerid = :userId ");
                    break;
            }
        } else {
            queryStringBuilder.append("WHERE p.userid = :userId OR pr.sellerid = :userId ");
        }

        String queryString = queryStringBuilder.toString();

        Query nativeQuery = em.createNativeQuery(queryString);
        nativeQuery.setParameter("userId", userId);

        return Integer.parseInt((nativeQuery.getSingleResult()).toString());
    }

}
