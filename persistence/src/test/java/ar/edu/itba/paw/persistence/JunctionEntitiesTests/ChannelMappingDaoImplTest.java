package ar.edu.itba.paw.persistence.JunctionEntitiesTests;

import ar.edu.itba.paw.enums.Table;
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

import javax.sql.DataSource;

import static org.junit.Assert.assertEquals;

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
    private ChannelMappingDaoImpl channelMappingDao;


    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        channelMappingDao = new ChannelMappingDaoImpl(ds);
    }

    @Test
    public void testCreateChannelMapping() {
        // Pre Conditions
        long chKey = testInserter.createChannel();
        long nhKey = testInserter.createNeighborhood();

        // Exercise
        channelMappingDao.createChannelMapping(chKey, nhKey);

        // Validations & Post Conditions
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.neighborhoods_channels.name()));
    }
}
