package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.interfaces.persistence.ChannelDao;
import ar.edu.itba.paw.models.Entities.Channel;
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
public class ChannelDaoImpl implements ChannelDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    // -------------------------------------------- CHANNELS INSERT ----------------------------------------------------

    @Override
    public Channel createChannel(final String channelName) {
        LOGGER.debug("Inserting Channel {}", channelName);
        final Channel channel = new Channel.Builder()
                .channel(channelName)
                .build();
        em.persist(channel);
        return channel;
    }
    // -------------------------------------------- CHANNELS SELECT ----------------------------------------------------

    public Optional<Channel> findChannel(long channelId, long neighborhoodId) {
        LOGGER.debug("Selecting Channel with channelId {} in Neighborhood {}", channelId, neighborhoodId);

        TypedQuery<Channel> query = em.createQuery(
                "SELECT c FROM Channel c JOIN c.neighborhoods n WHERE c.channelId = :channelId AND n.neighborhoodId = :neighborhoodId",
                Channel.class
        );

        query.setParameter("channelId", channelId);
        query.setParameter("neighborhoodId", neighborhoodId);

        List<Channel> result = query.getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }


    @Override
    public Optional<Channel> findChannel(String channelName) {
        LOGGER.debug("Selecting Channel with name {}", channelName);

        TypedQuery<Channel> query = em.createQuery("FROM Channel WHERE channel = :channel", Channel.class);
        query.setParameter("channel", channelName);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public List<Channel> getChannels(final long neighborhoodId, final int page, final int size) {
        LOGGER.debug("Selecting paginated Channels from Neighborhood {}", neighborhoodId);

        // Retrieve paginated channel IDs
        TypedQuery<Long> idQuery = em.createQuery(
                "SELECT c.id FROM Channel c JOIN c.neighborhoods n WHERE n.neighborhoodId = :neighborhoodId", Long.class);
        idQuery.setParameter("neighborhoodId", neighborhoodId);
        idQuery.setFirstResult((page - 1) * size);
        idQuery.setMaxResults(size);

        List<Long> channelIds = idQuery.getResultList();

        if (channelIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Retrieve Channels by IDs
        TypedQuery<Channel> query = em.createQuery(
                "SELECT c FROM Channel c WHERE c.id IN :channelIds", Channel.class);
        query.setParameter("channelIds", channelIds);

        return query.getResultList();
    }

    @Override
    public int countChannels(final long neighborhoodId) {
        LOGGER.debug("Counting Channels from Neighborhood {}", neighborhoodId);

        TypedQuery<Integer> query = em.createQuery(
                "SELECT COUNT(c) FROM Channel c JOIN c.neighborhoods n WHERE n.neighborhoodId = :neighborhoodId", Integer.class);
        query.setParameter("neighborhoodId", neighborhoodId);

        return query.getSingleResult();
    }

    // -------------------------------------------- CHANNELS DELETE ----------------------------------------------------

    @Override
    public boolean deleteChannel(long channelId) {
        LOGGER.info("Deleting Channel {}", channelId);

        Channel channel = em.find(Channel.class, channelId);
        if (channel == null) {
            return false;
        }

        em.remove(channel);
        return true;
    }

}
