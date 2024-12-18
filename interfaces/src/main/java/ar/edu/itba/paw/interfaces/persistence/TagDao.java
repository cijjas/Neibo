package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Tag;

import java.util.List;
import java.util.Optional;

public interface TagDao {

    // ---------------------------------------------- TAGS INSERT ------------------------------------------------------

    Tag createTag(String name);

    // ---------------------------------------------- TAGS SELECT ------------------------------------------------------

    Optional<Tag> findTag(long neighborhoodId, long tagId);

    Optional<Tag> findTag(String tagName);

    List<Tag> getTags(long neighborhoodId, Long postId, int page, int size);

    int countTags(long neighborhoodId, Long postId);

    // ---------------------------------------------- TAGS DELETE ------------------------------------------------------
    boolean deleteTag(long tagId);

}
