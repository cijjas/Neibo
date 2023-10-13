package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Channel;
import ar.edu.itba.paw.persistence.config.TestConfig;
import enums.Table;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import java.util.List;
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
        long chKey = testInsertionUtils.createChannel();

        // Exercise
        Optional<Channel> ch = channelDao.findChannelById(chKey);

        // Validations & Post Conditions
        assertTrue(ch.isPresent());
        assertEquals(chKey, ch.get().getChannelId());
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
        long chKey = testInsertionUtils.createChannel();
        long nhKey = testInsertionUtils.createNeighborhood();
        testInsertionUtils.createNeighborhoodChannelMapping(nhKey, chKey);

        // Exercise
        List<Channel> channels = channelDao.getChannels(nhKey);

        // Validations & Post Conditions
        assertEquals(1, channels.size());
    }

    @Test
    public void testGetNoChannels() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();

        // Exercise
        List<Channel> channels = channelDao.getChannels(nhKey);

        // Validations & Post Conditions
        assertEquals(0, channels.size());
    }
}
