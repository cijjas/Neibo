package ar.edu.itba.paw.interfaces.persistence;


import ar.edu.itba.paw.models.JunctionEntities.Like;

public interface LikeDao {

    // ---------------------------------------------- POST_USERS_LIKES INSERT ------------------------------------------

    Like createLike(long postId, long userId);

    // ---------------------------------------------- POST_USERS_LIKES SELECT ------------------------------------------

    int getLikes(long postId);

    boolean isPostLiked(long postId, long userId);

    // ---------------------------------------------- POST_USERS_LIKES DELETE ------------------------------------------

    boolean deleteLike(long postId, long userId);
}
