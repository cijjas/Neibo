package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.interfaces.persistence.TagMappingDao;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.models.Entities.Tag;
import ar.edu.itba.paw.models.Entities.TagMapping;
import ar.edu.itba.paw.models.compositeKeys.TagMappingKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class TagMappingDaoImpl implements TagMappingDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(TagMappingDaoImpl.class);
    @PersistenceContext
    private EntityManager em;

    // ----------------------------------------------------------------------------------

    @Override
    public TagMapping createTagMappingDao(long neighborhoodId, long tagId) {
        LOGGER.debug("Inserting Tag Mapping with Tag Id {} and Neighborhood Id {}", tagId, neighborhoodId);

        TagMapping tagMapping = new TagMapping(em.find(Neighborhood.class, neighborhoodId), em.find(Tag.class, tagId));
        em.persist(tagMapping);
        return tagMapping;
    }

    // ----------------------------------------------------------------------------------

    @Override
    public Optional<TagMapping> findTagMapping(long neighborhoodId, long tagId) {
        LOGGER.debug("Selecting Tag Mapping with Neighborhood Id {} and Tag Id {}", neighborhoodId, tagId);

        return Optional.ofNullable(em.find(TagMapping.class, new TagMappingKey(neighborhoodId, tagId)));
    }

    @Override
    public List<TagMapping> getTagMappings(Long neighborhoodId, Long tagId, int page, int size) {
        LOGGER.debug("Selecting Tag Mappings with Neighborhood Id {} and Tag Id {}", neighborhoodId, tagId);

        TypedQuery<TagMappingKey> idQuery = null;
        StringBuilder queryBuilder = new StringBuilder("SELECT tm.id FROM TagMapping tm ");

        if (tagId != null && neighborhoodId != null) {
            queryBuilder.append("WHERE tm.tag.tagId = :tagId AND tm.neighborhood.neighborhoodId = :neighborhoodId ");
        } else if (tagId != null) {
            queryBuilder.append("WHERE tm.tag.tagId = :tagId ");
        } else if (neighborhoodId != null) {
            queryBuilder.append("WHERE tm.neighborhood.neighborhoodId = :neighborhoodId ");
        }

        queryBuilder.append("ORDER BY tm.neighborhood.neighborhoodId");

        idQuery = em.createQuery(queryBuilder.toString(), TagMappingKey.class);

        if (tagId != null) {
            idQuery.setParameter("tagId", tagId);
        }
        if (neighborhoodId != null) {
            idQuery.setParameter("neighborhoodId", neighborhoodId);
        }

        idQuery.setFirstResult((page - 1) * size);
        idQuery.setMaxResults(size);

        List<TagMappingKey> ids = idQuery.getResultList();

        if (!ids.isEmpty()) {
            TypedQuery<TagMapping> tagMappingQuery = em.createQuery(
                    "SELECT tm FROM TagMapping tm WHERE tm.id IN :ids ORDER BY tm.neighborhood.neighborhoodId", TagMapping.class);
            tagMappingQuery.setParameter("ids", ids);
            return tagMappingQuery.getResultList();
        }
        return Collections.emptyList();
    }

    @Override
    public int countTagMappings(Long neighborhoodId, Long tagId) {
        LOGGER.debug("Counting Tag Mappings with Neighborhood Id {} and Tag Id {}", neighborhoodId, tagId);

        TypedQuery<TagMappingKey> idQuery = null;
        StringBuilder queryBuilder = new StringBuilder("SELECT tm.id FROM TagMapping tm ");

        if (tagId != null && neighborhoodId != null) {
            queryBuilder.append("WHERE tm.tag.tagId = :tagId AND tm.neighborhood.neighborhoodId = :neighborhoodId ");
        } else if (tagId != null) {
            queryBuilder.append("WHERE tm.tag.tagId = :tagId ");
        } else if (neighborhoodId != null) {
            queryBuilder.append("WHERE tm.neighborhood.neighborhoodId = :neighborhoodId ");
        }

        queryBuilder.append("ORDER BY tm.neighborhood.neighborhoodId");

        idQuery = em.createQuery(queryBuilder.toString(), TagMappingKey.class);

        if (tagId != null) {
            idQuery.setParameter("tagId", tagId);
        }
        if (neighborhoodId != null) {
            idQuery.setParameter("neighborhoodId", neighborhoodId);
        }

        List<TagMappingKey> ids = idQuery.getResultList();

        if (!ids.isEmpty()) {
            TypedQuery<TagMapping> tagMappingQuery = em.createQuery(
                    "SELECT tm FROM TagMapping tm WHERE tm.id IN :ids ORDER BY tm.neighborhood.neighborhoodId", TagMapping.class);
            tagMappingQuery.setParameter("ids", ids);
            return tagMappingQuery.getResultList().size();
        }
        return 0;
    }

    // ----------------------------------------------------------------------------------

    @Override
    public boolean deleteTagMapping(long neighborhoodId, long tagId) {
        LOGGER.debug("Deleting Tag Mapping with Tag Id {} and Neighborhood Id {}", tagId, neighborhoodId);

        TagMapping tagMapping = em.find(TagMapping.class, new TagMappingKey(neighborhoodId, tagId));
        if (tagMapping != null) {
            em.remove(tagMapping);
            return true;
        } else {
            return false;
        }
    }
}
