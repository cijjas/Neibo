package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Tag;

import java.util.List;

public interface TagService {

    Tag createTag(String name);

    // -----------------------------------------------------------------------------------------------------------------

    List<Tag> getTags(Long postId, Long neighborhoodId, int page, int size);

    // -----------------------------------------------------------------------------------------------------------------

    void createTagsAndCategorizePost(long postId, String tagsString);

    String createURLForTagFilter(String tags, String currentUrl, long neighborhoodId);

    int calculateTagPages(Long postId, Long neighborhoodId, int size);

    int countTags(Long postId, Long neighborhoodId);
}
