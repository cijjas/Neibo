package ar.edu.itba.paw.interfaces.persistence;


public interface LikeDao {

    // ---------------------------------------------- POST_USERS_LIKES INSERT ------------------------------------------

    void createLike(long postId, long userId);

    // ---------------------------------------------- POST_USERS_LIKES SELECT ------------------------------------------

    int getLikes(long postId);

    boolean isPostLiked(long postId, long userId);

    // ---------------------------------------------- POST_USERS_LIKES DELETE ------------------------------------------

    boolean deleteLike(long postId, long userId);
}
