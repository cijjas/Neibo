package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Tag;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface TagService {

    Tag createTag(String name);

    // -----------------------------------------------------------------------------------------------------------------
    boolean deleteTag(long tagId);

    // -----------------------------------------------------------------------------------------------------------------

    List<Tag> getTags(Long postId, long neighborhoodId, int page, int size);

    Optional<Tag> findTag(long tagId, long neighborhoodId);

    // -----------------------------------------------------------------------------------------------------------------

    void createTagsAndCategorizePost(long postId, String tagsString);

    String createURLForTagFilter(String tags, String currentUrl, long neighborhoodId);

    int calculateTagPages(Long postId, Long neighborhoodId, int size);

    int countTags(Long postId, Long neighborhoodId);
}
