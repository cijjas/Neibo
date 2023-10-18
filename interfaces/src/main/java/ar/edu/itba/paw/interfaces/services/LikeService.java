package ar.edu.itba.paw.interfaces.services;

public interface LikeService {

    void addLikeToPost(long postId, long userId);

    // -----------------------------------------------------------------------------------------------------------------

    boolean isPostLiked(long postId, long userId);

    // -----------------------------------------------------------------------------------------------------------------

    void removeLikeFromPost(long postId, long userId);
}
