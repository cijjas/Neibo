package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Like;

import java.util.List;

public interface LikeService {

    Like createLike(long postId, long userId);

    // -----------------------------------------------------------------------------------------------------------------

    List<Like> getLikes(Long postId, Long userId, int page, int size);

    // ---------------------------------------------------

    int countLikes(Long postId, Long userId);

    int calculateLikePages(Long postId, Long userId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteLike(Long postId, Long userId);
}
