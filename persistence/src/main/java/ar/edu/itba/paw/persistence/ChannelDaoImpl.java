package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.ChannelDao;
import ar.edu.itba.paw.models.Channel;
import ar.edu.itba.paw.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ChannelDaoImpl implements ChannelDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    private static final RowMapper<Channel> ROW_MAPPER = (rs, rowNum) ->
            new Channel.Builder()
                    .channelId(rs.getLong("channelid"))
                    .channel(rs.getString("channel"))
                    .build();
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final String CHANNELS_JOIN_NEIGHBORHOODS =
            "SELECT distinct c.* \n" +
                    "FROM channels c " +
                    "INNER JOIN neighborhoods_channels nc ON c.channelid = nc.channelid " +
                    "INNER JOIN neighborhoods n ON n.neighborhoodid = nc.neighborhoodid ";

    @Autowired
    public ChannelDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("channelid")
                .withTableName("channels");
    }


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

    public Optional<Channel> findChannelById(long channelId) {
        LOGGER.debug("Selecting Channel with channelId {}", channelId);
        return Optional.ofNullable(em.find(Channel.class, channelId));
    }

    @Override
    public Optional<Channel> findChannelByName(String channelName) {
        LOGGER.debug("Selecting Channel with name {}", channelName);
        TypedQuery<Channel> query = em.createQuery("FROM Channel WHERE channel = :channel", Channel.class);
        query.setParameter("channel", channelName);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public List<Channel> getChannels(final long neighborhoodId) {
        LOGGER.debug("Selecting Channels from Neighborhood {}", neighborhoodId);
        return jdbcTemplate.query(CHANNELS_JOIN_NEIGHBORHOODS + " WHERE n.neighborhoodid = ?", ROW_MAPPER, neighborhoodId);
    }
}
