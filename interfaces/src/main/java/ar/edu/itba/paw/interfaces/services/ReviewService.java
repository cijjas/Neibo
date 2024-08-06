package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {

    Review createReview(long workerId, String userURN, float rating, String review);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Review> findReview(long reviewId, long workerId);

    List<Review> getReviews(long workerId, int page, int size);

    // ---------------------------------------------------

    int calculateReviewPages(long workerId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteReview(long reviewId);

    Float getAvgRating(long workerId);
}
