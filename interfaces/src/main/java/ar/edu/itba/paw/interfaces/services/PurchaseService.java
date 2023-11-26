package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.JunctionEntities.Purchase;

import java.util.Optional;
import java.util.Set;

public interface PurchaseService {

    Purchase createPurchase(long productId, long userId, long unitsBought);

    Optional<Purchase> findPurchase(long purchaseId);

    Set<Purchase> getPurchasesBySellerId(long userId, int page, int size);

    int getPurchasesCountBySellerId(long userId);

    Set<Purchase> getPurchasesByBuyerId(long userId, int page, int size);

    int getPurchasesCountByBuyerId(long userId);
}
