package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Tag;

import java.util.List;
import java.util.Optional;

public interface TagDao {
    Optional<List<Tag>> findTags(long id);
}
