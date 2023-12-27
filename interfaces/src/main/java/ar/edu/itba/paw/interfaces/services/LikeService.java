package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Like;

import java.util.Optional;

public interface LikeService {

    void addLikeToPost(long postId, long userId);

    // -----------------------------------------------------------------------------------------------------------------

    boolean isPostLiked(long postId, long userId);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Like> findLikeById(long likeId);

    void removeLikeFromPost(long postId, long userId);
}
