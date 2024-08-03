package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Tag;

import java.util.List;
import java.util.Optional;

public interface TagService {

    Tag createTag(long neighborhoodId, String name);

    void categorizePost(long postId, List<String> tagURIs, long neighborhoodId);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Tag> findTag(long tagId, long neighborhoodId);

    List<Tag> getTags(String postURN, long neighborhoodId, int page, int size);

    // -----------------------------------------------------------------------------------------------------------------

    int calculateTagPages(String postURN, long neighborhoodId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteTag(long neighborhoodId, long tagId);

}
