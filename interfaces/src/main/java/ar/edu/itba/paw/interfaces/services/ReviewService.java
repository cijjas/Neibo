package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {

    Review createReview(long workerId, long userId, float rating, String review);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Review> findReview(long reviewId, long workerId);

    Optional<Review> findLatestReview(long workerId, long userId);

    List<Review> getReviews(long workerId, int page, int size);

    // ---------------------------------------------------

    int calculateReviewPages(long workerId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteReview(long reviewId);

    Float findAverageRating(long workerId);
}
