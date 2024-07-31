package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Tag;

import java.util.List;
import java.util.Optional;

public interface TagDao {

    // ---------------------------------------------- TAGS INSERT ------------------------------------------------------

    Tag createTag(String name);

    // ---------------------------------------------- TAGS SELECT ------------------------------------------------------

    List<Tag> getTags(Long postId, long neighborhoodId, int page, int size);

    Optional<Tag> findTag(long tagId, long neighborhoodId);

    Optional<Tag> findTag(String tagName);

    int countTags(Long postId, long neighborhoodId);

    // ---------------------------------------------- TAGS DELETE ------------------------------------------------------
    boolean deleteTag(long tagId);

}
