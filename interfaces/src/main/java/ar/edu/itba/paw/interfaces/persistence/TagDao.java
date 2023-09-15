package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Tag;

import java.util.List;
import java.util.Optional;

public interface TagDao {
    Optional<List<Tag>> findTagsByPostId(long id);

    List<Tag> getTags();
    Tag createTag(String name);

}
