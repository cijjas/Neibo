package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Like;

import java.util.List;

public interface LikeService {

    Like createLike(String postURN, String userURN);

    // -----------------------------------------------------------------------------------------------------------------

    List<Like> getLikes(long neighborhoodId, String postURN, String userURN, int page, int size);

    // ---------------------------------------------------

    int countLikes(long neighborhoodId, String postURN, String userURN);

    int calculateLikePages(long neighborhoodId, String postURN, String userURN, int size);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteLike(String postURN, String userURN);
}
