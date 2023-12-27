package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
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

import java.util.List;

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
    public Request createRequest(long userId, long productId, String message) {
        LOGGER.info("User {} Requesting Product {}", userId, productId);
        Product product = productDao.findProductById(productId).orElseThrow(() -> new IllegalStateException("Product not found"));
        User sender = userDao.findUserById(userId).orElseThrow(() -> new IllegalStateException("User not found"));
        emailService.sendNewRequestMail(product, sender, message);
        return requestDao.createRequest(userId, productId, message);
    }

    @Override
    public List<Request> getRequestsByCriteria(long productId, long userId, int page, int size){
        if (userId > 0 && productId > 0) {
            return requestDao.getRequestsByProductAndUser(productId, userId, page, size);
        } else if (userId > 0) {
            return requestDao.getRequestsByUserId(userId, page, size);
        } else if (productId > 0) {
            return requestDao.getRequestsByProductId(productId, page, size);
        } else {
            throw new NotFoundException("Invalid combination of parameters.");
        }
    }

    @Override
    public int getRequestsCountByCriteria(long productId, long userId) {
        if (userId > 0 && productId > 0) {
            return requestDao.getRequestsCountByProductAndUser(productId, userId);
        } else if (userId > 0) {
            return requestDao.getRequestsCountByUserId(userId);
        } else if (productId > 0) {
            return requestDao.getRequestsCountByProductId(productId);
        } else {
            throw new NotFoundException("Invalid combination of parameters.");
        }
    }

    @Override
    public List<Request> getRequestsByProductId(long productId, int page, int size) {
        return requestDao.getRequestsByProductId(productId, page, size);
    }

    @Override
    public int getRequestsCountByProductId(long productId) {
        return requestDao.getRequestsCountByProductId(productId);
    }


    @Override
    public List<Request> getRequestsByProductAndUser(long productId, long userId, int page, int size) {
        return requestDao.getRequestsByProductAndUser(productId, userId, page, size);
    }

    @Override
    public List<Request> getRequestsByUserId(long userId, int page, int size) {
        return requestDao.getRequestsByUserId(userId, page, size);
    }

    @Override
    public int getRequestsCountByProductAndUser(long productId, long userId) {
        return requestDao.getRequestsCountByProductAndUser(productId, userId);
    }

    @Override
    public int getRequestsCountUser(long userId) {
        return requestDao.getRequestsCountByUserId(userId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void markRequestAsFulfilled(long requestId) {
        Request request = requestDao.findRequest(requestId).orElseThrow(()-> new NotFoundException("Request Not Found"));
        request.setFulfilled(true);
    }
}
