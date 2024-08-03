package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewDao {

    // -------------------------------------------- REVIEWS INSERT -----------------------------------------------------

    Review createReview(long workerId, long userId, float rating, String review);

    // -------------------------------------------- REVIEWS SELECT -----------------------------------------------------

    Optional<Review> findReview(long reviewId);

    Optional<Review> findReview(long reviewId, long workerId);

    List<Review> getReviews(long workerId, int page, int size);

    Optional<Review> findLatestReview(long workerId, long userId);

    Optional<Float> findAverageRating(long workerId);

    int countReviews(long workerId);

    // -------------------------------------------- REVIEWS DELETE -----------------------------------------------------

    boolean deleteReview(long reviewId);

}
