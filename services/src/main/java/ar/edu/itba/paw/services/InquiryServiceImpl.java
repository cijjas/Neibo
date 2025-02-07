package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.InquiryDao;
import ar.edu.itba.paw.interfaces.persistence.ProductDao;
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
    private final ProductDao productDao;

    @Autowired
    public InquiryServiceImpl(InquiryDao inquiryDao, ProductDao productDao, EmailService emailService) {
        this.inquiryDao = inquiryDao;
        this.productDao = productDao;
        this.emailService = emailService;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Inquiry createInquiry(long neighborhoodId, long userId, long productId, String message) {
        LOGGER.info("Creating Inquiry with message {} for Product {} from User {}", message, productId, userId);

        Product product = productDao.findProduct(neighborhoodId, productId).orElseThrow(NotFoundException::new);
        User receiver = product.getSeller();
        emailService.sendInquiryMail(receiver, product, message, false);

        return inquiryDao.createInquiry(userId, productId, message);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Inquiry> findInquiry(long neighborhoodId, long productId, long inquiryId) {
        LOGGER.info("Finding Inquiry {} for Product {} from Neighborhood {}", inquiryId, productId, neighborhoodId);

        return inquiryDao.findInquiry(neighborhoodId, productId, inquiryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inquiry> getInquiries(long productId, int size, int page) {
        LOGGER.info("Getting Inquiries for Product {}}", productId);

        return inquiryDao.getInquiries(productId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int calculateInquiryPages(long productId, int size) {
        LOGGER.info("Calculating Inquiry Pages for Product {}", productId);

        return PaginationUtils.calculatePages(inquiryDao.countInquiries(productId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Inquiry replyInquiry(long neighborhoodId, long productId, long inquiryId, String reply) {
        LOGGER.info("Creating the reply {} for Inquiry {} made on Product {} from Neighborhood {}", reply, inquiryId, productId, neighborhoodId);

        Inquiry inquiry = inquiryDao.findInquiry(neighborhoodId, productId, inquiryId).orElseThrow(NotFoundException::new);
        inquiry.setReply(reply);

        Product product = inquiry.getProduct();
        User receiver = inquiry.getUser();
        emailService.sendInquiryMail(receiver, product, reply, true);

        return inquiry;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteInquiry(long neighborhoodId, long productId, long inquiryId) {
        LOGGER.info("Deleting Inquiry {} made on Product {} from Neighborhood {}", inquiryId, productId, neighborhoodId);

        return inquiryDao.deleteInquiry(neighborhoodId, productId, inquiryId);
    }
}
