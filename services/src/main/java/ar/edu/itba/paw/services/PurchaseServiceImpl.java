package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.TransactionType;
import ar.edu.itba.paw.interfaces.persistence.PurchaseDao;
import ar.edu.itba.paw.interfaces.services.PurchaseService;
import ar.edu.itba.paw.models.Entities.Purchase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseServiceImpl.class);
    private final PurchaseDao purchaseDao;

    @Autowired
    public PurchaseServiceImpl(PurchaseDao purchaseDao) {
        this.purchaseDao = purchaseDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Purchase createPurchase(long productId, long userId, long unitsBought) {

        return purchaseDao.createPurchase(productId, userId, unitsBought);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Optional<Purchase> findPurchase(long purchaseId) {

        ValidationUtils.checkPurchaseId(purchaseId);

        return  purchaseDao.findPurchase(purchaseId);
    }

    @Override
    public Set<Purchase> getPurchases(long userId, String transactionType, int page, int size) {

        ValidationUtils.checkUserId(userId);
        ValidationUtils.checkPageAndSize(page, size);
        ValidationUtils.checkTransactionTypeString(transactionType);

        return purchaseDao.getPurchases(userId, transactionType, page, size);
    }

    @Override
    public int countPurchases(long userId, String transactionType) {
        ValidationUtils.checkUserId(userId);
        ValidationUtils.checkTransactionTypeString(transactionType);

        return purchaseDao.countPurchases(userId, transactionType);
    }

    @Override
    public int calculatePurchasePages(long userId, String transactionType, int size) {

        ValidationUtils.checkUserId(userId);
        ValidationUtils.checkTransactionTypeString(transactionType);
        ValidationUtils.checkSize(size);

        return PaginationUtils.calculatePages(purchaseDao.countPurchases(userId, transactionType), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

}
