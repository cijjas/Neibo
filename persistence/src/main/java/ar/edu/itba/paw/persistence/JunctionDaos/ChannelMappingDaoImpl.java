package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.interfaces.persistence.ChannelMappingDao;
import ar.edu.itba.paw.models.Entities.Channel;
import ar.edu.itba.paw.models.Entities.ChannelMapping;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.models.compositeKeys.ChannelMappingKey;
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
public class ChannelMappingDaoImpl implements ChannelMappingDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelMappingDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    // ----------------------------------------------------------------------------------

    @Override
    public ChannelMapping createChannelMapping(long neighborhoodId, long channelId) {
        LOGGER.debug("Inserting Channel Mapping with Neighborhood  Id {} and Channel Id {}", neighborhoodId, channelId);

        ChannelMapping channelMapping = new ChannelMapping(em.find(Neighborhood.class, neighborhoodId), em.find(Channel.class, channelId));
        em.persist(channelMapping);
        return channelMapping;
    }

    // ----------------------------------------------------------------------------------

    @Override
    public Optional<ChannelMapping> findChannelMapping(long neighborhoodId, long channelId) {
        LOGGER.debug("Finding Channel Mapping with Neighborhood Id {} and Channel Id {}", channelId, neighborhoodId);

        return Optional.ofNullable(em.find(ChannelMapping.class, new ChannelMappingKey(neighborhoodId, channelId)));
    }

    @Override
    public List<ChannelMapping> getChannelMappings(Long neighborhoodId, Long channelId, int page, int size) {
        LOGGER.debug("Selecting Channel Mappings with Neighborhood Id {} and Channel Id {}", neighborhoodId, channelId);

        TypedQuery<ChannelMappingKey> idQuery = null;
        StringBuilder queryBuilder = new StringBuilder("SELECT cm.id FROM ChannelMapping cm ");

        if (channelId != null && neighborhoodId != null) {
            queryBuilder.append("WHERE cm.channel.channelId = :channelId AND cm.neighborhood.neighborhoodId = :neighborhoodId ");
        } else if (channelId != null) {
            queryBuilder.append("WHERE cm.channel.channelId = :channelId ");
        } else if (neighborhoodId != null) {
            queryBuilder.append("WHERE cm.neighborhood.neighborhoodId = :neighborhoodId ");
        }

        queryBuilder.append("ORDER BY cm.neighborhood.neighborhoodId");

        idQuery = em.createQuery(queryBuilder.toString(), ChannelMappingKey.class);

        if (channelId != null) {
            idQuery.setParameter("channelId", channelId);
        }
        if (neighborhoodId != null) {
            idQuery.setParameter("neighborhoodId", neighborhoodId);
        }

        idQuery.setFirstResult((page - 1) * size);
        idQuery.setMaxResults(size);

        List<ChannelMappingKey> ids = idQuery.getResultList();

        if (!ids.isEmpty()) {
            TypedQuery<ChannelMapping> channelMappingQuery = em.createQuery(
                    "SELECT cm FROM ChannelMapping cm WHERE cm.id IN :ids ORDER BY cm.neighborhood.neighborhoodId", ChannelMapping.class);
            channelMappingQuery.setParameter("ids", ids);
            return channelMappingQuery.getResultList();
        }
        return Collections.emptyList();
    }

    // ----------------------------------------------------------------------------------

    @Override
    public boolean deleteChannelMapping(long neighborhoodId, long channelId) {
        LOGGER.debug("Deleting Channel Mapping with Neighborhood Id {} and Channel Id {}", neighborhoodId, channelId);

        ChannelMapping channelMapping = em.find(ChannelMapping.class, new ChannelMappingKey(neighborhoodId, channelId));
        if (channelMapping != null) {
            em.remove(channelMapping);
            return true;
        } else {
            return false;
        }
    }
}
