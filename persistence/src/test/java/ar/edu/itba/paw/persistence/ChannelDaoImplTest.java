package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Channel;
import ar.edu.itba.paw.models.Neighborhood;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:hsqlValueCleanUp.sql")
public class ChannelDaoImplTest {

    private JdbcTemplate jdbcTemplate;
    private TestInsertionUtils testInsertionUtils;
    private ChannelDaoImpl channelDao;

    private String CHANNEL_NAME = "Not Default Name";

    @Autowired
    private DataSource ds;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        channelDao = new ChannelDaoImpl(ds);
        testInsertionUtils = new TestInsertionUtils(jdbcTemplate, ds);
    }

    @Test
    public void testCreateChannel() {
        // Pre Conditions

        // Exercise
        Channel ch = channelDao.createChannel(CHANNEL_NAME);

        // Validations & Post Conditions
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.channels.name()));
        assertEquals(CHANNEL_NAME, ch.getChannel());
    }

    @Test
    public void testFindChannelById() {
        // Pre Conditions
        Number chKey = testInsertionUtils.createChannel();

        // Exercise
        Optional<Channel> ch = channelDao.findChannelById(chKey.longValue());

        // Validations & Post Conditions
        assertTrue(ch.isPresent());
        assertEquals(chKey.longValue(), ch.get().getChannelId());
    }

    @Test
    public void testFindChannelByName() {
        // Pre Conditions
        testInsertionUtils.createChannel(CHANNEL_NAME);

        // Exercise
        Optional<Channel> ch = channelDao.findChannelByName(CHANNEL_NAME);

        // Validations & Post Conditions
        assertTrue(ch.isPresent());
        assertEquals(CHANNEL_NAME, ch.get().getChannel());
    }

    @Test
    public void testFindChannelByInvalidId() {
        // Pre Conditions

        // Exercise
        Optional<Channel> ch = channelDao.findChannelById(1);

        // Validations & Post Conditions
        assertFalse(ch.isPresent());
    }

    @Test
    public void testFindChannelByInvalidName() {
        // Pre Conditions

        // Exercise
        Optional<Channel> ch = channelDao.findChannelByName(CHANNEL_NAME);

        // Validations & Post Conditions
        assertFalse(ch.isPresent());
    }

    @Test
    public void testGetChannels() {
        // Pre Conditions
        Number chKey = testInsertionUtils.createChannel();
        Number nhKey = testInsertionUtils.createNeighborhood();
        testInsertionUtils.createNeighborhoodChannelMapping(nhKey, chKey);

        // Exercise
        List<Channel> channels = channelDao.getChannels(nhKey.longValue());

        // Validations & Post Conditions
        assertEquals(1, channels.size());
    }

    @Test
    public void testGetNoChannels() {
        // Pre Conditions
        Number nhKey = testInsertionUtils.createNeighborhood();

        // Exercise
        List<Channel> channels = channelDao.getChannels(nhKey.longValue());

        // Validations & Post Conditions
        assertEquals(0, channels.size());
    }
}
