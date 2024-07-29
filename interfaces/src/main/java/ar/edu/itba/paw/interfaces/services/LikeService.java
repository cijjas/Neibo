package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Like;

import java.util.List;
import java.util.Optional;

public interface LikeService {

    Like createLike(String postURN, String userURN);

    // -----------------------------------------------------------------------------------------------------------------

    boolean isPostLiked(long postId, long userId);

    List<Like> getLikes(long neighborhoodId, String postURN, String userURN, int page, int size);

    Optional<Like> findLike(Long postId, Long userId);

    // ---------------------------------------------------

    int countLikes(long neighborhoodId, String postURN, String userURN);

    int calculateLikePages(long neighborhoodId, String postURN, String userURN, int size);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteLike(String postURN, String userURN);
}
