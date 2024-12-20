package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.interfaces.persistence.ReviewDao;
import ar.edu.itba.paw.models.Entities.Review;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.models.Entities.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class ReviewDaoImpl implements ReviewDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    // ---------------------------------------------- REVIEWS INSERT ---------------------------------------------------

    @Override
    public Review createReview(long workerId, long userId, float rating, String reviewString) {
        LOGGER.debug("Inserting Review with Worker Id {} and User Id {}", workerId, userId);

        Review review = new Review.Builder()
                .user(em.find(User.class, userId))
                .worker(em.find(Worker.class, workerId))
                .rating(rating)
                .review(reviewString)
                .date(new Date(System.currentTimeMillis()))
                .build();
        em.persist(review);
        return review;
    }

    // ---------------------------------------------- REVIEWS SELECT ---------------------------------------------------

    @Override
    public Optional<Review> findReview(long workerId, long reviewId) {
        LOGGER.debug("Selecting Review with Worker Id {} and Review Id {}", workerId, reviewId);

        TypedQuery<Review> query = em.createQuery(
                "SELECT r FROM Review r WHERE r.id = :reviewId AND r.worker.id = :workerId",
                Review.class
        );

        query.setParameter("reviewId", reviewId);
        query.setParameter("workerId", workerId);

        List<Review> result = query.getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public Optional<Review> findLatestReview(long workerId, long userId) {
        LOGGER.debug("Selecting Latest Review from with Worker Id {} and User Id {}", workerId, userId);

        TypedQuery<Review> query = em.createQuery(
                "SELECT r FROM Review r WHERE r.worker.user.userId = :workerId AND r.user.userId = :userId ORDER BY r.date DESC",
                Review.class
        );

        query.setParameter("workerId", workerId);
        query.setParameter("userId", userId);
        query.setMaxResults(1);

        List<Review> result = query.getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public Float findAverageRating(long workerId) {
        LOGGER.debug("Selecting Rating Average from Worker with Worker Id {}", workerId);

        TypedQuery<Double> query = em.createQuery("SELECT AVG(rating) FROM Review r WHERE r.worker.user.userId = :workerId", Double.class);
        query.setParameter("workerId", workerId);
        if (query.getSingleResult() == null)
            return 0.0f;
        return query.getSingleResult().floatValue();
    }

    @Override
    public List<Review> getReviews(long workerId, int page, int size) {
        LOGGER.debug("Selecting Reviews with Worker Id {}", workerId);

        TypedQuery<Review> query = em.createQuery("SELECT r FROM Review r WHERE r.worker.user.userId = :workerId ORDER BY r.date DESC", Review.class);
        query.setParameter("workerId", workerId);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    @Override
    public int countReviews(long workerId) {
        LOGGER.debug("Counting Reviews with Worker Id {}", workerId);

        TypedQuery<Long> query = em.createQuery("SELECT COUNT(*) FROM Review r WHERE r.worker.user.userId = :workerId", Long.class);
        query.setParameter("workerId", workerId);
        return query.getSingleResult().intValue();
    }

    // ---------------------------------------------- REVIEWS DELETE ---------------------------------------------------

    @Override
    public boolean deleteReview(long workerId, long reviewId) {
        LOGGER.debug("Deleting Review with Worker Id {} and Review Id {}", workerId, reviewId);

        String hql = "DELETE FROM Review r WHERE r.reviewId = :reviewId AND r.worker.id = :workerId";

        int deletedCount = em.createQuery(hql)
                .setParameter("reviewId", reviewId)
                .setParameter("workerId", workerId)
                .executeUpdate();

        return deletedCount > 0;
    }
}
