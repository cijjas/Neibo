package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Tag;

import java.util.List;
import java.util.Optional;

public interface TagService {
    List<Tag> getTags();

    Optional<List<Tag>> findTagsByPostId(long id);

    Tag createTag(String name);

    void createTagsAndCategorizePost(long postId, String tagsString);

    /*List<Tag> createTags(String tagsString);*/
}
