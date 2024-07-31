package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.TagMapping;

import java.util.List;

public interface TagMappingDao {
    TagMapping createTagMappingDao(long tagId, long neighborhoodId);

    boolean deleteTagMapping(long tagId, long neighborhoodId);

    List<TagMapping> getTagMappings(Long tagId, Long neighborhoodId, int page, int size);

    int tagMappingsCount(Long tagId, Long neighborhoodId);
}
