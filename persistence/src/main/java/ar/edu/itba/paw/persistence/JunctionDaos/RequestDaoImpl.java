package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.interfaces.persistence.RequestDao;
import ar.edu.itba.paw.models.JunctionEntities.Inquiry;
import ar.edu.itba.paw.models.JunctionEntities.Request;
import ar.edu.itba.paw.models.MainEntities.Comment;
import ar.edu.itba.paw.models.MainEntities.Product;
import ar.edu.itba.paw.models.MainEntities.User;
import ar.edu.itba.paw.persistence.MainEntitiesDaos.PostDaoImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;

@Repository
public class RequestDaoImpl implements RequestDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostDaoImpl.class);
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
                .build();
        em.persist(request);
        return request;
    }

    @Override
    public List<Request> getRequestsByProductId(long productId, int page, int size) {
        LOGGER.debug("Selecting Requests from Product {}", productId);

//        TypedQuery<Request> requests = em.createQuery("SELECT DISTINCT r FROM Request r WHERE r.product.productId = :productId", Request.class)
//                .setParameter("productId", productId);
//        return requests.getResultList();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Request> query = cb.createQuery(Request.class);
        Root<Request> requestRoot = query.from(Request.class);
        query.select(requestRoot);
        query.where(cb.equal(requestRoot.get("request").get("requestId"), productId));
//        query.orderBy(cb.desc(requestRoot.get("date")));
        TypedQuery<Request> typedQuery = em.createQuery(query);
        typedQuery.setFirstResult((page - 1) * size);
        typedQuery.setMaxResults(size);
        List<Request> requests = typedQuery.getResultList();
        if (requests.isEmpty())
            return Collections.emptyList();
        return requests;
    }

    @Override
    public int getRequestsCountByProductId(long productId) {
        LOGGER.debug("Selecting Requests Count from Product {}", productId);
        Long count = (Long) em.createQuery("SELECT COUNT(r) FROM Request r " +
                        "WHERE r.product.productId = :productId")
                .setParameter("productId", productId)
                .getSingleResult();
        return count != null ? count.intValue() : 0;
    }

    @Override
    public List<Request> getRequestsByProductAndUser(long productId, long userId, int page, int size) {
        LOGGER.debug("Selecting Requests from Product {} By User {}", productId, userId);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Request> query = cb.createQuery(Request.class);
        Root<Request> requestRoot = query.from(Request.class);
        query.select(requestRoot);
        query.where(cb.equal(requestRoot.get("request").get("requestId"), productId));
        query.where(cb.equal(requestRoot.get("user").get("userId"), userId));
        TypedQuery<Request> typedQuery = em.createQuery(query);
        typedQuery.setFirstResult((page - 1) * size);
        typedQuery.setMaxResults(size);
        List<Request> requests = typedQuery.getResultList();
        if (requests.isEmpty())
            return Collections.emptyList();
        return requests;
    }

    @Override
    public int getRequestsCountByProductAndUser(long productId, long userId) {
        LOGGER.debug("Selecting Requests Count from Product {} By User {}", productId, userId);
        Long count = (Long) em.createQuery("SELECT COUNT(r) FROM Request r " +
                        "WHERE r.product.productId = :productId AND r.user.userId = :userId")
                .setParameter("productId", productId)
                .getSingleResult();
        return count != null ? count.intValue() : 0;
    }

}
