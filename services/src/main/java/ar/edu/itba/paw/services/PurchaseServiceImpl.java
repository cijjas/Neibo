package ar.edu.itba.paw.services;

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
    public Set<Purchase> getPurchases(long userId, String type, int page, int size) {

        ValidationUtils.checkUserId(userId);
        ValidationUtils.checkPageAndSize(page, size);

        return purchaseDao.getPurchases(userId, type, page, size);
    }

    @Override
    public int countPurchases(long userId, String type) {
        ValidationUtils.checkUserId(userId);

        return purchaseDao.countPurchases(userId, type);
    }

    @Override
    public int calculatePurchasePages(long userId, String type, int size) {

        ValidationUtils.checkId(userId, "User");
        ValidationUtils.checkSize(size);

        return PaginationUtils.calculatePages(countPurchases(userId, type), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

}
