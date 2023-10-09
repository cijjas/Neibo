package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ReviewDao;
import ar.edu.itba.paw.interfaces.services.ReviewService;
import ar.edu.itba.paw.models.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewDao reviewDao;

    @Autowired
    public ReviewServiceImpl(ReviewDao reviewDao) {
        this.reviewDao = reviewDao;
    }

    // -------------------------------------------- REVIEWS INSERT -----------------------------------------------------
    @Override
    public Review createReview(long workerId, long userId, float rating, String review) {
        return reviewDao.createReview(workerId, userId, rating, review);
    }

    // -------------------------------------------- REVIEWS SELECT -----------------------------------------------------
    @Override
    public Review getReview(long reviewId) {
        return reviewDao.getReview(reviewId);
    }

    @Override
    public List<Review> getReviews(long workerId) {
        return reviewDao.getReviews(workerId);
    }

    // -------------------------------------------- REVIEWS DELETE -----------------------------------------------------
    @Override
    public void deleteReview(long reviewId) {
        reviewDao.deleteReview(reviewId);
    }
}
