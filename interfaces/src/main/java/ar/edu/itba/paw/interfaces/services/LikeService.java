package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Like;
import ar.edu.itba.paw.models.Entities.Post;
import ar.edu.itba.paw.models.Entities.User;

import java.util.List;
import java.util.Optional;

public interface LikeService {

    Like addLikeToPost(long postId, long userId);

    // -----------------------------------------------------------------------------------------------------------------

    boolean isPostLiked(long postId, long userId);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Like> findLikeById(long likeId);

    void removeLikeFromPost(long postId, long userId);

    // -----------------------------------------------------------------------------------------------------------------

    List<Like> getLikesByCriteria(long neighborhoodId, long postId, long userId, int page, int size);

    int getTotalLikePagesByCriteria(long neighborhoodId, long postId, long userId, int size);

}
