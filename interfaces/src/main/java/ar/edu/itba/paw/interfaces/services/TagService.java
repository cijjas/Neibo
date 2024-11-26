package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Tag;

import java.util.List;
import java.util.Optional;

public interface TagService {

    Tag createTag(long neighborhoodId, String name);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Tag> findTag(long tagId, long neighborhoodId);

    List<Tag> getTags(Long postId, long neighborhoodId, int page, int size);

    int calculateTagPages(Long postId, long neighborhoodId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteTag(long neighborhoodId, long tagId);

}
