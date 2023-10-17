package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ReviewDao;
import ar.edu.itba.paw.interfaces.services.ReviewService;
import ar.edu.itba.paw.models.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewDao reviewDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewServiceImpl.class);

    @Autowired
    public ReviewServiceImpl(ReviewDao reviewDao) {
        this.reviewDao = reviewDao;
    }

    // -------------------------------------------- REVIEWS INSERT -----------------------------------------------------
    @Override
    public Review createReview(long workerId, long userId, float rating, String review) {
        LOGGER.info("Creating Review for Worker {} from User {}", workerId, userId);
        return reviewDao.createReview(workerId, userId, rating, review);
    }

    // -------------------------------------------- REVIEWS SELECT -----------------------------------------------------
    @Override
    public Review getReview(long reviewId) {
        LOGGER.info("Finding Review with id {}", reviewId);
        return reviewDao.getReview(reviewId);
    }

    @Override
    public List<Review> getReviews(long workerId) {
        LOGGER.info("Getting reviews for Worker {}", workerId);
        return reviewDao.getReviews(workerId);
    }

    @Override
    public Optional<Float> getAvgRating(long workerId) {
        LOGGER.info("Getting Average Rating for Worker {}", workerId);
        return reviewDao.getAvgRating(workerId);
    }

    @Override
    public int getReviewsCount(long workerId) {
        return reviewDao.getReviewsCount(workerId);
    }

    // -------------------------------------------- REVIEWS DELETE -----------------------------------------------------
    @Override
    public void deleteReview(long reviewId) {
        reviewDao.deleteReview(reviewId);
    }
}
