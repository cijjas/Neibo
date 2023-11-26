package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.JunctionEntities.Purchase;

import java.util.Optional;

public interface PurchaseDao {

    Purchase createPurchase(long productId, long userId, long unitsBought);

    Optional<Purchase> findPurchase(long purchaseId);
}
