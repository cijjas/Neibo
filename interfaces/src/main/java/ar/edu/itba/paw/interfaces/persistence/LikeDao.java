package ar.edu.itba.paw.interfaces.persistence;


import ar.edu.itba.paw.models.Entities.Like;

import java.util.List;

public interface LikeDao {

    // -------------------------------------------------- LIKES INSERT -------------------------------------------------

    Like createLike(long postId, long userId);

    // -------------------------------------------------- LIKES SELECT -------------------------------------------------

    List<Like> getLikes(Long postId, Long userId, int page, int size);

    int countLikes(Long postId, Long userId);

    // -------------------------------------------------- LIKES DELETE -------------------------------------------------

    boolean deleteLike(long postId, long userId);
}
