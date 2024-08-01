package ar.edu.itba.paw.persistence.MainEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Entities.Channel;
import ar.edu.itba.paw.persistence.MainEntitiesDaos.ChannelDaoImpl;
import ar.edu.itba.paw.persistence.TestInserter;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static ar.edu.itba.paw.persistence.TestConstants.INVALID_ID;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class ChannelDaoImplTest {

    private final String CHANNEL_NAME_1 = "Channel Name 1";
    private final String CHANNEL_NAME_2 = "Channel Name 2";
    private final String NEIGHBORHOOD_NAME_1 = "Neighborhood Name 1";
    private final String NEIGHBORHOOD_NAME_2 = "Neighborhood Name 2";
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ChannelDaoImpl channelDao;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void create_valid() {
        // Pre Conditions

        // Exercise
        Channel ch = channelDao.createChannel(CHANNEL_NAME_1);

        // Validations & Post Conditions
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.channels.name()));
        assertEquals(CHANNEL_NAME_1, ch.getChannel());
    }

    @Test
    public void find_channelId_neighborhoodId_valid() {
        // Pre Conditions
        long chKey = testInserter.createChannel();
        long nhKey = testInserter.createNeighborhood();
        testInserter.createChannelMapping(nhKey, chKey);

        // Exercise
        Optional<Channel> ch = channelDao.findChannel(chKey, nhKey);

        // Validations & Post Conditions
        assertTrue(ch.isPresent());
        assertEquals(chKey, ch.get().getChannelId().longValue());
    }

    @Test
    public void find_channelId_neighborhoodId_invalid_channelId() {
        // Pre Conditions

        // Exercise
        Optional<Channel> ch = channelDao.findChannel(1, 1);

        // Validations & Post Conditions
        assertFalse(ch.isPresent());
    }

    @Test
    public void find_channelName_valid() {
        // Pre Conditions
        testInserter.createChannel(CHANNEL_NAME_1);

        // Exercise
        Optional<Channel> ch = channelDao.findChannel(CHANNEL_NAME_1);

        // Validations & Post Conditions
        assertTrue(ch.isPresent());
        assertEquals(CHANNEL_NAME_1, ch.get().getChannel());
    }

    @Test
    public void find_channelName_invalid_channelName() {
        // Pre Conditions

        // Exercise
        Optional<Channel> ch = channelDao.findChannel(CHANNEL_NAME_1);

        // Validations & Post Conditions
        assertFalse(ch.isPresent());
    }

    @Test
    public void get_neighborhoodId() {
        // Pre Conditions
        long chKey1 = testInserter.createChannel(CHANNEL_NAME_1);
        long nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long chKey2 = testInserter.createChannel(CHANNEL_NAME_2);
        long nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        testInserter.createChannelMapping(nhKey1, chKey1);
        testInserter.createChannelMapping(nhKey2, chKey2);

        // Exercise
        List<Channel> channels = channelDao.getChannels(nhKey1);

        // Validations & Post Conditions
        assertEquals(1, channels.size());
    }

    @Test
    public void get_empty() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();

        // Exercise
        List<Channel> channels = channelDao.getChannels(nhKey);

        // Validations & Post Conditions
        assertEquals(0, channels.size());
    }

    @Test
	public void delete_channelId_valid() {
	    // Pre Conditions
        long cKey = testInserter.createChannel(CHANNEL_NAME_1);

	    // Exercise
	    boolean deleted = channelDao.deleteChannel(cKey);

	    // Validations & Post Conditions
        em.flush();
	    assertTrue(deleted);
	    assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.channels.name()));
	}

    @Test
	public void delete_channelId_invalid_channelId() {
	    // Pre Conditions

	    // Exercise
	    boolean deleted = channelDao.deleteChannel(INVALID_ID);

	    // Validations & Post Conditions
        em.flush();
	    assertFalse(deleted);
	}
}
