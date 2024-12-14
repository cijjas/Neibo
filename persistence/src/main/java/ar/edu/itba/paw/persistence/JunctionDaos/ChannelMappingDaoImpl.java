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
    public ChannelMapping createChannelMapping(long channelId, long neighborhoodId) {
        LOGGER.debug("Inserting Channel Mapping for Channel {} for Neighborhood {}", channelId, neighborhoodId);

        ChannelMapping channelMapping = new ChannelMapping(em.find(Neighborhood.class, neighborhoodId), em.find(Channel.class, channelId));
        em.persist(channelMapping);
        return channelMapping;
    }

    // ----------------------------------------------------------------------------------

    @Override
    public Optional<ChannelMapping> findChannelMapping(long channelId, long neighborhoodId) {
        LOGGER.debug("Finding Channel Mapping with Channel id {} in Neighborhood {}", channelId, neighborhoodId);

        return Optional.ofNullable(em.find(ChannelMapping.class, new ChannelMappingKey(channelId, neighborhoodId)));
    }

    @Override
    public List<ChannelMapping> getChannelMappings(Long channelId, Long neighborhoodId, int page, int size) {
        LOGGER.debug("Selecting Channel Mappings by Criteria");

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
    public boolean deleteChannelMapping(long channelId, long neighborhoodId) {
        LOGGER.debug("Deleting ChannelMapping with channelId {} and neighborhoodId {}", channelId, neighborhoodId);

        ChannelMapping channelMapping = em.find(ChannelMapping.class, new ChannelMappingKey(neighborhoodId, channelId));
        if (channelMapping != null) {
            em.remove(channelMapping);
            return true;
        } else {
            return false;
        }
    }
}
