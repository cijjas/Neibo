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

@Repository
public class ChannelMappingDaoImpl implements ChannelMappingDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelMappingDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    // ---------------------------------- NEIGHBORHOODS CHANNELS INSERT ------------------------------------------------

    @Override
    public ChannelMapping createChannelMapping(long channelId, long neighborhoodId) {
        LOGGER.debug("Inserting Channel Mapping");

        ChannelMapping channelMapping = new ChannelMapping(em.find(Neighborhood.class, neighborhoodId), em.find(Channel.class, channelId));
        em.persist(channelMapping);
        return channelMapping;
    }

    // ---------------------------------- NEIGHBORHOODS CHANNELS DELETE ------------------------------------------------

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
