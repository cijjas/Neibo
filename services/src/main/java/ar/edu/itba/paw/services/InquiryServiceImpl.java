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
    public InquiryServiceImpl(final InquiryDao inquiryDao, final ProductDao productDao, final EmailService emailService) {
        this.inquiryDao = inquiryDao;
        this.productDao = productDao;
        this.emailService = emailService;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Inquiry createInquiry(long userId, long productId, String message) {
        LOGGER.info("Creating Inquiry for Product {} from User {}", productId, userId);

        // Send email to seller
        Product product = productDao.findProduct(productId).orElseThrow(NotFoundException::new);
        User receiver = product.getSeller();
        emailService.sendInquiryMail(receiver, product, message, false);

        return inquiryDao.createInquiry(userId, productId, message);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Inquiry> findInquiry(long inquiryId) {
        LOGGER.info("Finding Inquiry {}", inquiryId);

        return inquiryDao.findInquiry(inquiryId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Inquiry> findInquiry(long neighborhoodId, long productId, long inquiryId) {
        LOGGER.info("Finding Inquiry {} for Product {} from Neighborhood {}", inquiryId, productId, neighborhoodId);

        return inquiryDao.findInquiry(inquiryId, productId, neighborhoodId);
    }


    @Override
    @Transactional(readOnly = true)
    public List<Inquiry> getInquiries(long neighborhoodId, long productId, int size, int page) {
        LOGGER.info("Getting Inquiries for Product {} from Neighborhood {}", productId, neighborhoodId);

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
    public Inquiry replyInquiry(long inquiryId, String reply) {
        LOGGER.info("Creating a reply for Inquiry {}", inquiryId);

        // Send email to inquirer
        Inquiry inquiry = inquiryDao.findInquiry(inquiryId).orElseThrow(NotFoundException::new);
        inquiry.setReply(reply);

        Product product = inquiry.getProduct();
        User receiver = inquiry.getUser();
        emailService.sendInquiryMail(receiver, product, reply, true);

        return inquiry;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteInquiry(long neighborhoodId, long productId, long inquiryId) {
        LOGGER.info("Deleting Inquiry {}", inquiryId);

        return inquiryDao.deleteInquiry(neighborhoodId, productId, inquiryId);
    }
}
