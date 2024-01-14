package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Tag;

import java.util.List;

public interface TagDao {

    // ---------------------------------------------- TAGS INSERT ------------------------------------------------------

    Tag createTag(String name);

    // ---------------------------------------------- TAGS SELECT ------------------------------------------------------

    List<Tag> getTagsByCriteria(long postId, long neighborhoodId, int page, int size);

    int getTagsCountByCriteria(long postId, long neighborhoodId);

    List<Tag> getTagsByPostId(long id);

    List<Tag> getTags(long neighborhoodId);

//    int getTagsCount(long neighborhoodId);
//
//    int getTagsCountByPostId(long postId);

    List<Tag> getAllTags();
}
