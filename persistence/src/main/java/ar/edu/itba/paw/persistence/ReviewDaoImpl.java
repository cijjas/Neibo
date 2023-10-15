package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.ReviewDao;
import ar.edu.itba.paw.models.Review;
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

@Repository
public class ReviewDaoImpl implements ReviewDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewDaoImpl.class);

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

    // ---------------------------------------------- REVIEWS SELECT ---------------------------------------------------
    private static final RowMapper<Review> reviewRowMapper = (rs, rowNum) ->
            new Review.Builder()
                    .reviewId(rs.getLong("reviewid"))
                    .workerId(rs.getLong("workerid"))
                    .userId(rs.getLong("userid"))
                    .rating(rs.getFloat("rating"))
                    .review(rs.getString("review"))
                    .date(rs.getTimestamp("date"))
                    .build();

    @Override
    public Review getReview(long reviewId) {
        LOGGER.debug("Selecting Reviews with reviewId {}", reviewId);
        return jdbcTemplate.queryForObject("SELECT * FROM reviews WHERE reviewid = ?", reviewRowMapper, reviewId);
    }

    @Override
    public List<Review> getReviews(long workerId) {
        LOGGER.debug("Selecting Reviews from Worker {}", workerId);
        return jdbcTemplate.query("SELECT * FROM reviews WHERE workerid = ?", reviewRowMapper, workerId);
    }

    @Override
    public float getAvgRating(long workerId) {
        LOGGER.debug("Selecting Average Rating for Worker {}", workerId);
        return jdbcTemplate.query("SELECT AVG(rating) FROM reviews WHERE workerid = ?", rs -> {
            if (rs.next()) {
                return rs.getFloat(1);
            }
            return 0f;
        }, workerId);
    }

    @Override
    public int getReviewsCount(long workerId) {
        LOGGER.debug("Selecting Review Count for Worker {}", workerId);
        return jdbcTemplate.query("SELECT COUNT(*) FROM reviews WHERE workerid = ?", rs -> {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }, workerId);
    }

    // ---------------------------------------------- REVIEWS DELETE ---------------------------------------------------

    @Override
    public boolean deleteReview(long reviewId) {
        LOGGER.debug("Deleting Review with reviewId {}", reviewId);
        return jdbcTemplate.update("DELETE FROM reviews WHERE reviewid = ?", reviewId) > 0;
    }
}
