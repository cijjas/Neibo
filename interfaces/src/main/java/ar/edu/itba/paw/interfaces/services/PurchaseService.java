package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Purchase;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PurchaseService {

    Purchase createPurchase(long productId, long userId, long unitsBought);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Purchase> findPurchase(long purchaseId);

    Optional<Purchase> findPurchase(long purchaseId, long userId, long neighborhoodId);

    List<Purchase> getPurchases(long userId, String typeURN, int page, int size, long neighborhoodId);

    int countPurchases(long userId, String typeURN);

    int calculatePurchasePages(long userId, String typeURN, int size);
}
