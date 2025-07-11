package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewDao {

    // -------------------------------------------- REVIEWS INSERT -----------------------------------------------------

    Review createReview(long workerId, long userId, float rating, String review);

    // -------------------------------------------- REVIEWS SELECT -----------------------------------------------------

    Optional<Review> findReview(long workerId, long reviewId);

    Optional<Review> findLatestReview(long workerId, long userId);

    Float findAverageRating(long workerId);

    List<Review> getReviews(long workerId, int page, int size);

    int countReviews(long workerId);

    // -------------------------------------------- REVIEWS DELETE -----------------------------------------------------

    boolean deleteReview(long workerId, long reviewId);

}
