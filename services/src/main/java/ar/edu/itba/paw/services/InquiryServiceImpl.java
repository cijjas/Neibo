package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.interfaces.persistence.InquiryDao;
import ar.edu.itba.paw.interfaces.persistence.ProductDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.InquiryService;
import ar.edu.itba.paw.models.JunctionEntities.Inquiry;
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
public class InquiryServiceImpl implements InquiryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageServiceImpl.class);
    private final InquiryDao inquiryDao;
    private final EmailService emailService;
    private final UserDao userDao;
    private final ProductDao productDao;

    @Autowired
    public InquiryServiceImpl(final InquiryDao inquiryDao, final UserDao userDao, final ProductDao productDao, final EmailService emailService) {
        this.inquiryDao = inquiryDao;
        this.userDao = userDao;
        this.productDao = productDao;
        this.emailService = emailService;
    }

    @Override
    public Inquiry createInquiry(long userId, long productId, String message) {
        LOGGER.info("User {} Inquiring on Product {}", userId, productId);
        //Send email to seller
        Product product = productDao.findProductById(productId).orElseThrow(() -> new IllegalStateException("Product not found"));
        User receiver = product.getSeller();
        sendInquiryMail(receiver, product, message, false);

        return inquiryDao.createInquiry(userId, productId, message);
    }

    @Override
    public void replyInquiry(long inquiryId, String reply) {
        LOGGER.info("Replying to Inquiry with id {}", inquiryId);
        //Send email to inquirer
        Inquiry inquiry = inquiryDao.findInquiryById(inquiryId).orElseThrow(() -> new IllegalStateException("Inquiry not found"));
        Product product = inquiry.getProduct();
        User receiver = inquiry.getUser();
        sendInquiryMail(receiver, product, reply, true);

        inquiryDao.replyInquiry(inquiryId, reply);
    }

    private void sendInquiryMail(User receiver, Product product, String message, boolean reply) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", receiver.getName());
        variables.put("productName", product.getName());
        variables.put("message", message);
        variables.put("productPath", "http://pawserver.it.itba.edu.ar/paw-2023b-02/marketplace/" + product.getProductId());
        boolean isEnglish = receiver.getLanguage() == Language.ENGLISH;
        if(isEnglish) {
            variables.put("customMessage", reply ? "Your inquiry has been replied " : "You have a new inquiry ");
            variables.put("replyOrMessage", reply ? "The reply: " : "The message: ");
            emailService.sendMessageUsingThymeleafTemplate(receiver.getMail(), reply ? "Response to Inquiry" : "New Inquiry", "inquiry-template_en.html", variables);
        }
        variables.put("customMessage", reply ? "Has recibido una respuesta a tu consulta " : "Tienes una nueva consulta ");
        variables.put("replyOrMessage", reply ? "La respuesta: " : "El mensaje: ");
        emailService.sendMessageUsingThymeleafTemplate(receiver.getMail(), reply ? "Respuesta a Consulta" : "Nueva Consulta", "inquiry-template_es.html", variables);
    }
}
