package ar.edu.itba.paw.interfaces.services;

public interface LikeService {

    // ---------------------------------------------- POST_USERS_LIKES INSERT ------------------------------------------------

    void addLikeToPost(long postId, long userId);

    void removeLikeFromPost(long postId, long userId);
}
