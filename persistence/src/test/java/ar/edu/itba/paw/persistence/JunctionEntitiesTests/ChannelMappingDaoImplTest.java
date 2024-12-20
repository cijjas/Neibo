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
import java.util.Optional;

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
        ChannelMapping channelMapping = channelMappingDaoImpl.createChannelMapping(nhKey, chKey);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(channelMapping);
        assertEquals(chKey, channelMapping.getChannel().getChannelId().longValue());
        assertEquals(nhKey, channelMapping.getNeighborhood().getNeighborhoodId().longValue());
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.neighborhoods_channels.name()));
    }

    // -------------------------------------------------- FINDS --------------------------------------------------------

    @Test
    public void find_neighborhoodId_tagId_valid(){
        // Pre conditions
        long nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long chKey1 = testInserter.createChannel(CHANNEL_NAME_1);
        testInserter.createChannelMapping(nhKey1, chKey1);

        // Exercise
        Optional<ChannelMapping> optionalChannelMapping= channelMappingDaoImpl.findChannelMapping(nhKey1, chKey1);

        // Validations & Post Conditions
        assertTrue(optionalChannelMapping.isPresent());
        assertEquals(nhKey1, optionalChannelMapping.get().getNeighborhood().getNeighborhoodId().longValue());
        assertEquals(chKey1, optionalChannelMapping.get().getChannel().getChannelId().longValue());
    }

    @Test
    public void find_neighborhoodId_tagId_invalid_neighborhoodId(){
        // Pre conditions
        long nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long chKey1 = testInserter.createChannel(CHANNEL_NAME_1);
        testInserter.createChannelMapping(nhKey1, chKey1);

        // Exercise
        Optional<ChannelMapping> optionalChannelMapping= channelMappingDaoImpl.findChannelMapping(INVALID_ID, chKey1);

        // Validations & Post Conditions
        assertFalse(optionalChannelMapping.isPresent());
    }

    @Test
    public void find_neighborhoodId_tagId_invalid_tagId(){
        // Pre conditions
        long nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long chKey1 = testInserter.createChannel(CHANNEL_NAME_1);
        testInserter.createChannelMapping(nhKey1, chKey1);

        // Exercise
        Optional<ChannelMapping> optionalChannelMapping= channelMappingDaoImpl.findChannelMapping(nhKey1, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalChannelMapping.isPresent());
    }

    @Test
    public void find_neighborhoodId_tagId_invalid_neighborhoodId_tagId(){
        // Pre conditions
        long nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long chKey1 = testInserter.createChannel(CHANNEL_NAME_1);
        testInserter.createChannelMapping(nhKey1, chKey1);

        // Exercise
        Optional<ChannelMapping> optionalChannelMapping= channelMappingDaoImpl.findChannelMapping(INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalChannelMapping.isPresent());
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
        List<ChannelMapping> channelMappingList = channelMappingDaoImpl.getChannelMappings(EMPTY_FIELD, chKey2, BASE_PAGE, BASE_PAGE_SIZE);

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
        List<ChannelMapping> channelMappingList = channelMappingDaoImpl.getChannelMappings(nhKey1, EMPTY_FIELD, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, channelMappingList.size());
    }

    @Test
    public void get_neighborhoodId_channelId() {
        // Pre conditions
        long nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        long chKey1 = testInserter.createChannel(CHANNEL_NAME_1);
        long chKey2 = testInserter.createChannel(CHANNEL_NAME_2);
        testInserter.createChannelMapping(nhKey1, chKey1);
        testInserter.createChannelMapping(nhKey2, chKey2);
        testInserter.createChannelMapping(nhKey1, chKey2);

        // Exercise
        List<ChannelMapping> channelMappingList = channelMappingDaoImpl.getChannelMappings(nhKey1, chKey1, BASE_PAGE, BASE_PAGE_SIZE);

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

    // ------------------------------------------------ DELETES --------------------------------------------------------

    @Test
    public void delete_neighborhoodId_channelId_valid() {
        // Pre Conditions
        long chKey = testInserter.createChannel();
        long nhKey = testInserter.createNeighborhood();
        testInserter.createChannelMapping(nhKey, chKey);

        // Exercise
        boolean deleted = channelMappingDaoImpl.deleteChannelMapping(nhKey, chKey);

        // Validations & Post Conditions
        em.flush();
        assertTrue(deleted);
        assertEquals(NO_ELEMENTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.neighborhoods_channels.name()));
    }

    @Test
    public void delete_neighborhoodId_channelId_invalid_channelId() {
        // Pre Conditions
        long chKey = testInserter.createChannel();
        long nhKey = testInserter.createNeighborhood();
        testInserter.createChannelMapping(nhKey, chKey);

        // Exercise
        boolean deleted = channelMappingDaoImpl.deleteChannelMapping(nhKey, INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }

    @Test
    public void delete_neighborhoodId_channelId_invalid_neighborhoodId() {
        // Pre Conditions
        long chKey = testInserter.createChannel();
        long nhKey = testInserter.createNeighborhood();
        testInserter.createChannelMapping(nhKey, chKey);

        // Exercise
        boolean deleted = channelMappingDaoImpl.deleteChannelMapping(INVALID_ID, chKey);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }

    @Test
    public void delete_neighborhoodId_channelId_invalid_neighborhoodId_channelId() {
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
