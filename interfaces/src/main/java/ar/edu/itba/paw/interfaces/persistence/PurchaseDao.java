package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.enums.TransactionType;
import ar.edu.itba.paw.models.Entities.Purchase;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PurchaseDao {

    // -------------------------------------------- PURCHASES INSERT ---------------------------------------------------

    Purchase createPurchase(long productId, long userId, long unitsBought);

    // -------------------------------------------- PURCHASES SELECT ---------------------------------------------------

    Optional<Purchase> findPurchase(long purchaseId);

    Optional<Purchase> findPurchase(long purchaseId, long userId, long neighborhoodId);

    List<Purchase> getPurchases(long userId, Long transactionTypeId, int page, int size);

    int countPurchases(long userId, Long transactionTypeId);
}
