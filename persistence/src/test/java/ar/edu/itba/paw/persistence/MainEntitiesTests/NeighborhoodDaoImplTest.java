package ar.edu.itba.paw.persistence.MainEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.persistence.MainEntitiesDaos.NeighborhoodDaoImpl;
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
public class NeighborhoodDaoImplTest {

    private final String NEIGHBORHOOD_NAME = "Testing Create Neighborhood";
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NeighborhoodDaoImpl neighborhoodDao;
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
        Neighborhood nh = neighborhoodDao.createNeighborhood(NEIGHBORHOOD_NAME);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(nh);
        assertEquals(NEIGHBORHOOD_NAME, nh.getName());
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.neighborhoods.name()));
    }

    @Test
    public void find_neighborhoodId_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();

        // Exercise
        Optional<Neighborhood> nh = neighborhoodDao.findNeighborhood(nhKey);

        // Validations & Post Conditions
        assertTrue(nh.isPresent());
        assertEquals(nhKey, nh.get().getNeighborhoodId().longValue());
    }

    @Test
    public void find_neighborhoodId_invalid_neighborhoodId() {
        // Pre Conditions

        // Exercise
        Optional<Neighborhood> nh = neighborhoodDao.findNeighborhood(1);

        // Validations & Post Conditions
        assertFalse(nh.isPresent());
    }

    @Test
    public void find_neighborhoodName_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood(NEIGHBORHOOD_NAME);

        // Exercise
        Optional<Neighborhood> nh = neighborhoodDao.findNeighborhood(NEIGHBORHOOD_NAME);

        // Validations & Post Conditions
        assertTrue(nh.isPresent());
        assertEquals(nhKey, nh.get().getNeighborhoodId().longValue());
    }

    @Test
    public void find_neighborhoodName_invalid_neighborhoodName() {
        // Pre Conditions

        // Exercise
        Optional<Neighborhood> nh = neighborhoodDao.findNeighborhood(NEIGHBORHOOD_NAME);

        // Validations & Post Conditions
        assertFalse(nh.isPresent());
    }

    @Test
    public void weirdGet() {
        // Pre Conditions
        testInserter.createNeighborhood();

        // Exercise
        List<Neighborhood> elements = neighborhoodDao.getNeighborhoods();

        // Validations & Post Conditions
        assertEquals(1, elements.size());
    }

    @Test
    public void weirdGet_empty() {
        // Pre Conditions

        // Exercise
        List<Neighborhood> elements = neighborhoodDao.getNeighborhoods();

        // Validations & Post Conditions
        assertEquals(0, elements.size());
    }

    @Test
    public void get() {
        // Pre Conditions
        testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);

        // Exercise
        List<Neighborhood> elements = neighborhoodDao.getNeighborhoods(BASE_PAGE, BASE_PAGE_SIZE, null);

        // Validations & Post Conditions
        assertEquals(2, elements.size());
    }

    @Test
    public void get_workerId() {
        // Pre Conditions
        long nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        long uKey1 = testInserter.createUser(WORKER_MAIL_1, nhKey1);
        long uKey2 = testInserter.createUser(WORKER_MAIL_2, nhKey1);
        testInserter.createWorker(uKey1);
        testInserter.createWorker(uKey2);
        testInserter.createAffiliation(uKey1, nhKey1);
        testInserter.createAffiliation(uKey1, nhKey2);
        testInserter.createAffiliation(uKey2, nhKey1);

        // Exercise
        List<Neighborhood> elements = neighborhoodDao.getNeighborhoods(BASE_PAGE, BASE_PAGE_SIZE, uKey1);

        // Validations & Post Conditions
        assertEquals(2, elements.size());
    }

    @Test
    public void get_empty() {
        // Pre Conditions
        long nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        long uKey1 = testInserter.createUser(WORKER_MAIL_1, nhKey1);
        long uKey2 = testInserter.createUser(WORKER_MAIL_2, nhKey1);
        testInserter.createWorker(uKey1);
        testInserter.createWorker(uKey2);

        // Exercise
        List<Neighborhood> elements = neighborhoodDao.getNeighborhoods(BASE_PAGE, BASE_PAGE_SIZE, uKey1);

        // Validations & Post Conditions
        assertEquals(0, elements.size());
    }

    @Test
	public void delete_neighborhoodId_valid() {
	    // Pre Conditions
        long nhKey = testInserter.createNeighborhood();

	    // Exercise
	    boolean deleted = neighborhoodDao.deleteNeighborhood(nhKey);

	    // Validations & Post Conditions
		em.flush();
	    assertTrue(deleted);
	    assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.neighborhoods.name()));
	}

    @Test
	public void delete_neighborhoodId_invalid_neighborhoodId() {
	    // Pre Conditions

	    // Exercise
	    boolean deleted = neighborhoodDao.deleteNeighborhood(INVALID_ID);

	    // Validations & Post Conditions
		em.flush();
	    assertFalse(deleted);
	}
}
