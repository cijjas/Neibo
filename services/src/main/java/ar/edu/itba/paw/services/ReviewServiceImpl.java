package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ReviewDao;
import ar.edu.itba.paw.interfaces.services.ReviewService;
import ar.edu.itba.paw.models.Entities.Review;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewServiceImpl.class);
    private final ReviewDao reviewDao;

    @Autowired
    public ReviewServiceImpl(ReviewDao reviewDao) {
        this.reviewDao = reviewDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Review createReview(long workerId, long userId, float rating, String review) {
        LOGGER.info("Creating Review for Worker {} from User {}", workerId, userId);
        return reviewDao.createReview(workerId, userId, rating, review);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Review> findReviewById(long reviewId) {
        LOGGER.info("Finding Review with id {}", reviewId);
        if (reviewId <= 0)
            throw new IllegalArgumentException("Review ID must be a positive integer");
        return reviewDao.findReviewById(reviewId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviews(long workerId) {
        LOGGER.info("Getting reviews for Worker {}", workerId);
        return reviewDao.getReviews(workerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviews(long workerId, int page, int size) {
        LOGGER.info("Getting reviews for Worker {}", workerId);
        return reviewDao.getReviews(workerId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Float> getAvgRating(long workerId) {
        LOGGER.info("Getting Average Rating for Worker {}", workerId);
        return reviewDao.getAvgRating(workerId);
    }

    @Override
    @Transactional(readOnly = true)
    public int getReviewsCount(long workerId) {
        return reviewDao.getReviewsCount(workerId);
    }

    @Override
    public int getReviewsTotalPages(long workerId, int size) {
        return (int) Math.ceil((double) getReviewsCount(workerId) / size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void deleteReview(long reviewId) {
        reviewDao.deleteReview(reviewId);
    }
}
