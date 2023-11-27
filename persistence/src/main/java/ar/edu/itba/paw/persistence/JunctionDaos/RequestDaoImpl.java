package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.interfaces.persistence.RequestDao;
import ar.edu.itba.paw.models.Entities.Request;
import ar.edu.itba.paw.models.Entities.Product;
import ar.edu.itba.paw.models.Entities.User;
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
public class RequestDaoImpl implements RequestDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestDaoImpl.class);
    @PersistenceContext
    private EntityManager em;

    // --------------------------------------------- REQUESTS INSERT ---------------------------------------------------

    @Override
    public Request createRequest(long userId, long productId, String message) {
        LOGGER.debug("Inserting Request for product with id {}", productId);
        Request request = new Request.Builder()
                .product(em.find(Product.class, productId))
                .user(em.find(User.class, userId))
                .message(message)
                .requestDate(new java.sql.Date(System.currentTimeMillis()))
                .fulfilled(false)
                .build();
        em.persist(request);
        return request;
    }

    @Override
    public Optional<Request> findRequest(long requestId) {
        LOGGER.debug("Selecting Request {}", requestId);
        return Optional.ofNullable(em.find(Request.class, requestId));
    }


    @Override
    public List<Request> getRequestsByProductId(long productId, int page, int size) {
        LOGGER.debug("Selecting Requests from Product {}", productId);

        TypedQuery<Long> idQuery = em.createQuery(
                "SELECT r.requestId FROM Request r WHERE r.product.productId = :productId AND r.fulfilled = false", Long.class);
        idQuery.setParameter("productId", productId);
        idQuery.setFirstResult((page - 1) * size);
        idQuery.setMaxResults(size);

        List<Long> requestIds = idQuery.getResultList();

        if (!requestIds.isEmpty()) {
            TypedQuery<Request> requestQuery = em.createQuery(
                    "SELECT r FROM Request r WHERE r.requestId IN :requestIds ORDER BY r.requestDate DESC", Request.class);
            requestQuery.setParameter("requestIds", requestIds);
            return requestQuery.getResultList();
        }

        return Collections.emptyList();
    }


    @Override
    public int getRequestsCountByProductId(long productId) {
        LOGGER.debug("Selecting Requests Count from Product {}", productId);

        Long count = (Long) em.createQuery("SELECT COUNT(r) FROM Request r " +
                        "WHERE r.product.productId = :productId AND r.fulfilled = false")
                .setParameter("productId", productId)
                .getSingleResult();
        return count != null ? count.intValue() : 0;
    }

    @Override
    public List<Request> getRequestsByProductAndUser(long productId, long userId, int page, int size) {
        LOGGER.debug("Selecting Requests from Product {} By User {}", productId, userId);

        TypedQuery<Long> idQuery = em.createQuery(
                "SELECT r.requestId FROM Request r WHERE r.product.productId = :productId AND r.user.userId = :userId AND r.fulfilled = false", Long.class);
        idQuery.setParameter("productId", productId);
        idQuery.setParameter("userId", userId);
        idQuery.setFirstResult((page - 1) * size);
        idQuery.setMaxResults(size);

        List<Long> requestIds = idQuery.getResultList();

        // Second query to retrieve Product objects based on the IDs
        if (!requestIds.isEmpty()) {
            TypedQuery<Request> requestQuery = em.createQuery(
                    "SELECT r FROM Request r WHERE r.requestId IN :requestIds ORDER BY r.requestDate DESC", Request.class);
            requestQuery.setParameter("requestIds", requestIds);
            return requestQuery.getResultList();
        }

        return Collections.emptyList();
    }

    @Override
    public int getRequestsCountByProductAndUser(long productId, long userId) {
        LOGGER.debug("Selecting Requests Count from Product {} By User {}", productId, userId);
        Long count = (Long) em.createQuery("SELECT COUNT(r) FROM Request r " +
                        "WHERE r.product.productId = :productId AND r.user.userId = :userId AND r.fulfilled = false")
                .setParameter("productId", productId)
                .getSingleResult();
        return count != null ? count.intValue() : 0;
    }

    @Override
    public List<Request> getRequestsByUserId(long userId, int page, int size) {
        LOGGER.debug("Selecting Requests from User {}", userId);

        TypedQuery<Long> idQuery = em.createQuery("SELECT r.requestId FROM Request r " +
                "WHERE r.user.userId = :userId AND r.fulfilled = false", Long.class);
        idQuery.setParameter("userId", userId);
        idQuery.setFirstResult((page - 1) * size);
        idQuery.setMaxResults(size);

        List<Long> requestIds = idQuery.getResultList();

        if (!requestIds.isEmpty()) {
            TypedQuery<Request> requestQuery = em.createQuery(
                    "SELECT r FROM Request r WHERE r.requestId IN :requestIds ORDER BY r.requestDate DESC", Request.class);
            requestQuery.setParameter("requestIds", requestIds);
            return requestQuery.getResultList();
        }

        return Collections.emptyList();
    }

    @Override
    public int getRequestsCountByUserId(long userId) {
        LOGGER.debug("Selecting Requests Count from User {}", userId);

        Long count = (Long) em.createQuery("SELECT COUNT(r) FROM Request r " +
                        "WHERE r.user.userId = :userId AND r.fulfilled = false")
                .setParameter("userId", userId)
                .getSingleResult();
        return count != null ? count.intValue() : 0;
    }

}
