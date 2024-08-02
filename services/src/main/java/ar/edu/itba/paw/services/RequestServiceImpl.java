package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.RequestStatus;
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
    public Request createRequest(String userURN, String productURN, String message, int quantity) {
        LOGGER.info("Creating a Request for Product {} by User {}", productURN, userURN);

        TwoIds twoIds = ValidationUtils.extractTwoURNIds(productURN);
        long neighborhoodId = twoIds.getFirstId();
        long productId = twoIds.getSecondId();

        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkProductId(productId);
        ValidationUtils.checkQuantity(quantity);

        Product product = productDao.findProduct(productId, neighborhoodId).orElseThrow(() -> new NotFoundException("Product not found"));
        User seller = userDao.findUser(product.getSeller().getUserId()).orElseThrow(() -> new NotFoundException("User not found"));
        emailService.sendNewRequestMail(product, seller, message);

        Long userId = ValidationUtils.checkURNAndExtractUserId(userURN);

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

    @Override
    public List<Request> getRequests(String userURN, String productURN, String typeURN, String statusURN, int page, int size, long neighborhoodId) {
        LOGGER.info("Getting Requests for Product {} made by User {} from Neighborhood {}", productURN, userURN, neighborhoodId);

        Long userId = ValidationUtils.checkURNAndExtractUserId(userURN);
        Long productId = ValidationUtils.checkURNAndExtractProductId(productURN);
        Long transactionTypeId = ValidationUtils.checkURNAndExtractTransactionTypeId(typeURN);
        Long requestStatusId = ValidationUtils.checkURNAndExtractRequestStatusId(statusURN);

        ValidationUtils.checkPageAndSize(page, size);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        neighborhoodDao.findNeighborhood(neighborhoodId).orElseThrow(NotFoundException::new);

        return requestDao.getRequests(userId, productId, transactionTypeId, requestStatusId, neighborhoodId, page, size);
    }

    // ---------------------------------------------------

    @Override
    public int calculateRequestPages(String productURN, String userURN, String typeURN, String statusURN, long neighborhoodId, int size) {
        LOGGER.info("Calculating Request Pages for Product {} made by User {}", productURN, userURN);

        Long userId = ValidationUtils.checkURNAndExtractUserId(userURN);
        Long productId = ValidationUtils.checkURNAndExtractProductId(productURN);
        Long transactionTypeId = ValidationUtils.checkURNAndExtractTransactionTypeId(typeURN);
        Long requestStatusId = ValidationUtils.checkURNAndExtractRequestStatusId(statusURN);

        ValidationUtils.checkSize(size);

        return PaginationUtils.calculatePages(requestDao.countRequests(userId, productId, transactionTypeId, requestStatusId, neighborhoodId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Request updateRequest(long requestId, String requestStatusURN) {
        LOGGER.info("Updating Request {} as {}", requestId, requestStatusURN);

        ValidationUtils.checkRequestId(requestId);
        Long requestStatusId = ValidationUtils.checkURNAndExtractRequestStatusId(requestStatusURN);

        Request request = requestDao.findRequest(requestId).orElseThrow(() -> new NotFoundException("Request Not Found"));
        request.setStatus(RequestStatus.fromId(requestStatusId));
        request.setPurchaseDate(new java.sql.Date(System.currentTimeMillis()));

        return request;
    }
}
