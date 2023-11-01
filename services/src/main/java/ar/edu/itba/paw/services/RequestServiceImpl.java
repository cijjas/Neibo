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

    @Override
    public Request createRequest(long userId, long productId, String message) {
        LOGGER.info("User {} Requesting Product {}", userId, productId);
        //Send email to seller
        Product product = productDao.findProductById(productId).orElseThrow(() -> new IllegalStateException("Product not found"));
        User sender = userDao.findUserById(userId).orElseThrow(() -> new IllegalStateException("User not found"));
        User receiver = product.getSeller();
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", receiver.getName());
        variables.put("productName", product.getName());
        variables.put("senderName", sender.getName());
        variables.put("senderSurname", sender.getSurname());
        variables.put("message", message);
        variables.put("productPath", "http://pawserver.it.itba.edu.ar/paw-2023b-02/marketplace/" + product.getProductId());
        boolean isEnglish = receiver.getLanguage() == Language.ENGLISH;
        emailService.sendMessageUsingThymeleafTemplate(receiver.getMail(), isEnglish ? "New Request" : "Nueva Solicitud", isEnglish ? "request-template_en.html" : "request-template_es.html", variables);

        return requestDao.createRequest(userId, productId);
    }
}
