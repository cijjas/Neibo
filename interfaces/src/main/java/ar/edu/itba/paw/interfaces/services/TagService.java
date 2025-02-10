package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Tag;

import java.util.List;
import java.util.Optional;

public interface TagService {

    Tag createTag(long neighborhoodId, String name);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Tag> findTag(long neighborhoodId, long tagId);

    List<Tag> getTags(long neighborhoodId, Long postId, int page, int size);

    int countTags(long neighborhoodId, Long postId);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteTag(long neighborhoodId, long tagId);

}
