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
    public Review createReview(long workerId, String userURN, float rating, String review) {
        LOGGER.info("Creating a Review for Worker {} made by User {}", workerId, userURN);

        Long userId = ValidationUtils.checkURNAndExtractUserId(userURN);

        return reviewDao.createReview(workerId, userId, rating, review);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Review> findReview(long reviewId) {
        LOGGER.info("Finding Review {}", reviewId);

        ValidationUtils.checkReviewId(reviewId);

        return reviewDao.findReview(reviewId);
    }

    @Override
    public Optional<Review> findReview(long reviewId, long workerId) {
        LOGGER.info("Finding Review {} from Worker {}", reviewId, workerId);

        ValidationUtils.checkReviewId(reviewId);
        ValidationUtils.checkWorkerId(workerId);

        workerDao.findWorker(workerId).orElseThrow(NotFoundException::new);

        return reviewDao.findReview(reviewId, workerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviews(long workerId) {
        LOGGER.info("Getting Reviews for Worker {}", workerId);

        ValidationUtils.checkWorkerId(workerId);

        return reviewDao.getReviews(workerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviews(long workerId, int page, int size) {
        LOGGER.info("Getting Reviews for Worker {}", workerId);

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
        LOGGER.info("Counting Reviews for Worker {}", workerId);

        ValidationUtils.checkWorkerId(workerId);

        return reviewDao.countReviews(workerId);
    }

    @Override
    public int calculateReviewPages(long workerId, int size) {
        LOGGER.info("Calculating Review Pages for Worker {}", workerId);

        ValidationUtils.checkWorkerId(workerId);
        ValidationUtils.checkSize(size);

        return PaginationUtils.calculatePages(reviewDao.countReviews(workerId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteReview(long reviewId) {
        LOGGER.info("Deleting Review {}", reviewId);

        ValidationUtils.checkReviewId(reviewId);

        return reviewDao.deleteReview(reviewId);
    }
}
