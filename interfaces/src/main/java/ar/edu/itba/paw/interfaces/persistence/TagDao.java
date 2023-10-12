package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Tag;

import java.util.List;
import java.util.Optional;

public interface TagDao {

    // ---------------------------------------------- TAGS INSERT ------------------------------------------------------

    Tag createTag(String name);

    // ---------------------------------------------- TAGS SELECT ------------------------------------------------------

    List<Tag> findTagsByPostId(long id);

    List<Tag> getTags(long neighborhoodId);

    List<Tag> getAllTags();
}
