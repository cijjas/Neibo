package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Tag;

import java.util.List;

public interface TagService {

    Tag createTag(String name);

    // -----------------------------------------------------------------------------------------------------------------

    List<Tag> getTags(long neighborhoodId);

    List<Tag> getTagsByCriteria(Long postId, Long neighborhoodId, int page, int size);

    // -----------------------------------------------------------------------------------------------------------------

    void createTagsAndCategorizePost(long postId, String tagsString);

    String createURLForTagFilter(String tags, String currentUrl, long neighborhoodId);

    int getTotalTagPages(long neighborhoodId, int size);

    int getTotalTagPagesByCriteria(Long postId, Long neighborhoodId, int size);
}
