package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.interfaces.persistence.InquiryDao;
import ar.edu.itba.paw.models.Entities.Inquiry;
import ar.edu.itba.paw.models.Entities.Product;
import ar.edu.itba.paw.models.Entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class InquiryDaoImpl implements InquiryDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(InquiryDaoImpl.class);
    @PersistenceContext
    private EntityManager em;

    // ------------------------------------------ INQUIRIES INSERT -----------------------------------------------------

    @Override
    public Inquiry createInquiry(long userId, long productId, String message) {
        LOGGER.debug("Inserting Inquiry for product with id {}", productId);
        Inquiry inquiry = new Inquiry.Builder()
                .product(em.find(Product.class, productId))
                .user(em.find(User.class, userId))
                .message(message)
                .build();
        em.persist(inquiry);

        return inquiry;
    }

    // ------------------------------------------ INQUIRIES INSERT -----------------------------------------------------

    @Override
    public Optional<Inquiry> findInquiryById(long inquiryId) {
        LOGGER.debug("Selecting Inquiry with id {}", inquiryId);
        return Optional.ofNullable(em.find(Inquiry.class, inquiryId));
    }

    @Override
    public List<Inquiry> getInquiriesByProduct(long productId) {
        LOGGER.debug("Selecting Inquiries from Product with id {}", productId);
        TypedQuery<Inquiry> inquiries = em.createQuery("SELECT DISTINCT i FROM Inquiry i WHERE i.product.productId = :productId", Inquiry.class)
                .setParameter("productId", productId);
        return inquiries.getResultList();
    }

    @Override
    public List<Inquiry> getInquiriesByProductAndCriteria(long productId, int page, int size) {
        LOGGER.debug("Selecting Inquiries from Product with id {}", productId);
        TypedQuery<Inquiry> inquiries = em.createQuery("SELECT DISTINCT i FROM Inquiry i WHERE i.product.productId = :productId", Inquiry.class)
                .setParameter("productId", productId)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size);
        return inquiries.getResultList();
    }
}
