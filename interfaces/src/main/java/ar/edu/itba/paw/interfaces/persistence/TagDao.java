package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Tag;

import java.util.List;
import java.util.Optional;

public interface TagDao {

    // ---------------------------------------------- TAGS INSERT ------------------------------------------------------

    Tag createTag(String name);

    // ---------------------------------------------- TAGS SELECT ------------------------------------------------------

    List<Tag> getTags(Long postId, long neighborhoodId, int page, int size);

    Optional<Tag> findTag(long tagId);

    int countTags(Long postId, long neighborhoodId);

    List<Tag> getTags(long postId);

    List<Tag> getNeighborhoodTags(long neighborhoodId);

    List<Tag> getAllTags();
}
