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
    public Inquiry createInquiry(String userURN, long productId, String message) {
        LOGGER.info("Creating Inquiry for Product {} from User {}", productId, userURN);

        //Send email to seller
        Product product = productDao.findProduct(productId).orElseThrow(() -> new NotFoundException("Product not found"));
        User receiver = product.getSeller();
        emailService.sendInquiryMail(receiver, product, message, false);

        Long userId = ValidationUtils.checkURNAndExtractUserId(userURN); // Cant be null due to form validation

        return inquiryDao.createInquiry(userId, productId, message);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Optional<Inquiry> findInquiry(long inquiryId) {
        LOGGER.info("Finding Inquiry {}", inquiryId);

        ValidationUtils.checkInquiryId(inquiryId);

        return inquiryDao.findInquiry(inquiryId);
    }

    @Override
    public Optional<Inquiry> findInquiry(long inquiryId, long productId, long neighborhoodId) {
        LOGGER.info("Finding Inquiry {} for Product {} from Neighborhood {}", inquiryId, productId, neighborhoodId);

        ValidationUtils.checkInquiryId(inquiryId);
        ValidationUtils.checkProductId(productId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        return inquiryDao.findInquiry(inquiryId, productId, neighborhoodId);
    }


    @Override
    public List<Inquiry> getInquiries(long productId, int page, int size, long neighborhoodId) {
        LOGGER.info("Getting Inquiries for Product {} from Neighborhood {}", productId, neighborhoodId);

        ValidationUtils.checkProductId(productId);
        ValidationUtils.checkPageAndSize(page, size);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        productDao.findProduct(productId, neighborhoodId).orElseThrow(NotFoundException::new);

        return inquiryDao.getInquiries(productId, page, size);
    }

    // ---------------------------------------------------

    @Override
    public int calculateInquiryPages(long productId, int size) {
        LOGGER.info("Calculating Inquiry Pages for Product {}", productId);

        ValidationUtils.checkProductId(productId);
        ValidationUtils.checkSize(size);

        return PaginationUtils.calculatePages(inquiryDao.countInquiries(productId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Inquiry replyInquiry(long inquiryId, String reply) {
        LOGGER.info("Creating a reply for Inquiry {}", inquiryId);

        //Send email to inquirer
        Inquiry inquiry = inquiryDao.findInquiry(inquiryId).orElseThrow(() -> new NotFoundException("Inquiry not found"));
        inquiry.setReply(reply);

        Product product = inquiry.getProduct();
        User receiver = inquiry.getUser();
        emailService.sendInquiryMail(receiver, product, reply, true);

        return inquiry;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteInquiry(long inquiryId) {
        LOGGER.info("Deleting Inquiry {}", inquiryId);

        ValidationUtils.checkInquiryId(inquiryId);

        return inquiryDao.deleteInquiry(inquiryId);
    }
}
