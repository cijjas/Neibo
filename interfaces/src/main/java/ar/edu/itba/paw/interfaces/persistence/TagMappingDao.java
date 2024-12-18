package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.TagMapping;

import java.util.List;
import java.util.Optional;

public interface TagMappingDao {

    // ---------------------------------------------- TAG MAPPING INSERT ------------------------------------------------------
    TagMapping createTagMappingDao(long neighborhoodId, long tagId);

    // ---------------------------------------------- TAG MAPPING SELECT ------------------------------------------------------

    Optional<TagMapping> findTagMapping(long neighborhoodId, long tagId);

    List<TagMapping> getTagMappings(Long neighborhoodId, Long tagId, int page, int size);

    int countTagMappings(Long neighborhoodId, Long tagId);

    // ---------------------------------------------- TAG MAPPING DELETE ------------------------------------------------------

    boolean deleteTagMapping(long neighborhoodId, long tagId);
}
