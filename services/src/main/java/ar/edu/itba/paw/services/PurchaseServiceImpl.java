package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ProfessionWorkerDao;
import ar.edu.itba.paw.interfaces.persistence.PurchaseDao;
import ar.edu.itba.paw.interfaces.services.PurchaseService;
import ar.edu.itba.paw.models.JunctionEntities.Purchase;
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
}
