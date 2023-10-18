package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {

    Review createReview(long workerId, long userId, float rating, String review);

    // -----------------------------------------------------------------------------------------------------------------

    Review getReview(long reviewId);

    List<Review> getReviews(long workerId);

    Optional<Float> getAvgRating(long workerId);

    int getReviewsCount(long workerId);

    // -----------------------------------------------------------------------------------------------------------------

    void deleteReview(long reviewId);
}
