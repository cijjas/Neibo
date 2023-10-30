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
    // ---------------------------------------------- REVIEWS SELECT ---------------------------------------------------
    private static final RowMapper<Review> ROW_MAPPER = (rs, rowNum) ->
            new Review.Builder()
                    .reviewId(rs.getLong("reviewid"))
                    .rating(rs.getFloat("rating"))
                    .review(rs.getString("review"))
                    .date(rs.getTimestamp("date"))
                    .build();
    private static final RowMapper<Float> ROW_MAPPER_2 = (rs, rowNum) ->
            rs.getFloat(1);
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final String REVIEWS = "SELECT * FROM reviews ";

    @Autowired
    public ReviewDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("reviewid")
                .withTableName("reviews");
    }

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

    @Override
    public Review getReview(long reviewId) {
        LOGGER.debug("Selecting Reviews with reviewId {}", reviewId);
        return em.find(Review.class, reviewId);
        //DEBERIA SER OPTIONAL
//        return Optional.ofNullable(em.find(Review.class, reviewId));
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
        return Optional.ofNullable(query.getSingleResult().floatValue());
//        List<Float> rating = jdbcTemplate.query("SELECT AVG(rating) FROM reviews WHERE workerid = ?", ROW_MAPPER_2, workerId);
//        return rating.isEmpty() ? Optional.empty() : Optional.of(rating.get(0));
    }

    @Override
    public int getReviewsCount(long workerId) {
        LOGGER.debug("Selecting Review Count for Worker {}", workerId);
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(*) FROM Review r WHERE r.worker.user.userId = :workerId", Long.class);
        query.setParameter("workerId", workerId);
        return query.getSingleResult().intValue();
//        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM reviews WHERE workerid = ?", Integer.class, workerId);
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
