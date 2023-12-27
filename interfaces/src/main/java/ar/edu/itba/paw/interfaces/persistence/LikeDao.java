package ar.edu.itba.paw.interfaces.persistence;


import ar.edu.itba.paw.models.Entities.Like;

import java.util.Optional;

public interface LikeDao {

    // -------------------------------------------------- LIKES INSERT -------------------------------------------------

    Like createLike(long postId, long userId);

    // -------------------------------------------------- LIKES SELECT -------------------------------------------------

    int getLikes(long postId);

    boolean isPostLiked(long postId, long userId);

    // -------------------------------------------------- LIKES DELETE -------------------------------------------------

    Optional<Like> findLikeById(long likeId);

    boolean deleteLike(long postId, long userId);
}
