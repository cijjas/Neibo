package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Purchase;

import java.util.Optional;
import java.util.Set;

public interface PurchaseService {

    Purchase createPurchase(long productId, long userId, long unitsBought);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Purchase> findPurchase(long purchaseId);

    Set<Purchase> getPurchasesByCriteria(long userId, String type, int page, int size);

    int getPurchasesByCriteriaCount(long userId, String type);

    int getTotalPurchasesPages(long userId, String type, int size);
}
