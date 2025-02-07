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
        LOGGER.info("Creating the Review {} with {} rating for Worker {} made by User {}", review, rating, workerId, userId);

        return reviewDao.createReview(workerId, userId, rating, review);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Review> findReview(long workerId, long reviewId) {
        LOGGER.info("Finding Review {} from Worker {}", reviewId, workerId);

        return reviewDao.findReview(workerId, reviewId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Review> findLatestReview(long workerId, long userId) {
        LOGGER.info("Finding Latest from Review User {} to Worker {}", userId, workerId);

        return reviewDao.findLatestReview(workerId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Float findAverageRating(long workerId) {
        LOGGER.info("Finding Average Rating for Worker {}", workerId);

        return reviewDao.findAverageRating(workerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviews(long workerId, int page, int size) {
        LOGGER.info("Getting Reviews for Worker {}", workerId);

        return reviewDao.getReviews(workerId, page, size);
    }

    @Override
    public int countReviews(long workerId) {
        LOGGER.info("Counting Reviews for Worker {}", workerId);

        return reviewDao.countReviews(workerId);
    }

    @Override
    @Transactional(readOnly = true)
    public int calculateReviewPages(long workerId, int size) {
        LOGGER.info("Calculating Review Pages for Worker {}", workerId);

        return PaginationUtils.calculatePages(reviewDao.countReviews(workerId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteReview(long workerId, long reviewId) {
        LOGGER.info("Deleting Review {} made to Worker {}", reviewId, workerId);

        return reviewDao.deleteReview(workerId, reviewId);
    }
}
