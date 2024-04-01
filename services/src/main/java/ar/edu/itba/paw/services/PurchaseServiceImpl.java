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
    public List<Purchase> getPurchases(long userId, String transactionType, int page, int size, long neighborhoodId) {
        LOGGER.info("Getting Transactions of type {} made by User {} from Neighborhood {}", transactionType, userId, neighborhoodId);

        ValidationUtils.checkUserId(userId);
        ValidationUtils.checkPageAndSize(page, size);
        ValidationUtils.checkOptionalTransactionTypeString(transactionType);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        userDao.findUser(userId, neighborhoodId).orElseThrow(NotFoundException::new);

        return purchaseDao.getPurchases(userId, transactionType, page, size);
    }

    @Override
    public int countPurchases(long userId, String transactionType) {
        LOGGER.info("Counting Transactions of type {} made by User {}", transactionType, userId);

        ValidationUtils.checkUserId(userId);
        ValidationUtils.checkOptionalTransactionTypeString(transactionType);

        return purchaseDao.countPurchases(userId, transactionType);
    }

    @Override
    public int calculatePurchasePages(long userId, String transactionType, int size) {
        LOGGER.info("Calculating Transaction Pages of type {} made by User {}", transactionType, userId);

        ValidationUtils.checkUserId(userId);
        ValidationUtils.checkOptionalTransactionTypeString(transactionType);
        ValidationUtils.checkSize(size);

        return PaginationUtils.calculatePages(purchaseDao.countPurchases(userId, transactionType), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

}
