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

import java.security.acl.NotOwnerException;

import static ar.edu.itba.paw.persistence.TestConstants.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class ChannelMappingDaoImplTest {

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
