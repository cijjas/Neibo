package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.interfaces.persistence.ProductDao;
import ar.edu.itba.paw.interfaces.persistence.RequestDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.RequestService;
import ar.edu.itba.paw.models.JunctionEntities.Request;
import ar.edu.itba.paw.models.MainEntities.Product;
import ar.edu.itba.paw.models.MainEntities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class RequestServiceImpl implements RequestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageServiceImpl.class);
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
    public int getRequestsCountByProductAndUser(long productId, long userId) {
        return requestDao.getRequestsCountByProductAndUser(productId, userId);
    }
}
