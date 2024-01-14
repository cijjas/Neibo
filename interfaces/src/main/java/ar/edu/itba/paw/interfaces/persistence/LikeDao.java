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

    List<Like> getLikesByCriteria(long postId, long userId, long neighborhoodId, int page, int size);

    int getLikesCountByCriteria(long postId, long userId, long neighborhoodId);

    boolean isPostLiked(long postId, long userId);

    // -------------------------------------------------- LIKES DELETE -------------------------------------------------

    Optional<Like> findLikeById(long likeId);

    boolean deleteLike(long postId, long userId);
}
