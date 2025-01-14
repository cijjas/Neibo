package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Like;

import java.util.List;

public interface LikeService {

    Like createLike(long userId, long postId);

    // -----------------------------------------------------------------------------------------------------------------

    List<Like> getLikes(long neighborhoodId, Long userId, Long postId, int page, int size);

    int calculateLikePages(long neighborhoodId, Long userId, Long postId, int size);

    int countLikes(long neighborhoodId, Long userId, Long postId);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteLike(Long userId, Long postId);
}
