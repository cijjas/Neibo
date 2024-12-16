package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Like;

import java.util.List;

public interface LikeService {

    Like createLike(long userId, long postId);

    // -----------------------------------------------------------------------------------------------------------------

    List<Like> getLikes(Long userId, Long postId, int page, int size);

    int calculateLikePages(Long userId, Long postId, int size);

    int countLikes(Long userId, Long postId);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteLike(Long userId, Long postId);
}
