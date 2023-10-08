package ar.edu.itba.paw.persistence;

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

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:hsqlValueCleanUp.sql")
public class NeighborhoodDaoImplTest {

    private JdbcTemplate jdbcTemplate;
    private TestInsertionUtils testInsertionUtils;
    private NeighborhoodDaoImpl neighborhoodDao;

    private String NEIGHBORHOODS_NAME = "Testing Create Neighborhood";

    @Autowired
    private DataSource ds;

    @Before
    public void SetUp(){
        jdbcTemplate = new JdbcTemplate(ds);
        neighborhoodDao = new NeighborhoodDaoImpl(ds);
        testInsertionUtils = new TestInsertionUtils(jdbcTemplate, ds);
    }

    @Test
    public void testCreateNeighborhood() {
        // Pre Conditions

        // Exercise
        Neighborhood nh = neighborhoodDao.createNeighborhood(NEIGHBORHOODS_NAME);

        // Validations & Post Conditions
        assertNotNull(nh);
        assertEquals(NEIGHBORHOODS_NAME, nh.getName());
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.neighborhoods.name()));
    }

    @Test
    public void testFindByNeighborhoodByInvalidId(){
        // Pre Conditions

        // Exercise
        Optional<Neighborhood> nh = neighborhoodDao.findNeighborhoodById(1);

        // Validations & Post Conditions
        assertFalse(nh.isPresent());
    }

    @Test
    public void testFindByNeighborhoodByValidId(){
        // Pre Conditions
        Number nhKey = testInsertionUtils.createNeighborhood();

        // Exercise
        Optional<Neighborhood> nh = neighborhoodDao.findNeighborhoodById(nhKey.longValue());

        // Validations & Post Conditions
        assertTrue(nh.isPresent());
        assertEquals(nhKey.longValue(), nh.get().getNeighborhoodId());
    }
}
