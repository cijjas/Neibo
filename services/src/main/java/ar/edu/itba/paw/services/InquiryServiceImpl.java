package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.InquiryDao;
import ar.edu.itba.paw.interfaces.persistence.ProductDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.InquiryService;
import ar.edu.itba.paw.models.Entities.Inquiry;
import ar.edu.itba.paw.models.Entities.Product;
import ar.edu.itba.paw.models.Entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InquiryServiceImpl implements InquiryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(InquiryServiceImpl.class);
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

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Inquiry createInquiry(long userId, long productId, String message) {
        LOGGER.info("User {} Inquiring on Product {}", userId, productId);
        //Send email to seller
        Product product = productDao.findProductById(productId).orElseThrow(() -> new IllegalStateException("Product not found"));
        User receiver = product.getSeller();
        emailService.sendInquiryMail(receiver, product, message, false);

        return inquiryDao.createInquiry(userId, productId, message);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Optional<Inquiry> findInquiryById(long inquiryId) {
        return inquiryDao.findInquiryById(inquiryId);
    }

    @Override
    public List<Inquiry> getInquiriesByProduct(long productId) {
        return inquiryDao.getInquiriesByProduct(productId);
    }

    @Override
    public List<Inquiry> getInquiriesByProductAndCriteria(long productId, int page, int size) {
        return inquiryDao.getInquiriesByProductAndCriteria(productId, page, size);
    }

    @Override
    public int getTotalInquiryPages(long productId, int size) {
        return (int) Math.ceil((double) inquiryDao.getInquiriesByProduct(productId).size() / size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void replyInquiry(long inquiryId, String reply) {
        LOGGER.info("Replying to Inquiry with id {}", inquiryId);
        //Send email to inquirer
        Inquiry inquiry = inquiryDao.findInquiryById(inquiryId).orElseThrow(() -> new IllegalStateException("Inquiry not found"));
        inquiry.setReply(reply);

        Product product = inquiry.getProduct();
        User receiver = inquiry.getUser();
        emailService.sendInquiryMail(receiver, product, reply, true);
    }
}
