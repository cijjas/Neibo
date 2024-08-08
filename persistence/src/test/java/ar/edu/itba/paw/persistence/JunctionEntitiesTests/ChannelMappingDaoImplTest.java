package ar.edu.itba.paw.persistence.JunctionEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Entities.ChannelMapping;
import ar.edu.itba.paw.persistence.JunctionDaos.ChannelMappingDaoImpl;
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

import static ar.edu.itba.paw.persistence.TestConstants.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class ChannelMappingDaoImplTest {

    private final String CHANNEL_NAME_1 = "Channel Name 1";
    private final String CHANNEL_NAME_2 = "Channel Name 2";

    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ChannelMappingDaoImpl channelMappingDaoImpl;
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
        long chKey = testInserter.createChannel();
        long nhKey = testInserter.createNeighborhood();

        // Exercise
        ChannelMapping channelMapping = channelMappingDaoImpl.createChannelMapping(chKey, nhKey);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(channelMapping);
        assertEquals(chKey, channelMapping.getChannel().getChannelId().longValue());
        assertEquals(nhKey, channelMapping.getNeighborhood().getNeighborhoodId().longValue());
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.neighborhoods_channels.name()));
    }

    // -------------------------------------------------- GETS ---------------------------------------------------------

    @Test
    public void get() {
        // Pre conditions
        long nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        long chKey1 = testInserter.createChannel(CHANNEL_NAME_1);
        long chKey2 = testInserter.createChannel(CHANNEL_NAME_2);
        testInserter.createChannelMapping(nhKey1, chKey1);
        testInserter.createChannelMapping(nhKey2, chKey2);
        testInserter.createChannelMapping(nhKey1, chKey2);

        // Exercise
        List<ChannelMapping> channelMappingList = channelMappingDaoImpl.getChannelMappings(EMPTY_FIELD, EMPTY_FIELD, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(THREE_ELEMENTS, channelMappingList.size());
    }

    @Test
    public void get_channelId() {
        // Pre conditions
        long nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        long chKey1 = testInserter.createChannel(CHANNEL_NAME_1);
        long chKey2 = testInserter.createChannel(CHANNEL_NAME_2);
        testInserter.createChannelMapping(nhKey1, chKey1);
        testInserter.createChannelMapping(nhKey2, chKey2);
        testInserter.createChannelMapping(nhKey1, chKey2);

        // Exercise
        List<ChannelMapping> channelMappingList = channelMappingDaoImpl.getChannelMappings(chKey2, EMPTY_FIELD, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, channelMappingList.size());
    }


    @Test
    public void get_neighborhoodId() {
        // Pre conditions
        long nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        long chKey1 = testInserter.createChannel(CHANNEL_NAME_1);
        long chKey2 = testInserter.createChannel(CHANNEL_NAME_2);
        testInserter.createChannelMapping(nhKey1, chKey1);
        testInserter.createChannelMapping(nhKey2, chKey2);
        testInserter.createChannelMapping(nhKey1, chKey2);

        // Exercise
        List<ChannelMapping> channelMappingList = channelMappingDaoImpl.getChannelMappings(EMPTY_FIELD, nhKey1, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, channelMappingList.size());
    }

    @Test
    public void get_channelId_neighborhoodId() {
        // Pre conditions
        long nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        long chKey1 = testInserter.createChannel(CHANNEL_NAME_1);
        long chKey2 = testInserter.createChannel(CHANNEL_NAME_2);
        testInserter.createChannelMapping(nhKey1, chKey1);
        testInserter.createChannelMapping(nhKey2, chKey2);
        testInserter.createChannelMapping(nhKey1, chKey2);

        // Exercise
        List<ChannelMapping> channelMappingList = channelMappingDaoImpl.getChannelMappings(chKey1, nhKey1, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, channelMappingList.size());
    }

    @Test
    public void get_empty() {
        // Pre conditions
        long nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        long chKey1 = testInserter.createChannel(CHANNEL_NAME_1);
        long chKey2 = testInserter.createChannel(CHANNEL_NAME_2);

        // Exercise
        List<ChannelMapping> channelMappingList = channelMappingDaoImpl.getChannelMappings(EMPTY_FIELD, EMPTY_FIELD, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertTrue(channelMappingList.isEmpty());
    }

    // ---------------------------------------------- PAGINATION -------------------------------------------------------

    @Test
    public void get_pagination() {
        // Pre conditions
        long nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        long chKey1 = testInserter.createChannel(CHANNEL_NAME_1);
        long chKey2 = testInserter.createChannel(CHANNEL_NAME_2);
        testInserter.createChannelMapping(nhKey1, chKey1);
        testInserter.createChannelMapping(nhKey2, chKey2);
        testInserter.createChannelMapping(nhKey1, chKey2);

        // Exercise
        List<ChannelMapping> channelMappingList = channelMappingDaoImpl.getChannelMappings(EMPTY_FIELD, EMPTY_FIELD, TEST_PAGE, TEST_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, channelMappingList.size());
    }

    // ------------------------------------------------- COUNTS ---------------------------------------------------------

    @Test
    public void count() {
        // Pre conditions
        long nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        long chKey1 = testInserter.createChannel(CHANNEL_NAME_1);
        long chKey2 = testInserter.createChannel(CHANNEL_NAME_2);
        testInserter.createChannelMapping(nhKey1, chKey1);
        testInserter.createChannelMapping(nhKey2, chKey2);
        testInserter.createChannelMapping(nhKey1, chKey2);

        // Exercise
        int countChannelMappings = channelMappingDaoImpl.channelMappingsCount(EMPTY_FIELD, EMPTY_FIELD);

        // Validations & Post Conditions
        assertEquals(THREE_ELEMENTS, countChannelMappings);
    }

    @Test
    public void count_channelId() {
        // Pre conditions
        long nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        long chKey1 = testInserter.createChannel(CHANNEL_NAME_1);
        long chKey2 = testInserter.createChannel(CHANNEL_NAME_2);
        testInserter.createChannelMapping(nhKey1, chKey1);
        testInserter.createChannelMapping(nhKey2, chKey2);
        testInserter.createChannelMapping(nhKey1, chKey2);

        // Exercise
        int countChannelMapping = channelMappingDaoImpl.channelMappingsCount(chKey2, EMPTY_FIELD);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, countChannelMapping);
    }

    @Test
    public void count_neighborhoodId() {
        // Pre conditions
        long nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        long chKey1 = testInserter.createChannel(CHANNEL_NAME_1);
        long chKey2 = testInserter.createChannel(CHANNEL_NAME_2);
        testInserter.createChannelMapping(nhKey1, chKey1);
        testInserter.createChannelMapping(nhKey2, chKey2);
        testInserter.createChannelMapping(nhKey1, chKey2);

        // Exercise
        int countChannelMapping = channelMappingDaoImpl.channelMappingsCount(EMPTY_FIELD, nhKey1);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, countChannelMapping);
    }

    @Test
    public void count_channelId_neighborhoodId() {
        // Pre conditions
        long nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        long chKey1 = testInserter.createChannel(CHANNEL_NAME_1);
        long chKey2 = testInserter.createChannel(CHANNEL_NAME_2);
        testInserter.createChannelMapping(nhKey1, chKey1);
        testInserter.createChannelMapping(nhKey2, chKey2);
        testInserter.createChannelMapping(nhKey1, chKey2);

        // Exercise
        int countChannelMapping = channelMappingDaoImpl.channelMappingsCount(chKey1, nhKey1);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, countChannelMapping);
    }

    @Test
    public void count_empty() {
        // Pre conditions
        long nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        long chKey1 = testInserter.createChannel(CHANNEL_NAME_1);
        long chKey2 = testInserter.createChannel(CHANNEL_NAME_2);

        // Exercise
        int countChannelMapping = channelMappingDaoImpl.channelMappingsCount(EMPTY_FIELD, EMPTY_FIELD);

        // Validations & Post Conditions
        assertEquals(NO_ELEMENTS, countChannelMapping);
    }

    // ------------------------------------------------ DELETES --------------------------------------------------------

    @Test
    public void delete_channelId_neighborhoodId_valid() {
        // Pre Conditions
        long chKey = testInserter.createChannel();
        long nhKey = testInserter.createNeighborhood();
        testInserter.createChannelMapping(nhKey, chKey);

        // Exercise
        boolean deleted = channelMappingDaoImpl.deleteChannelMapping(chKey, nhKey);

        // Validations & Post Conditions
        em.flush();
        assertTrue(deleted);
        assertEquals(NO_ELEMENTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.neighborhoods_channels.name()));
    }

    @Test
    public void delete_channelId_neighborhoodId_invalid_channelId() {
        // Pre Conditions
        long chKey = testInserter.createChannel();
        long nhKey = testInserter.createNeighborhood();
        testInserter.createChannelMapping(nhKey, chKey);

        // Exercise
        boolean deleted = channelMappingDaoImpl.deleteChannelMapping(INVALID_ID, nhKey);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }

    @Test
    public void delete_channelId_neighborhoodId_invalid_neighborhoodId() {
        // Pre Conditions
        long chKey = testInserter.createChannel();
        long nhKey = testInserter.createNeighborhood();
        testInserter.createChannelMapping(nhKey, chKey);

        // Exercise
        boolean deleted = channelMappingDaoImpl.deleteChannelMapping(chKey, INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }

    @Test
    public void delete_channelId_neighborhoodId_invalid_channelId_neighborhoodId() {
        // Pre Conditions
        long chKey = testInserter.createChannel();
        long nhKey = testInserter.createNeighborhood();
        testInserter.createChannelMapping(nhKey, chKey);

        // Exercise
        boolean deleted = channelMappingDaoImpl.deleteChannelMapping(INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }
}
