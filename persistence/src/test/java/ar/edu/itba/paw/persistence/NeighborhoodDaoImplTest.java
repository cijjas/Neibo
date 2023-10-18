package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Neighborhood;
import ar.edu.itba.paw.persistence.config.TestConfig;
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
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Sql("classpath:hsqlValueCleanUp.sql")
public class NeighborhoodDaoImplTest {

    private final String NEIGHBORHOOD_NAME = "Testing Create Neighborhood";
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    private NeighborhoodDaoImpl neighborhoodDao;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        neighborhoodDao = new NeighborhoodDaoImpl(ds);
    }

    @Test
    public void testCreateNeighborhood() {
        // Pre Conditions

        // Exercise
        Neighborhood nh = neighborhoodDao.createNeighborhood(NEIGHBORHOOD_NAME);

        // Validations & Post Conditions
        assertNotNull(nh);
        assertEquals(NEIGHBORHOOD_NAME, nh.getName());
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.neighborhoods.name()));
    }

    @Test
    public void testFindNeighborhoodByValidId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();

        // Exercise
        Optional<Neighborhood> nh = neighborhoodDao.findNeighborhoodById(nhKey);

        // Validations & Post Conditions
        assertTrue(nh.isPresent());
        assertEquals(nhKey, nh.get().getNeighborhoodId());
    }

    @Test
    public void testFindNeighborhoodByInvalidId() {
        // Pre Conditions

        // Exercise
        Optional<Neighborhood> nh = neighborhoodDao.findNeighborhoodById(1);

        // Validations & Post Conditions
        assertFalse(nh.isPresent());
    }

    @Test
    public void testFindNeighborhoodByValidName() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood(NEIGHBORHOOD_NAME);

        // Exercise
        Optional<Neighborhood> nh = neighborhoodDao.findNeighborhoodByName(NEIGHBORHOOD_NAME);

        // Validations & Post Conditions
        assertTrue(nh.isPresent());
        assertEquals(nhKey, nh.get().getNeighborhoodId());
    }

    @Test
    public void testFindNeighborhoodByInvalidName() {
        // Pre Conditions

        // Exercise
        Optional<Neighborhood> nh = neighborhoodDao.findNeighborhoodByName(NEIGHBORHOOD_NAME);

        // Validations & Post Conditions
        assertFalse(nh.isPresent());
    }

    @Test
    public void testGetNeighborhoods() {
        // Pre Conditions
        testInserter.createNeighborhood();

        // Exercise
        neighborhoodDao.getNeighborhoods();

        // Validations & Post Conditions
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.neighborhoods.name()));
    }

    @Test
    public void testGetNoNeighborhoods() {
        // Pre Conditions

        // Exercise
        neighborhoodDao.getNeighborhoods();

        // Validations & Post Conditions
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.neighborhoods.name()));
    }
}
