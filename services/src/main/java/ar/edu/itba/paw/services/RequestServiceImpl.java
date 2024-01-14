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

        Product product = productDao.findProduct(productId).orElseThrow(() -> new NotFoundException("Product not found"));
        User sender = userDao.findUser(userId).orElseThrow(() -> new NotFoundException("User not found"));
        emailService.sendNewRequestMail(product, sender, message);
        return requestDao.createRequest(userId, productId, message);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public List<Request> getRequests(long productId, long userId, int page, int size){

        ValidationUtils.checkRequestId(productId, userId);
        ValidationUtils.checkPageAndSize(page, size);

        return requestDao.getRequests(productId, userId, page, size);
    }

    @Override
    public int countRequests(long productId, long userId) {

//        ValidationUtils.checkRequestId(productId, userId);

        if(userId <= 0 && productId <= 0) {
            throw new NotFoundException("Invalid combination of parameters.");
        }
        return requestDao.countRequests(productId, userId);
    }

    @Override
    public int calculateRequestPages(long productId, long userId, int size) {
        return PaginationUtils.calculatePages(requestDao.countRequests(productId, userId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void markRequestAsFulfilled(long requestId) {

        ValidationUtils.checkRequestId(requestId);

        Request request = requestDao.findRequest(requestId).orElseThrow(()-> new NotFoundException("Request Not Found"));
        request.setFulfilled(true);
    }
}
