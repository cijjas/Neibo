package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.enums.Table;
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

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:hsqlValueCleanUp.sql")
public class ChannelMappingDaoImplTest {

    private JdbcTemplate jdbcTemplate;
    private TestInsertionUtils testInsertionUtils;
    private ChannelMappingDaoImpl channelMappingDao;

    @Autowired
    private DataSource ds;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        channelMappingDao = new ChannelMappingDaoImpl(ds);
        testInsertionUtils = new TestInsertionUtils(jdbcTemplate, ds);
    }

    @Test
    public void testCreateChannelMapping() {
        // Pre Conditions
        long chKey = testInsertionUtils.createChannel();
        long nhKey = testInsertionUtils.createNeighborhood();

        // Exercise
        channelMappingDao.createChannelMapping(chKey, nhKey);

        // Validations & Post Conditions
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.neighborhoods_channels.name()));
    }
}
