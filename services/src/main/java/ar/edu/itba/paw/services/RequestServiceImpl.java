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
    public RequestServiceImpl(RequestDao requestDao, UserDao userDao, ProductDao productDao, EmailService emailService) {
        this.requestDao = requestDao;
        this.userDao = userDao;
        this.productDao = productDao;
        this.emailService = emailService;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Request createRequest(long neighborhoodId, long userId, long productId, String message, int quantity) {
        LOGGER.info("Creating Request {} for {} units for Product {} by User {}", message, quantity, productId, userId);

        Product product = productDao.findProduct(neighborhoodId, productId).orElseThrow(NotFoundException::new);
        User seller = userDao.findUser(product.getSeller().getUserId()).orElseThrow(NotFoundException::new);
        emailService.sendNewRequestMail(product, seller, message);

        return requestDao.createRequest(userId, productId, message, quantity);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Request> findRequest(long neighborhoodId, long requestId) {
        LOGGER.info("Finding Request {} from Neighborhood {}", requestId, neighborhoodId);

        return requestDao.findRequest(neighborhoodId, requestId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Request> getRequests(long neighborhoodId, Long userId, Long productId, Long transactionTypeId, Long requestStatusId, int page, int size) {
        LOGGER.info("Getting Requests for Product {} made by User {} that has Transaction Type {} and has Request Status {} from Neighborhood {}", productId, userId, transactionTypeId, requestStatusId, neighborhoodId);

        return requestDao.getRequests(neighborhoodId, userId, productId, transactionTypeId, requestStatusId, page, size);
    }

    @Override
    public int countRequests(long neighborhoodId, Long userId, Long productId, Long transactionTypeId, Long requestStatusId) {
        LOGGER.info("Counting Requests for Product {} made by User {} that has Transaction Type {} and has Request Status {} from Neighborhood {}", productId, userId, transactionTypeId, requestStatusId, neighborhoodId);

        return requestDao.countRequests(neighborhoodId, userId, productId, transactionTypeId, requestStatusId);
    }

    @Override
    @Transactional(readOnly = true)
    public int calculateRequestPages(long neighborhoodId, Long userId, Long productId, Long transactionTypeId, Long requestStatusId, int size) {
        LOGGER.info("Calculating Requests for Product {} made by User {} that has Transaction Type {} and has Request Status {} from Neighborhood {}", productId, userId, transactionTypeId, requestStatusId, neighborhoodId);

        return PaginationUtils.calculatePages(requestDao.countRequests(neighborhoodId, userId, productId, transactionTypeId, requestStatusId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Request updateRequest(long neighborhoodId, long requestId, Long requestStatusId) {
        LOGGER.info("Updating Request {} from Neighborhood {} to {}", requestId, neighborhoodId, requestStatusId);

        Request request = requestDao.findRequest(neighborhoodId, requestId).orElseThrow(NotFoundException::new);

        if (requestStatusId != null) {
            if (requestStatusId == RequestStatus.ACCEPTED.getId()) {
                Product p = request.getProduct();
                long finalUnits = p.getRemainingUnits() - request.getUnits();
                if (finalUnits < 0)
                    throw new IllegalArgumentException("Cant fulfill the request, not enough stock.");
                request.setPurchaseDate(new Date(System.currentTimeMillis()));
                p.setRemainingUnits(p.getRemainingUnits() - request.getUnits());
            }
            request.setStatus(RequestStatus.fromId(requestStatusId).get()); // Controller layer guarantees non-empty Optional
        }

        return request;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteRequest(long neighborhoodId, long requestId) {
        LOGGER.info("Deleting Request {} from Neighborhood {}", requestId, neighborhoodId);

        return requestDao.deleteRequest(neighborhoodId, requestId);
    }
}
