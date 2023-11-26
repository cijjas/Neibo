package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.interfaces.persistence.PurchaseDao;
import ar.edu.itba.paw.models.JunctionEntities.Purchase;
import ar.edu.itba.paw.models.JunctionEntities.Request;
import ar.edu.itba.paw.models.MainEntities.Product;
import ar.edu.itba.paw.models.MainEntities.Review;
import ar.edu.itba.paw.models.MainEntities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

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
}
