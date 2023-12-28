package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Tag;

import java.util.List;

public interface TagService {

    Tag createTag(String name);

    // -----------------------------------------------------------------------------------------------------------------

    List<Tag> getTagsByPostId(long id);

    List<Tag> getTags(long neighborhoodId);

    List<Tag> getTagsByCriteria(Long postId, Long neighborhoodId);

    // -----------------------------------------------------------------------------------------------------------------

    void createTagsAndCategorizePost(long postId, String tagsString);

    String createURLForTagFilter(String tags, String currentUrl, long neighborhoodId);
}
