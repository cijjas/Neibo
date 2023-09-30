package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Tag;

import java.util.List;
import java.util.Optional;

public interface TagService {

    // -----------------------------------------------------------------------------------------------------------------

    Tag createTag(String name);

    void createTagsAndCategorizePost(long postId, String tagsString);

    String createURLForTagFilter(String tags, String currentUrl);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<List<Tag>> findTagsByPostId(long id);

    List<Tag> getTags();
}
