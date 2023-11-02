package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.MainEntities.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewDao {
    // -------------------------------------------- REVIEWS INSERT -----------------------------------------------------
    Review createReview(long workerId, long userId, float rating, String review);

    // -------------------------------------------- REVIEWS SELECT -----------------------------------------------------
    Optional<Review> findReviewById(long reviewId);

    List<Review> getReviews(long workerId);

    Optional<Float> getAvgRating(long workerId);

    int getReviewsCount(long workerId);

    // -------------------------------------------- REVIEWS DELETE -----------------------------------------------------
    boolean deleteReview(long reviewId);

}
