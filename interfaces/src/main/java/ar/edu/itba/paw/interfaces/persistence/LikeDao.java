package ar.edu.itba.paw.interfaces.persistence;


import ar.edu.itba.paw.models.Entities.Like;

import java.util.List;

public interface LikeDao {

    // -------------------------------------------------- LIKES INSERT -------------------------------------------------

    Like createLike(long userId, long postId);

    // -------------------------------------------------- LIKES SELECT -------------------------------------------------

    List<Like> getLikes(long neighborhoodId, Long userId, Long postId, int page, int size);

    int countLikes(long neighborhoodId, Long userId, Long postId);

    // -------------------------------------------------- LIKES DELETE -------------------------------------------------

    boolean deleteLike(long userId, long postId);
}
