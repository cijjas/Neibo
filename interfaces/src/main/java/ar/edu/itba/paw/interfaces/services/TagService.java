package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Tag;

import java.util.List;
import java.util.Optional;

public interface TagService {
    Optional<List<Tag>> findTags(long id);

}
