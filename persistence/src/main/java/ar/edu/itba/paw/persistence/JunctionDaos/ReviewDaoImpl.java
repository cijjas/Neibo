package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.ReviewDao;
import ar.edu.itba.paw.models.MainEntities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ReviewDaoImpl implements ReviewDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewDaoImpl.class);
    @PersistenceContext
    private EntityManager em;

    // ---------------------------------------------- REVIEWS INSERT ---------------------------------------------------

    @Override
    public Review createReview(long workerId, long userId, float rating, String reviewString) {
        LOGGER.debug("Inserting Review");
        Review review = new Review.Builder()
                .user(em.find(User.class, userId))
                .worker(em.find(Worker.class, workerId))
                .rating(rating)
                .review(reviewString)
                .build();
        em.persist(review);
        return review;
    }

    // ---------------------------------------------- REVIEWS SELECT ---------------------------------------------------

    @Override
    public Optional<Review> findReviewById(long reviewId) {
        LOGGER.debug("Selecting Review with reviewId {}", reviewId);
        return Optional.ofNullable(em.find(Review.class, reviewId));
    }

    @Override
    public List<Review> getReviews(long workerId) {
        LOGGER.debug("Selecting Reviews from Worker {}", workerId);
        TypedQuery<Review> query = em.createQuery("SELECT r FROM Review r WHERE r.worker.user.userId = :workerId", Review.class);
        query.setParameter("workerId", workerId);
        return query.getResultList();
    }

    @Override
    public Optional<Float> getAvgRating(long workerId) {
        LOGGER.debug("Selecting Average Rating for Worker {}", workerId);
        TypedQuery<Double> query = em.createQuery("SELECT AVG(rating) FROM Review r WHERE r.worker.user.userId = :workerId", Double.class);
        query.setParameter("workerId", workerId);
        if(query.getSingleResult() == null)
            return Optional.of(0.0f);
        return Optional.of(query.getSingleResult().floatValue());
    }

    @Override
    public int getReviewsCount(long workerId) {
        LOGGER.debug("Selecting Review Count for Worker {}", workerId);
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(*) FROM Review r WHERE r.worker.user.userId = :workerId", Long.class);
        query.setParameter("workerId", workerId);
        return query.getSingleResult().intValue();
    }

    // ---------------------------------------------- REVIEWS DELETE ---------------------------------------------------

    @Override
    public boolean deleteReview(long reviewId) {
        LOGGER.debug("Deleting Review with reviewId {}", reviewId);
        Review review = em.find(Review.class, reviewId);
        if (review != null) {
            em.remove(review);
            return true;
        }
        return false;
    }
}
