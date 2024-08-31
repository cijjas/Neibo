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

import static ar.edu.itba.paw.persistence.TestConstants.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class BaseChannelDaoImplTest {

    private final String CHANNEL_NAME_1 = "Channel Name 1";
    private final String CHANNEL_NAME_2 = "Channel Name 2";
    private final String CHANNEL_NAME_3 = "Channel Name 3";

    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ChannelDaoImpl channelDaoImpl;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    // ------------------------------------------------- CREATE --------------------------------------------------------

    @Test
    public void create_valid() {
        // Pre Conditions

        // Exercise
        Channel channel = channelDaoImpl.createChannel(CHANNEL_NAME_1);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(channel);
        assertEquals(CHANNEL_NAME_1, channel.getChannel());
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.channels.name()));
    }

    // -------------------------------------------------- FINDS --------------------------------------------------------

    @Test
    public void find_channelId_neighborhoodId_valid() {
        // Pre Conditions
        long chKey = testInserter.createChannel();
        long nhKey = testInserter.createNeighborhood();
        testInserter.createChannelMapping(nhKey, chKey);

        // Exercise
        Optional<Channel> optionalChannel = channelDaoImpl.findChannel(chKey, nhKey);

        // Validations & Post Conditions
        assertTrue(optionalChannel.isPresent());
        assertEquals(chKey, optionalChannel.get().getChannelId().longValue());
    }

    @Test
    public void find_channelId_neighborhoodId_invalid_channelId() {
        // Pre Conditions
        long chKey = testInserter.createChannel();
        long nhKey = testInserter.createNeighborhood();
        testInserter.createChannelMapping(nhKey, chKey);

        // Exercise
        Optional<Channel> optionalChannel = channelDaoImpl.findChannel(INVALID_ID, nhKey);

        // Validations & Post Conditions
        assertFalse(optionalChannel.isPresent());
    }

    @Test
    public void find_channelId_neighborhoodId_invalid_neighborhoodId() {
        // Pre Conditions
        long chKey = testInserter.createChannel();
        long nhKey = testInserter.createNeighborhood();
        testInserter.createChannelMapping(nhKey, chKey);

        // Exercise
        Optional<Channel> optionalChannel = channelDaoImpl.findChannel(chKey, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalChannel.isPresent());
    }

    @Test
    public void find_channelId_neighborhoodId_invalid_channelId_neighborhoodId() {
        // Pre Conditions
        long chKey = testInserter.createChannel();
        long nhKey = testInserter.createNeighborhood();
        testInserter.createChannelMapping(nhKey, chKey);

        // Exercise
        Optional<Channel> optionalChannel = channelDaoImpl.findChannel(INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalChannel.isPresent());
    }

    @Test
    public void find_channelName_valid() {
        // Pre Conditions
        testInserter.createChannel(CHANNEL_NAME_1);

        // Exercise
        Optional<Channel> optionalChannel = channelDaoImpl.findChannel(CHANNEL_NAME_1);

        // Validations & Post Conditions
        assertTrue(optionalChannel.isPresent());
        assertEquals(CHANNEL_NAME_1, optionalChannel.get().getChannel());
    }

    @Test
    public void find_channelName_invalid_channelName() {
        // Pre Conditions
        testInserter.createChannel(CHANNEL_NAME_1);

        // Exercise
        Optional<Channel> optionalChannel = channelDaoImpl.findChannel(INVALID_STRING_ID);

        // Validations & Post Conditions
        assertFalse(optionalChannel.isPresent());
    }

    // -------------------------------------------------- GETS ---------------------------------------------------------

    @Test
    public void get() {
        // Pre Conditions
        long nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        long chKey1 = testInserter.createChannel(CHANNEL_NAME_1);
        long chKey2 = testInserter.createChannel(CHANNEL_NAME_2);
        testInserter.createChannelMapping(nhKey1, chKey1);
        testInserter.createChannelMapping(nhKey2, chKey2);

        // Exercise
        List<Channel> channelList = channelDaoImpl.getChannels(nhKey1, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, channelList.size());
    }

    @Test
    public void get_empty() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();

        // Exercise
        List<Channel> channelList = channelDaoImpl.getChannels(nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertTrue(channelList.isEmpty());
    }

    // ---------------------------------------------- PAGINATION -------------------------------------------------------

    @Test
    public void get_pagination() {
        // Pre Conditions
        long nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long chKey1 = testInserter.createChannel(CHANNEL_NAME_1);
        long chKey2 = testInserter.createChannel(CHANNEL_NAME_2);
        long chKey3 = testInserter.createChannel(CHANNEL_NAME_3);
        testInserter.createChannelMapping(nhKey1, chKey1);
        testInserter.createChannelMapping(nhKey1, chKey2);
        testInserter.createChannelMapping(nhKey1, chKey3);

        // Exercise
        List<Channel> channelList = channelDaoImpl.getChannels(nhKey1, TEST_PAGE, TEST_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, channelList.size());
    }

    // ------------------------------------------------- COUNTS ---------------------------------------------------------

    @Test
    public void count() {
        // Pre Conditions
        long chKey1 = testInserter.createChannel(CHANNEL_NAME_1);
        long nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long chKey2 = testInserter.createChannel(CHANNEL_NAME_2);
        long nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        testInserter.createChannelMapping(nhKey1, chKey1);
        testInserter.createChannelMapping(nhKey2, chKey2);

        // Exercise
        int countChannels = channelDaoImpl.countChannels(nhKey1);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, countChannels);
    }

    @Test
    public void count_empty() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();

        // Exercise
        int countChannels = channelDaoImpl.countChannels(nhKey);

        // Validations & Post Conditions
        assertEquals(NO_ELEMENTS, countChannels);
    }

    // ------------------------------------------------ DELETES --------------------------------------------------------

    @Test
    public void delete_channelId_valid() {
        // Pre Conditions
        long cKey = testInserter.createChannel(CHANNEL_NAME_1);

        // Exercise
        boolean deleted = channelDaoImpl.deleteChannel(cKey);

        // Validations & Post Conditions
        em.flush();
        assertTrue(deleted);
        assertEquals(NO_ELEMENTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.channels.name()));
    }

    @Test
    public void delete_channelId_invalid_channelId() {
        // Pre Conditions
        long cKey = testInserter.createChannel(CHANNEL_NAME_1);

        // Exercise
        boolean deleted = channelDaoImpl.deleteChannel(INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }
}
