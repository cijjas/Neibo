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
        LOGGER.info("Creating a Review for Worker {} made by User {}", workerId, userId);

        // Check if user has already created a review this same day for the same worker
        // Deactivated for testing should check, uses java.sql which is weird
/*        reviewDao.findLatestReview(workerId, userId).ifPresent(r -> {
            if (r.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().equals(new java.sql.Date(System.currentTimeMillis()).toLocalDate())) {
                throw new IllegalArgumentException("User has already created a review for this worker today");
            }
        });*/

        return reviewDao.createReview(workerId, userId, rating, review);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Optional<Review> findReview(long reviewId, long workerId) {
        LOGGER.info("Finding Review {} from Worker {}", reviewId, workerId);

        return reviewDao.findReview(reviewId, workerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviews(long workerId, int page, int size) {
        LOGGER.info("Getting Reviews for Worker {}", workerId);

        return reviewDao.getReviews(workerId, page, size);
    }

    // ---------------------------------------------------

    @Override
    public int calculateReviewPages(long workerId, int size) {
        LOGGER.info("Calculating Review Pages for Worker {}", workerId);

        return PaginationUtils.calculatePages(reviewDao.countReviews(workerId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteReview(long reviewId) {
        LOGGER.info("Deleting Review {}", reviewId);

        return reviewDao.deleteReview(reviewId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Float findAverageRating(long workerId) {
        return reviewDao.findAverageRating(workerId);
    }
}
