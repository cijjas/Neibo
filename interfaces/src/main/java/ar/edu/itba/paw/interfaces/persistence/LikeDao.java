package ar.edu.itba.paw.interfaces.persistence;


import ar.edu.itba.paw.models.JunctionEntities.Like;

public interface LikeDao {

    // -------------------------------------------------- LIKES INSERT -------------------------------------------------

    Like createLike(long postId, long userId);

    // -------------------------------------------------- LIKES SELECT -------------------------------------------------

    int getLikes(long postId);

    boolean isPostLiked(long postId, long userId);

    // -------------------------------------------------- LIKES DELETE -------------------------------------------------

    boolean deleteLike(long postId, long userId);
}
