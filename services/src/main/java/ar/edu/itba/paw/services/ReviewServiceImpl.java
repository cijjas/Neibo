package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.ReviewDao;
import ar.edu.itba.paw.interfaces.persistence.WorkerDao;
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
    private final WorkerDao workerDao;

    @Autowired
    public ReviewServiceImpl(ReviewDao reviewDao, WorkerDao workerDao) {
        this.reviewDao = reviewDao;
        this.workerDao = workerDao;
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
    public Optional<Review> findReview(long reviewId) {
        LOGGER.info("Finding Review with id {}", reviewId);

        ValidationUtils.checkReviewId(reviewId);

        return reviewDao.findReview(reviewId);
    }

    @Override
    public Optional<Review> findReview(long reviewId, long workerId) {
        ValidationUtils.checkReviewId(reviewId);
        ValidationUtils.checkWorkerId(workerId);

        return reviewDao.findReview(reviewId, workerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviews(long workerId) {
        LOGGER.info("Getting reviews for Worker {}", workerId);

        ValidationUtils.checkWorkerId(workerId);

        return reviewDao.getReviews(workerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviews(long workerId, int page, int size) {
        LOGGER.info("Getting reviews for Worker {}", workerId);

        ValidationUtils.checkWorkerId(workerId);
        ValidationUtils.checkPageAndSize(page, size);

        workerDao.findWorker(workerId).orElseThrow(NotFoundException::new);

        return reviewDao.getReviews(workerId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Float> getAvgRating(long workerId) {
        LOGGER.info("Getting Average Rating for Worker {}", workerId);

        ValidationUtils.checkWorkerId(workerId);

        return reviewDao.getAvgRating(workerId);
    }

    // ---------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public int countReviews(long workerId) {

        ValidationUtils.checkWorkerId(workerId);

        return reviewDao.countReviews(workerId);
    }

    @Override
    public int calculateReviewPages(long workerId, int size) {

        ValidationUtils.checkWorkerId(workerId);
        ValidationUtils.checkSize(size);

        return PaginationUtils.calculatePages(reviewDao.countReviews(workerId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void deleteReview(long reviewId) {

        ValidationUtils.checkReviewId(reviewId);

        reviewDao.deleteReview(reviewId);
    }
}
