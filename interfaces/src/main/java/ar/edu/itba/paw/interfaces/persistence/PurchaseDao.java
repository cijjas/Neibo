package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Purchase;

import java.util.Optional;
import java.util.Set;

public interface PurchaseDao {

    // -------------------------------------------- PURCHASES INSERT ---------------------------------------------------

    Purchase createPurchase(long productId, long userId, long unitsBought);

    // -------------------------------------------- PURCHASES SELECT ---------------------------------------------------

    Optional<Purchase> findPurchase(long purchaseId);

    Set<Purchase> getPurchases(long userId, String type, int page, int size);

    int countPurchases(long userId, String type);
}
