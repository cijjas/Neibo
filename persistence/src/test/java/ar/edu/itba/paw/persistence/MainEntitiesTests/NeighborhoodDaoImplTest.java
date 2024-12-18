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

    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NeighborhoodDaoImpl neighborhoodDaoImpl;
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
        Neighborhood neighborhood = neighborhoodDaoImpl.createNeighborhood(NEIGHBORHOOD_NAME_1);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(neighborhood);
        assertEquals(NEIGHBORHOOD_NAME_1, neighborhood.getName());
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.neighborhoods.name()));
    }

    // -------------------------------------------------- FINDS --------------------------------------------------------

    @Test
    public void find_neighborhoodId_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();

        // Exercise
        Optional<Neighborhood> optionalNeighborhood = neighborhoodDaoImpl.findNeighborhood(nhKey);

        // Validations & Post Conditions
        assertTrue(optionalNeighborhood.isPresent());
        assertEquals(nhKey, optionalNeighborhood.get().getNeighborhoodId().longValue());
    }

    @Test
    public void find_neighborhoodId_invalid_neighborhoodId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();

        // Exercise
        Optional<Neighborhood> optionalNeighborhood = neighborhoodDaoImpl.findNeighborhood(INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalNeighborhood.isPresent());
    }

    @Test
    public void find_neighborhoodName_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);

        // Exercise
        Optional<Neighborhood> optionalNeighborhood = neighborhoodDaoImpl.findNeighborhood(NEIGHBORHOOD_NAME_1);

        // Validations & Post Conditions
        assertTrue(optionalNeighborhood.isPresent());
        assertEquals(nhKey, optionalNeighborhood.get().getNeighborhoodId().longValue());
    }

    @Test
    public void find_neighborhoodName_invalid_neighborhoodName() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();

        // Exercise
        Optional<Neighborhood> optionalNeighborhood = neighborhoodDaoImpl.findNeighborhood(INVALID_STRING_ID);

        // Validations & Post Conditions
        assertFalse(optionalNeighborhood.isPresent());
    }

    // -------------------------------------------------- GETS ---------------------------------------------------------

    @Test
    public void getNeighborhoodIds() {
        // Pre Conditions
        long nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);

        // Exercise
        List<Long> neighborhoodList = neighborhoodDaoImpl.getNeighborhoodIds();

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, neighborhoodList.size());
        assertTrue(neighborhoodList.contains(nhKey1));
        assertTrue(neighborhoodList.contains(nhKey2));
    }

    @Test
    public void getNeighborhoodIds_empty() {
        // Pre Conditions

        // Exercise
        List<Long> neighborhoodList = neighborhoodDaoImpl.getNeighborhoodIds();

        // Validations & Post Conditions
        assertTrue(neighborhoodList.isEmpty());
    }

    @Test
    public void get() {
        // Pre Conditions
        testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);

        // Exercise
        List<Neighborhood> neighborhoodList = neighborhoodDaoImpl.getNeighborhoods(EMPTY_FIELD, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, neighborhoodList.size());
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
        List<Neighborhood> neighborhoodList = neighborhoodDaoImpl.getNeighborhoods(uKey1, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, neighborhoodList.size());
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
        List<Neighborhood> neighborhoodList = neighborhoodDaoImpl.getNeighborhoods(uKey1, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertTrue(neighborhoodList.isEmpty());
    }

    // ---------------------------------------------- PAGINATION -------------------------------------------------------

    @Test
    public void get_pagination() {
        // Pre Conditions
        testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        testInserter.createNeighborhood(NEIGHBORHOOD_NAME_3);

        // Exercise
        List<Neighborhood> neighborhoodList = neighborhoodDaoImpl.getNeighborhoods(EMPTY_FIELD, TEST_PAGE, TEST_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, neighborhoodList.size());
    }

    // ------------------------------------------------- COUNTS ---------------------------------------------------------

    @Test
    public void count() {
        // Pre Conditions
        testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);

        // Exercise
        int countNeighborhoods = neighborhoodDaoImpl.countNeighborhoods(EMPTY_FIELD);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, countNeighborhoods);
    }

    @Test
    public void count_workerId() {
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
        int countNeighborhoods = neighborhoodDaoImpl.countNeighborhoods(uKey1);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, countNeighborhoods);
    }

    @Test
    public void count_empty() {
        // Pre Conditions
        long nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        long uKey1 = testInserter.createUser(WORKER_MAIL_1, nhKey1);
        long uKey2 = testInserter.createUser(WORKER_MAIL_2, nhKey1);
        testInserter.createWorker(uKey1);
        testInserter.createWorker(uKey2);

        // Exercise
        int countNeighborhoods = neighborhoodDaoImpl.countNeighborhoods(uKey1);

        // Validations & Post Conditions
        assertEquals(NO_ELEMENTS, countNeighborhoods);
    }

    // ------------------------------------------------ DELETES --------------------------------------------------------

    @Test
    public void delete_neighborhoodId_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();

        // Exercise
        boolean deleted = neighborhoodDaoImpl.deleteNeighborhood(nhKey);

        // Validations & Post Conditions
        em.flush();
        assertTrue(deleted);
        assertEquals(NO_ELEMENTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.neighborhoods.name()));
    }

    @Test
    public void delete_neighborhoodId_invalid_neighborhoodId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();

        // Exercise
        boolean deleted = neighborhoodDaoImpl.deleteNeighborhood(INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }
}
