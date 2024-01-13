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
    public Set<Purchase> getPurchasesBySellerId(long userId, int page, int size) {

        ValidationUtils.checkUserId(userId);
        ValidationUtils.checkPageAndSize(page, size);

        return  purchaseDao.getPurchasesBySellerId(userId, page, size);
    }

    @Override
    public int getPurchasesCountBySellerId(long userId) {

        ValidationUtils.checkUserId(userId);

        return purchaseDao.getPurchasesCountBySellerId(userId);
    }

    @Override
    public Set<Purchase> getPurchasesByBuyerId(long userId, int page, int size) {

        ValidationUtils.checkUserId(userId);
        ValidationUtils.checkPageAndSize(page, size);

        return purchaseDao.getPurchasesByBuyerId(userId, page, size);
    }

    @Override
    public int getPurchasesCountByBuyerId(long userId) {

        ValidationUtils.checkUserId(userId);

        return purchaseDao.getPurchasesCountByBuyerId(userId);
    }

    // In PurchaseService

    @Override
    public Set<Purchase> getPurchasesByType(long userId, String type, int page, int size) {

        ValidationUtils.checkUserId(userId);
        ValidationUtils.checkPageAndSize(page, size);

        if ("purchase".equalsIgnoreCase(type)) {
            return getPurchasesByBuyerId(userId, page, size);
        } else if ("sale".equalsIgnoreCase(type)) {
            return getPurchasesBySellerId(userId, page, size);
        } else {
            // You might throw an exception or return an empty set, depending on your requirements
            throw new IllegalArgumentException("Invalid 'type' parameter");
        }
    }

    @Override
    public int getTotalPurchasesPages(long sellerId, long buyerId, int size) {

        ValidationUtils.checkBuyerId(buyerId);
        ValidationUtils.checkSellerId(sellerId);
        ValidationUtils.checkSize(size);

        if (sellerId != 0) {
            return calculatePages(getPurchasesCountBySellerId(sellerId), size);
        } else if (buyerId != 0) {
            return calculatePages(getPurchasesCountByBuyerId(buyerId), size);
        } else {
            //throw something
            return 0;
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    private int calculatePages(int totalItems, int size) {
        return (int) Math.ceil((double) totalItems / size);
    }
}
