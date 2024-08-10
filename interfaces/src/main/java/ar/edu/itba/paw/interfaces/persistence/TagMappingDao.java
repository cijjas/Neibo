package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.TagMapping;

import java.util.List;
import java.util.Optional;

public interface TagMappingDao {

    // ---------------------------------------------- TAG MAPPING INSERT ------------------------------------------------------
    TagMapping createTagMappingDao(long tagId, long neighborhoodId);

    // ---------------------------------------------- TAG MAPPING SELECT ------------------------------------------------------

    Optional<TagMapping> findTagMapping(long tagId, long neighborhoodId);

    List<TagMapping> getTagMappings(Long tagId, Long neighborhoodId, int page, int size);

    int countTagMappings(Long tagId, Long neighborhoodId);

    // ---------------------------------------------- TAG MAPPING DELETE ------------------------------------------------------

    boolean deleteTagMapping(long tagId, long neighborhoodId);
}
