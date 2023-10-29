package ar.edu.itba.paw.persistence.JunctionDaos;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.ChannelMappingDao;
import ar.edu.itba.paw.models.JunctionEntities.ChannelMapping;
import ar.edu.itba.paw.models.MainEntities.Channel;
import ar.edu.itba.paw.models.MainEntities.Neighborhood;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Repository
public class ChannelMappingDaoImpl implements ChannelMappingDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelMappingDaoImpl.class);
    @PersistenceContext
    private EntityManager em;
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public ChannelMappingDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("neighborhoods_channels");
    }

    // ---------------------------------- NEIGHBORHOODS_CHANNELS INSERT ------------------------------------------------

    @Override
    public ChannelMapping createChannelMapping(long channelId, long neighborhoodId) {
        LOGGER.debug("Inserting Channel Mapping");
        ChannelMapping channelMapping = new ChannelMapping(em.find(Neighborhood.class, neighborhoodId), em.find(Channel.class, channelId));
        em.persist(channelMapping);
        return channelMapping;
    }
}
