package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.interfaces.persistence.ProductDao;
import ar.edu.itba.paw.interfaces.persistence.RequestDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.RequestService;
import ar.edu.itba.paw.models.Entities.Product;
import ar.edu.itba.paw.models.Entities.Request;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.models.TwoIds;
import org.hibernate.tool.schema.internal.exec.ScriptTargetOutputToFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RequestServiceImpl implements RequestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestServiceImpl.class);
    private final RequestDao requestDao;
    private final UserDao userDao;
    private final ProductDao productDao;
    private final EmailService emailService;
    private final NeighborhoodDao neighborhoodDao;

    @Autowired
    public RequestServiceImpl(final RequestDao requestDao, final UserDao userDao, final ProductDao productDao,
                              final EmailService emailService, final NeighborhoodDao neighborhoodDao) {
        this.requestDao = requestDao;
        this.userDao = userDao;
        this.productDao = productDao;
        this.emailService = emailService;
        this.neighborhoodDao = neighborhoodDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Request createRequest(long userId, String productURN, String message, int quantity) {
        LOGGER.info("Creating a Request for Product {} by User {}", productURN, userId);

        TwoIds twoIds = ValidationUtils.extractTwoURNIds(productURN);
        long neighborhoodId = twoIds.getFirstId();
        long productId = twoIds.getSecondId();

        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkProductId(productId);
        ValidationUtils.checkQuantity(quantity);

        Product product = productDao.findProduct(productId, neighborhoodId).orElseThrow(() -> new NotFoundException("Product not found"));
        User seller = userDao.findUser(product.getSeller().getUserId()).orElseThrow(() -> new NotFoundException("User not found"));
        emailService.sendNewRequestMail(product, seller, message);
        return requestDao.createRequest(userId, productId, message, quantity);
    }

    @Override
    public boolean deleteRequest(long requestId) {
        LOGGER.info("Deleting Request {}", requestId);

        ValidationUtils.checkRequestId(requestId);

        return requestDao.deleteRequest(requestId);
    }

    @Override
    public Optional<Request> findRequest(long requestId) {
        LOGGER.info("Finding Request {}", requestId);

        ValidationUtils.checkRequestId(requestId);

        return requestDao.findRequest(requestId);
    }

    @Override
    public Optional<Request> findRequest(long requestId, long neighborhoodId) {
        LOGGER.info("Finding Request {} from Neighborhood {}", requestId, neighborhoodId);

        ValidationUtils.checkRequestId(requestId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        neighborhoodDao.findNeighborhood(neighborhoodId).orElseThrow(NotFoundException::new);

        return requestDao.findRequest(requestId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public List<Request> getRequests(String userURN, String productURN, String typeURN, Boolean fulfilled, int page, int size, long neighborhoodId){
        LOGGER.info("Getting Requests for Product {} made by User {} from Neighborhood {}", productURN, userURN, neighborhoodId);

        Long userId = null;
        if (userURN != null) {
            TwoIds userTwoIds = ValidationUtils.extractTwoURNIds(userURN);
            ValidationUtils.checkNeighborhoodId(userTwoIds.getFirstId());
            ValidationUtils.checkUserId(userTwoIds.getSecondId());
            userId = userTwoIds.getSecondId();
        }

        Long productId = null;
        if (productURN != null) {
            TwoIds productTwoIds = ValidationUtils.extractTwoURNIds(productURN);
            ValidationUtils.checkNeighborhoodId(productTwoIds.getFirstId());
            ValidationUtils.checkProductId(productTwoIds.getSecondId());
            productId = productTwoIds.getSecondId();
        }

        Long transactionTypeId = null;
        if (typeURN != null){
            transactionTypeId = ValidationUtils.extractURNId(typeURN);
            ValidationUtils.checkTransactionTypeId(transactionTypeId);
        } else throw new IllegalArgumentException("Transaction-Type is required");

        ValidationUtils.checkFulfilled(fulfilled);
        ValidationUtils.checkPageAndSize(page, size);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        neighborhoodDao.findNeighborhood(neighborhoodId).orElseThrow(NotFoundException::new);

        return requestDao.getRequests(userId, productId, transactionTypeId, fulfilled, page, size);
    }

    @Override
    public int countRequests(String productURN, String userURN, String typeURN, Boolean fulfilled) {
        LOGGER.info("Counting Requests for Product {} made by User {}", productURN, userURN);

        Long userId = null;
        if (userURN != null) {
            TwoIds userTwoIds = ValidationUtils.extractTwoURNIds(userURN);
            ValidationUtils.checkNeighborhoodId(userTwoIds.getFirstId());
            ValidationUtils.checkUserId(userTwoIds.getSecondId());
            userId = userTwoIds.getSecondId();
        }

        Long productId = null;
        if (productURN != null) {
            TwoIds productTwoIds = ValidationUtils.extractTwoURNIds(productURN);
            ValidationUtils.checkNeighborhoodId(productTwoIds.getFirstId());
            ValidationUtils.checkProductId(productTwoIds.getSecondId());
            productId = productTwoIds.getSecondId();
        }

        Long transactionTypeId = null;
        if (typeURN != null){
            transactionTypeId = ValidationUtils.extractURNId(typeURN);
            ValidationUtils.checkTransactionTypeId(transactionTypeId);
        }

        return requestDao.countRequests(productId, userId, transactionTypeId, fulfilled);
    }

    @Override
    public int calculateRequestPages(String productURN, String userURN, String typeURN, Boolean fulfilled, int size) {
        LOGGER.info("Calculating Request Pages for Product {} made by User {}", productURN, userURN);

        Long userId = null;
        if (userURN != null) {
            TwoIds userTwoIds = ValidationUtils.extractTwoURNIds(userURN);
            ValidationUtils.checkNeighborhoodId(userTwoIds.getFirstId());
            ValidationUtils.checkUserId(userTwoIds.getSecondId());
            userId = userTwoIds.getSecondId();
        }

        Long productId = null;
        if (productURN != null) {
            TwoIds productTwoIds = ValidationUtils.extractTwoURNIds(productURN);
            ValidationUtils.checkNeighborhoodId(productTwoIds.getFirstId());
            ValidationUtils.checkProductId(productTwoIds.getSecondId());
            productId = productTwoIds.getSecondId();
        }

        Long typeId = null;
        if (typeURN != null){
            typeId = ValidationUtils.extractURNId(typeURN);
            ValidationUtils.checkTransactionTypeId(typeId);
        }

        ValidationUtils.checkSize(size);

        return PaginationUtils.calculatePages(requestDao.countRequests(userId, productId, typeId, fulfilled), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Request markRequestAsFulfilled(long requestId) {
        LOGGER.info("Marking Request {} as fulfilled", requestId);

        ValidationUtils.checkRequestId(requestId);

        Request request = requestDao.findRequest(requestId).orElseThrow(()-> new NotFoundException("Request Not Found"));
        request.setFulfilled(true);
        request.setPurchaseDate(new java.sql.Date(System.currentTimeMillis()));

        return request;
    }
}
