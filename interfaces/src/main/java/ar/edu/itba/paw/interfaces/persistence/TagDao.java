package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Tag;

import java.util.List;

public interface TagDao {

    // ---------------------------------------------- TAGS INSERT ------------------------------------------------------

    Tag createTag(String name);

    // ---------------------------------------------- TAGS SELECT ------------------------------------------------------

    List<Tag> getTagsByPostId(long id);

    List<Tag> getTagsByPostId(long id, int page, int size);

    List<Tag> getTags(long neighborhoodId);

    List<Tag> getTags(long neighborhoodId, int page, int size);

    int getTagsCount(long neighborhoodId);

    int getTagsCountByPostId(long postId);

    List<Tag> getAllTags();
}
