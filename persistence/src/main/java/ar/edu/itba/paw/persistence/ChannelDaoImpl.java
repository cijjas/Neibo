package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.ChannelDao;
import ar.edu.itba.paw.models.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ChannelDaoImpl implements ChannelDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final String CHANNELS = "select * from channels ";

    @Autowired
    public ChannelDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("channelid")
                .withTableName("channels");
    }

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

    private static final RowMapper<Channel> ROW_MAPPER = (rs, rowNum) ->
            new Channel.Builder()
                    .channelId(rs.getLong("channelid"))
                    .channel(rs.getString("channel"))
                    .build();

    @Override
    public List<Channel> getChannels() {
        return jdbcTemplate.query(CHANNELS, ROW_MAPPER);
    }
}
