package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Like;

import java.util.List;
import java.util.Optional;

public interface LikeService {

    Like createLike(long postId, long userId);

    // -----------------------------------------------------------------------------------------------------------------

    boolean isPostLiked(long postId, long userId);

    List<Like> getLikes(long neighborhoodId, Long postId, Long userId, int page, int size);

    // ---------------------------------------------------

    int countLikes(long neighborhoodId, Long postId, Long userId);

    int calculateLikePages(long neighborhoodId, Long postId, Long userId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteLike(long postId, long userId);
}
