package ar.edu.itba.paw.interfaces.persistence;


import ar.edu.itba.paw.models.Entities.Like;

import java.util.List;
import java.util.Optional;

public interface LikeDao {

    // -------------------------------------------------- LIKES INSERT -------------------------------------------------

    Like createLike(long postId, long userId);

    // -------------------------------------------------- LIKES SELECT -------------------------------------------------

    List<Like> getLikes(Long postId, Long userId, long neighborhoodId, int page, int size);

    Optional<Like> findLike(Long postId, Long userId);

    int countLikes(Long postId, Long userId, long neighborhoodId);

    boolean isPostLiked(long postId, long userId);

    // -------------------------------------------------- LIKES DELETE -------------------------------------------------

    boolean deleteLike(long postId, long userId);
}
