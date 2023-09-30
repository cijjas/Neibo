package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.ChannelDao;
import ar.edu.itba.paw.models.Channel;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ChannelDaoImpl implements ChannelDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final String CHANNELS = "SELECT * FROM channels ";
    private final String CHANNELS_JOIN_NEIGHBORHOODS =
            "SELECT distinct c.* \n" +
            "FROM channels c JOIN neighborhoods_channels nc ON c.channelid = nc.channelid JOIN neighborhoods n ON n.neighborhoodid = nc.neighborhoodid ";

    @Autowired
    public ChannelDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("channelid")
                .withTableName("channels");
    }

    // -------------------------------------------- CHANNELS INSERT ----------------------------------------------------

    @Override
    public Channel createChannel(String channel) {
        Map<String, Object> data = new HashMap<>();
        data.put("channel", channel);

        final Number key = jdbcInsert.executeAndReturnKey(data);
        return new Channel.Builder()
                .channelId(key.longValue())
                .channel(channel)
                .build();
    }

    // -------------------------------------------- CHANNELS SELECT ----------------------------------------------------

    private static final RowMapper<Channel> ROW_MAPPER = (rs, rowNum) ->
            new Channel.Builder()
                    .channelId(rs.getLong("channelid"))
                    .channel(rs.getString("channel"))
                    .build();

    @Override
    public Optional<Channel> findChannelById(long channelId) {
        final List<Channel> list = jdbcTemplate.query(CHANNELS + " WHERE channelid = ?", ROW_MAPPER, channelId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public Optional<Channel> findChannelByName(String channelName) {
        final List<Channel> list = jdbcTemplate.query(CHANNELS + " WHERE channel = ?", ROW_MAPPER, channelName);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public List<Channel> getChannels(final long neighborhoodId) {
        return jdbcTemplate.query(CHANNELS_JOIN_NEIGHBORHOODS + " WHERE n.neighborhoodid = ?", ROW_MAPPER, neighborhoodId);
    }

}
