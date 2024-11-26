package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.enums.RequestStatus;
import ar.edu.itba.paw.enums.TransactionType;
import ar.edu.itba.paw.interfaces.persistence.RequestDao;
import ar.edu.itba.paw.models.Entities.Product;
import ar.edu.itba.paw.models.Entities.Request;
import ar.edu.itba.paw.models.Entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class RequestDaoImpl implements RequestDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    // --------------------------------------------- REQUESTS INSERT ---------------------------------------------------

    @Override
    public Request createRequest(long userId, long productId, String message, int quantity) {
        LOGGER.debug("Inserting Request for product with id {}", productId);

        Request request = new Request.Builder()
                .product(em.find(Product.class, productId))
                .user(em.find(User.class, userId))
                .message(message)
                .requestDate(new Date(System.currentTimeMillis()))
                .status(RequestStatus.REQUESTED)
                .units(quantity)
                .build();
        em.persist(request);
        return request;
    }

    // --------------------------------------------- REQUESTS SELECT ---------------------------------------------------

    @Override
    public Optional<Request> findRequest(long requestId) {
        LOGGER.debug("Selecting Request {}", requestId);

        return Optional.ofNullable(em.find(Request.class, requestId));
    }

    @Override
    public List<Request> getRequests(Long userId, Long productId, Long typeId, Long statusId, long neighborhoodId, int page, int size) {
        LOGGER.debug("Selecting Requests By Criteria");

        StringBuilder queryBuilder = new StringBuilder("SELECT r.requestId FROM Request r WHERE r.user.neighborhood.neighborhoodId = :neighborhoodId ");

        // Add status condition
        if (statusId != null) {
            queryBuilder.append("AND r.status = :status ");
        }

        // Add productId condition
        if (productId != null) {
            queryBuilder.append("AND r.product.productId = :productId ");
        }

        if (typeId != null && userId != null) {
            TransactionType type = TransactionType.fromId(typeId);
            switch (type) {
                case PURCHASE:
                    queryBuilder.append("AND r.user.userId = :userId ");
                    break;
                case SALE:
                    queryBuilder.append("AND r.product.seller.userId = :userId ");
                    break;
            }
        }
        queryBuilder.append(" ORDER BY r.requestDate DESC");

        TypedQuery<Long> idQuery = em.createQuery(queryBuilder.toString(), Long.class);

        // Set parameters for the query
        idQuery.setParameter("neighborhoodId", neighborhoodId);
        if (statusId != null) {
            idQuery.setParameter("status", RequestStatus.fromId(statusId));
        }
        if (productId != null) {
            idQuery.setParameter("productId", productId);
        }
        if (typeId != null && userId != null) {
            idQuery.setParameter("userId", userId);
        }

        // Pagination
        idQuery.setFirstResult((page - 1) * size);
        idQuery.setMaxResults(size);
        List<Long> requestIds = idQuery.getResultList();

        if (!requestIds.isEmpty()) {
            TypedQuery<Request> requestQuery = em.createQuery(
                    "SELECT r FROM Request r WHERE r.requestId IN :requestIds ORDER BY r.requestDate DESC", Request.class);
            requestQuery.setParameter("requestIds", requestIds);
            return requestQuery.getResultList();
        }

        // Return an empty list if no request IDs are found
        return Collections.emptyList();
    }

    @Override
    public int countRequests(Long userId, Long productId, Long typeId, Long statusId, long neighborhoodId) {
        LOGGER.debug("Selecting Requests Count by Criteria");

        StringBuilder queryBuilder = new StringBuilder("SELECT r.requestId FROM Request r WHERE r.user.neighborhood.neighborhoodId = :neighborhoodId ");

        // Add status condition
        if (statusId != null) {
            queryBuilder.append("AND r.status = :status ");
        }

        // Add productId condition
        if (productId != null) {
            queryBuilder.append("AND r.product.productId = :productId ");
        }

        if (typeId != null && userId != null) {
            TransactionType type = TransactionType.fromId(typeId);
            switch (type) {
                case PURCHASE:
                    queryBuilder.append("AND r.user.userId = :userId ");
                    break;
                case SALE:
                    queryBuilder.append("AND r.product.seller.userId = :userId ");
                    break;
            }
        }
        queryBuilder.append(" ORDER BY r.requestDate DESC");

        TypedQuery<Long> idQuery = em.createQuery(queryBuilder.toString(), Long.class);

        // Set parameters for the query
        idQuery.setParameter("neighborhoodId", neighborhoodId);
        if (statusId != null) {
            idQuery.setParameter("status", RequestStatus.fromId(statusId));
        }
        if (productId != null) {
            idQuery.setParameter("productId", productId);
        }
        if (typeId != null && userId != null) {
            idQuery.setParameter("userId", userId);
        }

        List<Long> requestIds = idQuery.getResultList();

        int count = 0;
        if (!requestIds.isEmpty()) {
            TypedQuery<Request> requestQuery = em.createQuery(
                    "SELECT r FROM Request r WHERE r.requestId IN :requestIds ORDER BY r.requestDate DESC", Request.class);
            requestQuery.setParameter("requestIds", requestIds);
            count = requestQuery.getResultList().size();
        }

        return count;
    }

    // --------------------------------------------- REQUESTS DELETE ---------------------------------------------------

    @Override
    public boolean deleteRequest(long requestId) {
        LOGGER.debug("Deleting Request {}", requestId);

        Request request = em.find(Request.class, requestId);
        if (request != null) {
            em.remove(request);
            return true;
        }
        return false;
    }
}
