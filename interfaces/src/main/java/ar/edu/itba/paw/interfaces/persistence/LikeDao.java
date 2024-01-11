package ar.edu.itba.paw.interfaces.persistence;


import ar.edu.itba.paw.models.Entities.Like;
import ar.edu.itba.paw.models.Entities.Post;
import ar.edu.itba.paw.models.Entities.User;

import java.util.List;
import java.util.Optional;

public interface LikeDao {

    // -------------------------------------------------- LIKES INSERT -------------------------------------------------

    Like createLike(long postId, long userId);

    // -------------------------------------------------- LIKES SELECT -------------------------------------------------
    List<Like> getAllLikes();

    List<Like> getLikesByNeighborhood(long neighborhoodId, int page, int size);

    int getLikes(long postId);

    List<Like> getLikesByPost(long postId, int page, int size);

    List<Like> getLikesByUser(long userId, int page, int size);

    int getAllLikesCount();

    int getLikesByNeighborhoodCount(long neighborhoodId);

    int getLikesByPostCount(long postId);

    int getLikesByUserCount(long userId);

    boolean isPostLiked(long postId, long userId);

    // -------------------------------------------------- LIKES DELETE -------------------------------------------------

    Optional<Like> findLikeById(long likeId);

    boolean deleteLike(long postId, long userId);
}
