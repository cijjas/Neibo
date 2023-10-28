package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.ReviewDao;
import ar.edu.itba.paw.models.MainEntities.Review;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

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
    // ---------------------------------------------- REVIEWS SELECT ---------------------------------------------------
    private static final RowMapper<Review> ROW_MAPPER = (rs, rowNum) ->
            new Review.Builder()
                    .reviewId(rs.getLong("reviewid"))
                    .workerId(rs.getLong("workerid"))
                    .userId(rs.getLong("userid"))
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
    public Review createReview(long workerId, long userId, float rating, String review) {
        LOGGER.debug("Inserting Review");
        Map<String, Object> data = new HashMap<>();
        data.put("workerid", workerId);
        data.put("userid", userId);
        data.put("rating", rating);
        data.put("review", review);
        data.put("date", Timestamp.valueOf(LocalDateTime.now()));

        try {
            final Number key = jdbcInsert.executeAndReturnKey(data);
            return new Review.Builder()
                    .reviewId(key.longValue())
                    .workerId(workerId)
                    .userId(userId)
                    .rating(rating)
                    .review(review)
                    .build();
        } catch (DataAccessException ex) {
            LOGGER.error("Error inserting the review", ex);
            throw new InsertionException("An error occurred whilst creating the Review");
        }
    }

    @Override
    public Review getReview(long reviewId) {
        LOGGER.debug("Selecting Reviews with reviewId {}", reviewId);
        return jdbcTemplate.queryForObject(REVIEWS + " WHERE reviewid = ?", ROW_MAPPER, reviewId);
    }

    @Override
    public List<Review> getReviews(long workerId) {
        LOGGER.debug("Selecting Reviews from Worker {}", workerId);
        return jdbcTemplate.query(REVIEWS + " WHERE workerid = ?", ROW_MAPPER, workerId);
    }

    @Override
    public Optional<Float> getAvgRating(long workerId) {
        LOGGER.debug("Selecting Average Rating for Worker {}", workerId);
        List<Float> rating = jdbcTemplate.query("SELECT AVG(rating) FROM reviews WHERE workerid = ?", ROW_MAPPER_2, workerId);
        return rating.isEmpty() ? Optional.empty() : Optional.of(rating.get(0));
    }

    @Override
    public int getReviewsCount(long workerId) {
        LOGGER.debug("Selecting Review Count for Worker {}", workerId);
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM reviews WHERE workerid = ?", Integer.class, workerId);
    }

    // ---------------------------------------------- REVIEWS DELETE ---------------------------------------------------

    @Override
    public boolean deleteReview(long reviewId) {
        LOGGER.debug("Deleting Review with reviewId {}", reviewId);
        return jdbcTemplate.update("DELETE FROM reviews WHERE reviewid = ?", reviewId) > 0;
    }
}
