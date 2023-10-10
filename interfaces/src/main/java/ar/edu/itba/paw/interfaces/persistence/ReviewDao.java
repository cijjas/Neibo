package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Review;

import java.util.List;

public interface ReviewDao {
    // -------------------------------------------- REVIEWS INSERT -----------------------------------------------------
    Review createReview(long workerId, long userId, float rating, String review);

    // -------------------------------------------- REVIEWS SELECT -----------------------------------------------------
    Review getReview(long reviewId);

    List<Review> getReviews(long workerId);

    float getAvgRating(long workerId);

    int getReviewsCount(long workerId);

    // -------------------------------------------- REVIEWS DELETE -----------------------------------------------------
    void deleteReview(long reviewId);

}
