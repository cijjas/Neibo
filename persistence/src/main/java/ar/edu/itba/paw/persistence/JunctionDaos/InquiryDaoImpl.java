package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.interfaces.persistence.InquiryDao;
import ar.edu.itba.paw.models.Entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Collections;
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
                .inquiryDate(new java.sql.Date(System.currentTimeMillis()))
                .build();
        em.persist(inquiry);

        return inquiry;
    }

    @Override
    public boolean deleteInquiry(long inquiryId) {
        LOGGER.debug("Deleting Inquiry with id {}", inquiryId);

        Inquiry inquiry = em.find(Inquiry.class, inquiryId);
        if (inquiry != null) {
            em.remove(inquiry);
            return true;
        }
        return false;
    }

    // ------------------------------------------ INQUIRIES INSERT -----------------------------------------------------

    @Override
    public Optional<Inquiry> findInquiry(long inquiryId) {
        LOGGER.debug("Selecting Inquiry with id {}", inquiryId);

        return Optional.ofNullable(em.find(Inquiry.class, inquiryId));
    }

    @Override
    public Optional<Inquiry> findInquiry(long inquiryId, long productId, long neighborhoodId) {
        LOGGER.debug("Selecting Inquiry with inquiryId {}, productId {} and neighborhoodId {}", inquiryId, productId, neighborhoodId);

        TypedQuery<Inquiry> query = em.createQuery(
                "SELECT i FROM Inquiry i WHERE i.id = :inquiryId AND i.product.id = :productId AND i.product.seller.neighborhood.id = :neighborhoodId",
                Inquiry.class
        );

        query.setParameter("inquiryId", inquiryId);
        query.setParameter("productId", productId);
        query.setParameter("neighborhoodId", neighborhoodId);

        List<Inquiry> result = query.getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<Inquiry> getInquiries(long productId, int page, int size) {
        LOGGER.debug("Selecting Inquiries from Product with id {}", productId);

        TypedQuery<Long> idQuery = em.createQuery("SELECT i.inquiryId FROM Inquiry i " +
                "WHERE i.product.productId = :productId ORDER BY i.inquiryDate, i.inquiryId DESC", Long.class);
        idQuery.setParameter("productId", productId);
        idQuery.setFirstResult((page - 1) * size);
        idQuery.setMaxResults(size);

        List<Long> inquiryIds = idQuery.getResultList();

        if (!inquiryIds.isEmpty()) {
            TypedQuery<Inquiry> inquiryQuery = em.createQuery(
                    "SELECT i FROM Inquiry i WHERE i.inquiryId IN :inquiryIds ORDER BY i.inquiryDate, i.inquiryId DESC ", Inquiry.class);
            inquiryQuery.setParameter("inquiryIds", inquiryIds);
            return inquiryQuery.getResultList();
        }

        return Collections.emptyList();
    }

    // ---------------------------------------------------

    @Override
    public int countInquiries(long productId) {
        LOGGER.debug("Selecting Inquiries Count from Product {}", productId);

        Long count = (Long) em.createQuery("SELECT COUNT(i) FROM Inquiry i " +
                        "WHERE i.product.productId = :productId")
                .setParameter("productId", productId)
                .getSingleResult();
        return count != null ? count.intValue() : 0;
    }
}
