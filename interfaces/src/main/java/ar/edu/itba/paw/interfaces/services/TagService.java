package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Tag;

import java.util.List;

public interface TagService {

    Tag createTag(String name);

    // -----------------------------------------------------------------------------------------------------------------

    List<Tag> findTagsByPostId(long id);

    List<Tag> getTags(long neighborhoodId);

    // -----------------------------------------------------------------------------------------------------------------

    void createTagsAndCategorizePost(long postId, String tagsString);

    String createURLForTagFilter(String tags, String currentUrl, long neighborhoodId);
}
