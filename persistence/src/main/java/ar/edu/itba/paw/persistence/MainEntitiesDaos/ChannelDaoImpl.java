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
    public Channel createChannel(String channelName) {
        LOGGER.debug("Inserting Channel {}", channelName);
        final Channel channel = new Channel.Builder()
                .channel(channelName)
                .isBase(false)
                .build();
        em.persist(channel);
        return channel;
    }
    // -------------------------------------------- CHANNELS SELECT ----------------------------------------------------

    public Optional<Channel> findChannel(long neighborhoodId, long channelId) {
        LOGGER.debug("Selecting Channel with Neighborhood Id {} and Channel Id {}", neighborhoodId, channelId);

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
    public List<Channel> getChannels(long neighborhoodId, Boolean isBase, int page, int size) {
        LOGGER.debug("Selecting Channels with Neighborhood Id {} and isBase {}", neighborhoodId, isBase);

        StringBuilder idQueryBuilder = new StringBuilder(
                "SELECT c.id FROM Channel c JOIN c.neighborhoods n WHERE n.neighborhoodId = :neighborhoodId"
        );

        if (isBase != null) {
            idQueryBuilder.append(" AND c.isBase = :isBase");
        }

        TypedQuery<Long> idQuery = em.createQuery(idQueryBuilder.toString(), Long.class);
        idQuery.setParameter("neighborhoodId", neighborhoodId);
        if (isBase != null) {
            idQuery.setParameter("isBase", isBase);
        }
        idQuery.setFirstResult((page - 1) * size);
        idQuery.setMaxResults(size);

        List<Long> channelIds = idQuery.getResultList();

        if (channelIds.isEmpty()) {
            return Collections.emptyList();
        }

        TypedQuery<Channel> query = em.createQuery(
                "SELECT c FROM Channel c WHERE c.id IN :channelIds", Channel.class
        );
        query.setParameter("channelIds", channelIds);

        return query.getResultList();
    }

    @Override
    public int countChannels(long neighborhoodId, Boolean isBase) {
        LOGGER.debug("Counting Channels with Neighborhood Id {} and isBase {}", neighborhoodId, isBase);

        // Build the base query
        StringBuilder queryBuilder = new StringBuilder(
                "SELECT COUNT(c) FROM Channel c JOIN c.neighborhoods n WHERE n.neighborhoodId = :neighborhoodId"
        );

        if (isBase != null) {
            queryBuilder.append(" AND c.isBase = :isBase");
        }

        TypedQuery<Long> query = em.createQuery(queryBuilder.toString(), Long.class);
        query.setParameter("neighborhoodId", neighborhoodId);
        if (isBase != null) {
            query.setParameter("isBase", isBase);
        }

        return query.getSingleResult().intValue();
    }
}
