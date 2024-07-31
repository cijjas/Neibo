package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.TagMapping;

import java.util.List;

public interface TagMappingDao {

    // ---------------------------------------------- TAG MAPPING INSERT ------------------------------------------------------
    TagMapping createTagMappingDao(long tagId, long neighborhoodId);

    // ---------------------------------------------- TAG MAPPING SELECT ------------------------------------------------------

    List<TagMapping> getTagMappings(Long tagId, Long neighborhoodId, int page, int size);

    int tagMappingsCount(Long tagId, Long neighborhoodId);

    // ---------------------------------------------- TAG MAPPING DELETE ------------------------------------------------------

    boolean deleteTagMapping(long tagId, long neighborhoodId);
}
