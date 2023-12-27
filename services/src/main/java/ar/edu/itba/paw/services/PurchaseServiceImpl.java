package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.PurchaseDao;
import ar.edu.itba.paw.interfaces.services.PurchaseService;
import ar.edu.itba.paw.models.Entities.Purchase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
    @Override
    public Purchase createPurchase(long productId, long userId, long unitsBought) {
        return purchaseDao.createPurchase(productId, userId, unitsBought);
    }

    @Override
    public Optional<Purchase> findPurchase(long purchaseId) {
        return  purchaseDao.findPurchase(purchaseId);
    }

    @Override
    public Set<Purchase> getPurchasesBySellerId(long userId, int page, int size) {
        return  purchaseDao.getPurchasesBySellerId(userId, page, size);
    }

    @Override
    public int getPurchasesCountBySellerId(long userId) {
        return  purchaseDao.getPurchasesCountBySellerId(userId);
    }

    @Override
    public Set<Purchase> getPurchasesByBuyerId(long userId, int page, int size) {
        return purchaseDao.getPurchasesByBuyerId(userId, page, size);
    }

    @Override
    public int getPurchasesCountByBuyerId(long userId) {
        return purchaseDao.getPurchasesCountByBuyerId(userId);
    }

    // In PurchaseService

    @Override
    public Set<Purchase> getPurchasesByCriteria(long sellerId, long buyerId, int page, int size) {
        if (sellerId != 0) {
            return getPurchasesBySellerId(sellerId, page, size);
        } else if (buyerId != 0) {
            return getPurchasesByBuyerId(buyerId, page, size);
        } else {
            // throw something
            return Collections.emptySet();
        }
    }

    @Override
    public int getTotalPurchasesPages(long sellerId, long buyerId, int size) {
        if (sellerId != 0) {
            return calculatePages(getPurchasesCountBySellerId(sellerId), size);
        } else if (buyerId != 0) {
            return calculatePages(getPurchasesCountByBuyerId(buyerId), size);
        } else {
            //throw something
            return 0;
        }
    }

    private int calculatePages(int totalItems, int size) {
        return (int) Math.ceil((double) totalItems / size);
    }
}
