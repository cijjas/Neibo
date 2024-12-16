package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.RequestStatus;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.ProductDao;
import ar.edu.itba.paw.interfaces.persistence.RequestDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.RequestService;
import ar.edu.itba.paw.models.Entities.Product;
import ar.edu.itba.paw.models.Entities.Request;
import ar.edu.itba.paw.models.Entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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

    @Autowired
    public RequestServiceImpl(final RequestDao requestDao, final UserDao userDao, final ProductDao productDao, final EmailService emailService) {
        this.requestDao = requestDao;
        this.userDao = userDao;
        this.productDao = productDao;
        this.emailService = emailService;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Request createRequest(long userId, long productId, String message, int quantity) {
        LOGGER.info("Creating a Request for Product {} by User {}", productId, userId);

        Product product = productDao.findProduct(productId).orElseThrow(NotFoundException::new);
        User seller = userDao.findUser(product.getSeller().getUserId()).orElseThrow(NotFoundException::new);
        emailService.sendNewRequestMail(product, seller, message);

        return requestDao.createRequest(userId, productId, message, quantity);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Request> findRequest(long requestId) {
        LOGGER.info("Finding Request {}", requestId);

        return requestDao.findRequest(requestId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Request> findRequest(long neighborhoodId, long requestId) {
        LOGGER.info("Finding Request {} from Neighborhood {}", requestId, neighborhoodId);

        return requestDao.findRequest(requestId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Request> getRequests(long neighborhoodId, Long userId, Long productId, Long requestStatusId, Long transactionTypeId, int page, int size) {
        LOGGER.info("Getting Requests for Product {} made by User {} that has Transaction Type {} and has Request Status {} from Neighborhood {}", productId, userId, transactionTypeId, requestStatusId, neighborhoodId);

        return requestDao.getRequests(userId, productId, transactionTypeId, requestStatusId, neighborhoodId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int calculateRequestPages(long neighborhoodId, Long userId, Long productId, Long requestStatusId, Long transactionTypeId, int size) {
        LOGGER.info("Calculating Requests for Product {} made by User {} that has Transaction Type {} and has Request Status {} from Neighborhood {}", productId, userId, transactionTypeId, requestStatusId, neighborhoodId);

        return PaginationUtils.calculatePages(requestDao.countRequests(userId, productId, transactionTypeId, requestStatusId, neighborhoodId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Request updateRequest(long requestId, Long requestStatusId) {
        LOGGER.info("Updating Request {} as {}", requestId, requestStatusId);

        Request request = requestDao.findRequest(requestId).orElseThrow(NotFoundException::new);

        if (requestStatusId != null){
            request.setStatus(RequestStatus.fromId(requestStatusId));
            if (requestStatusId == RequestStatus.ACCEPTED.getId())
                request.setPurchaseDate(new Date(System.currentTimeMillis()));
        }

        return request;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteRequest(long neighborhoodId, long requestId) {
        LOGGER.info("Deleting Request {}", requestId);

        return requestDao.deleteRequest(neighborhoodId, requestId);
    }
}
