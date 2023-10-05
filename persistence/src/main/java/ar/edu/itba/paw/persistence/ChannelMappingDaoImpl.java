package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.ChannelMappingDao;
import ar.edu.itba.paw.models.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Repository
public class ChannelMappingDaoImpl implements ChannelMappingDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelMappingDaoImpl.class);

    @Autowired
    public ChannelMappingDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("neighborhoods_channels");
    }


    @Override
    public void createChannelMappingDao(long channelId, long neighborhoodId) {
        Map<String, Object> data = new HashMap<>();
        data.put("channelid", channelId);
        data.put("neighborhoodid", neighborhoodId);

        try {
            jdbcInsert.execute(data);
        } catch (DataAccessException ex) {
            LOGGER.error("Error inserting the Channel Mapping", ex);
            throw new InsertionException("An error occurred whilst creating the channel");
        }
    }
}
