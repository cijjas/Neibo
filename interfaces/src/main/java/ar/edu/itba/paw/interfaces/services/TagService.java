package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Tag;

import javax.swing.text.html.Option;
import java.net.URI;
import java.util.List;
import java.util.Optional;

public interface TagService {

    Tag createTag(long neighborhoodId, String name);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteTag(long neighborhoodId, long tagId);

    // -----------------------------------------------------------------------------------------------------------------

    List<Tag> getTags(String postURN, long neighborhoodId, int page, int size);

    Optional<Tag> findTag(long tagId, long neighborhoodId);

    // -----------------------------------------------------------------------------------------------------------------

    void categorizePost(long postId, List<String> tagURIs, long neighborhoodId);

    int calculateTagPages(String postURN, long neighborhoodId, int size);

}
