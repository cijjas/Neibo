package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Tag;

import java.util.List;

public interface TagDao {

    // ---------------------------------------------- TAGS INSERT ------------------------------------------------------

    Tag createTag(String name);

    // ---------------------------------------------- TAGS SELECT ------------------------------------------------------

    List<Tag> getTags(Long postId, long neighborhoodId, int page, int size);

    int countTags(Long postId, long neighborhoodId);

    List<Tag> getTags(long postId);

    List<Tag> getNeighborhoodTags(long neighborhoodId);

//    int getTagsCount(long neighborhoodId);
//
//    int getTagsCountByPostId(long postId);

    List<Tag> getAllTags();
}
