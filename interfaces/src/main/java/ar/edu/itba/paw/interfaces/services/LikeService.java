package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Like;

import java.util.List;

public interface LikeService {

    Like createLike(long postId, long userId);

    // -----------------------------------------------------------------------------------------------------------------

    List<Like> getLikes(String postURN, String userURN, int page, int size);

    // ---------------------------------------------------

    int countLikes(String postURN, String userURN);

    int calculateLikePages(String postURN, String userURN, int size);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteLike(String postURN, String userURN);
}
