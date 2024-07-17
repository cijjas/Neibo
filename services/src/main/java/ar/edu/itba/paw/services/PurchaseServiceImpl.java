package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.TransactionType;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.PurchaseDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.PurchaseService;
import ar.edu.itba.paw.models.Entities.Purchase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseServiceImpl.class);
    private final PurchaseDao purchaseDao;
    private final UserDao userDao;

    @Autowired
    public PurchaseServiceImpl(PurchaseDao purchaseDao, UserDao userDao) {
        this.purchaseDao = purchaseDao;
        this.userDao = userDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Purchase createPurchase(long productId, long userId, long unitsBought) {
        LOGGER.info("Creating a Purchase for Product {} for {} Units from User {}", productId, unitsBought, userId);

        return purchaseDao.createPurchase(productId, userId, unitsBought);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Optional<Purchase> findPurchase(long purchaseId) {
        LOGGER.info("Finding Purchase {}", purchaseId);

        ValidationUtils.checkPurchaseId(purchaseId);

        return purchaseDao.findPurchase(purchaseId);
    }

    @Override
    public Optional<Purchase> findPurchase(long purchaseId, long userId, long neighborhoodId) {
        LOGGER.info("Finding Purchase {} made by User {} from Neighborhood {}", purchaseId, userId, neighborhoodId);

        ValidationUtils.checkPurchaseId(purchaseId);
        ValidationUtils.checkUserId(userId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        return purchaseDao.findPurchase(purchaseId, userId, neighborhoodId);
    }

    @Override
    public List<Purchase> getPurchases(long userId, String transactionTypeURN, int page, int size, long neighborhoodId) {
        LOGGER.info("Getting Transactions of type {} made by User {} from Neighborhood {}", transactionTypeURN, userId, neighborhoodId);

        Long transactionTypeId = null;
        if (transactionTypeURN != null){
            transactionTypeId = ValidationUtils.extractURNId(transactionTypeURN);
            ValidationUtils.checkTransactionTypeId(transactionTypeId);
        }

        ValidationUtils.checkUserId(userId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkPageAndSize(page, size);

        userDao.findUser(userId, neighborhoodId).orElseThrow(NotFoundException::new);

        return purchaseDao.getPurchases(userId, transactionTypeId, page, size);
    }

    @Override
    public int countPurchases(long userId, String transactionTypeURN) {
        LOGGER.info("Counting Transactions of type {} made by User {}", transactionTypeURN, userId);

        Long transactionTypeId = null;
        if (transactionTypeURN != null){
            transactionTypeId = ValidationUtils.extractURNId(transactionTypeURN);
            ValidationUtils.checkTransactionTypeId(transactionTypeId);
        }

        ValidationUtils.checkUserId(userId);

        return purchaseDao.countPurchases(userId, transactionTypeId);
    }

    @Override
    public int calculatePurchasePages(long userId, String transactionTypeURN, int size) {
        LOGGER.info("Calculating Transaction Pages of type {} made by User {}", transactionTypeURN, userId);

        Long transactionTypeId = null;
        if (transactionTypeURN != null){
            transactionTypeId = ValidationUtils.extractURNId(transactionTypeURN);
            ValidationUtils.checkTransactionTypeId(transactionTypeId);
        }

        ValidationUtils.checkUserId(userId);
        ValidationUtils.checkSize(size);

        return PaginationUtils.calculatePages(purchaseDao.countPurchases(userId, transactionTypeId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

}
