package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Like;

import java.util.List;
import java.util.Optional;

public interface LikeService {

    Like createLike(long postId, long userId);

    // -----------------------------------------------------------------------------------------------------------------

    boolean isPostLiked(long postId, long userId);

    Optional<Like> findLike(long likeId);

    List<Like> getLikes(long neighborhoodId, long postId, long userId, int page, int size);

    // ---------------------------------------------------

    int countLikes(long neighborhoodId, long postId, long userId);

    int calculateLikePages(long neighborhoodId, long postId, long userId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    void deleteLike(long postId, long userId);
}
