package ar.edu.itba.paw.persistence.JunctionEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Entities.Affiliation;
import ar.edu.itba.paw.persistence.JunctionDaos.AffiliationDaoImpl;
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
public class AffiliationDaoImplTest {

    public static final long AFFILIATION_WORKER_ROLE_ID = 2L;

    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AffiliationDaoImpl affiliationDaoImpl;

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
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        testInserter.createWorker(uKey);

        // Exercise
        Affiliation affiliation = affiliationDaoImpl.createAffiliation(uKey, nhKey, AFFILIATION_WORKER_ROLE_ID);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(affiliation);
        assertEquals(AFFILIATION_WORKER_ROLE_ID, affiliation.getRole().getId());
        assertEquals(nhKey, affiliation.getNeighborhood().getNeighborhoodId().longValue());
        assertEquals(uKey, affiliation.getWorker().getUser().getUserId().longValue());
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.workers_neighborhoods.name()));
    }

    // -------------------------------------------------- FINDS --------------------------------------------------------

    @Test
    public void find_workerId_neighborhoodId_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        testInserter.createWorker(uKey);
        testInserter.createAffiliation(uKey, nhKey);

        // Exercise
        Optional<Affiliation> optionalAffiliation = affiliationDaoImpl.findAffiliation(uKey, nhKey);

        // Validations & Post Conditions
        assertTrue(optionalAffiliation.isPresent());
        assertEquals(nhKey, optionalAffiliation.get().getNeighborhood().getNeighborhoodId().longValue());
        assertEquals(uKey, optionalAffiliation.get().getWorker().getWorkerId().longValue());
    }

    @Test
    public void find_workerId_neighborhoodId_invalid_workerId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        testInserter.createWorker(uKey);
        testInserter.createAffiliation(uKey, nhKey);

        // Exercise
        Optional<Affiliation> optionalAffiliation = affiliationDaoImpl.findAffiliation(INVALID_ID, nhKey);

        // Validations & Post Conditions
        assertFalse(optionalAffiliation.isPresent());
    }

    @Test
    public void find_workerId_neighborhoodId_invalid_neighborhoodId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        testInserter.createWorker(uKey);
        testInserter.createAffiliation(uKey, nhKey);

        // Exercise
        Optional<Affiliation> optionalAffiliation = affiliationDaoImpl.findAffiliation(uKey, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalAffiliation.isPresent());
    }

    @Test
    public void find_workerId_neighborhoodId_invalid_workerId_neighborhoodId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        testInserter.createWorker(uKey);
        testInserter.createAffiliation(uKey, nhKey);

        // Exercise
        Optional<Affiliation> optionalAffiliation = affiliationDaoImpl.findAffiliation(INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalAffiliation.isPresent());
    }

    // -------------------------------------------------- GETS ---------------------------------------------------------

    @Test
    public void get() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        testInserter.createWorker(uKey);
        testInserter.createAffiliation(uKey, nhKey);

        // Exercise
        List<Affiliation> affiliationList = affiliationDaoImpl.getAffiliations(EMPTY_FIELD, EMPTY_FIELD, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, affiliationList.size());
    }

    @Test
    public void get_workerId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(USER_MAIL_1, nhKey);
        testInserter.createWorker(uKey);
        testInserter.createAffiliation(uKey, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        testInserter.createWorker(uKey2);
        testInserter.createAffiliation(uKey2, nhKey);

        // Exercise
        List<Affiliation> affiliationList = affiliationDaoImpl.getAffiliations(uKey, EMPTY_FIELD, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, affiliationList.size());
    }

    @Test
    public void get_neighborhoodId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        long uKey = testInserter.createUser(USER_MAIL_1, nhKey);
        testInserter.createWorker(uKey);
        testInserter.createAffiliation(uKey, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        testInserter.createWorker(uKey2);
        testInserter.createAffiliation(uKey2, nhKey2);

        // Exercise
        List<Affiliation> affiliationList = affiliationDaoImpl.getAffiliations(EMPTY_FIELD, nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, affiliationList.size());
    }

    @Test
    public void get_workerId_neighborhoodId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        long uKey = testInserter.createUser(USER_MAIL_1, nhKey);
        testInserter.createWorker(uKey);
        testInserter.createAffiliation(uKey, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        testInserter.createWorker(uKey2);
        testInserter.createAffiliation(uKey2, nhKey2);
        testInserter.createAffiliation(uKey2, nhKey);

        // Exercise
        List<Affiliation> affiliationList = affiliationDaoImpl.getAffiliations(uKey, nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, affiliationList.size());
    }

    @Test
    public void get_empty() {
        // Pre Conditions

        // Exercise
        List<Affiliation> affiliationList = affiliationDaoImpl.getAffiliations(EMPTY_FIELD, EMPTY_FIELD, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertTrue(affiliationList.isEmpty());
    }

    // ------------------------------------------------- COUNTS ---------------------------------------------------------

    @Test
    public void count() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        testInserter.createWorker(uKey);
        testInserter.createAffiliation(uKey, nhKey);

        // Exercise
        int countAffiliations = affiliationDaoImpl.countAffiliations(EMPTY_FIELD, EMPTY_FIELD);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, countAffiliations);
    }

    @Test
    public void count_workerId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(USER_MAIL_1, nhKey);
        testInserter.createWorker(uKey);
        testInserter.createAffiliation(uKey, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        testInserter.createWorker(uKey2);
        testInserter.createAffiliation(uKey2, nhKey);

        // Exercise
        int countAffiliations = affiliationDaoImpl.countAffiliations(uKey, EMPTY_FIELD);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, countAffiliations);
    }

    @Test
    public void count_neighborhoodId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        long uKey = testInserter.createUser(USER_MAIL_1, nhKey);
        testInserter.createWorker(uKey);
        testInserter.createAffiliation(uKey, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        testInserter.createWorker(uKey2);
        testInserter.createAffiliation(uKey2, nhKey2);

        // Exercise
        int countAffiliations = affiliationDaoImpl.countAffiliations(EMPTY_FIELD, nhKey);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, countAffiliations);
    }

    @Test
    public void count_workerId_neighborhoodId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        long nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        long uKey = testInserter.createUser(USER_MAIL_1, nhKey);
        testInserter.createWorker(uKey);
        testInserter.createAffiliation(uKey, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        testInserter.createWorker(uKey2);
        testInserter.createAffiliation(uKey2, nhKey2);
        testInserter.createAffiliation(uKey2, nhKey);

        // Exercise
        int countAffiliations= affiliationDaoImpl.countAffiliations(uKey, nhKey);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, countAffiliations);
    }

    @Test
    public void count_empty() {
        // Pre Conditions

        // Exercise
        int countAffiliations = affiliationDaoImpl.countAffiliations(EMPTY_FIELD, EMPTY_FIELD);

        // Validations & Post Conditions
        assertEquals(NO_ELEMENTS, countAffiliations);
    }

    // ------------------------------------------------ DELETES --------------------------------------------------------

    @Test
    public void delete_userId_neighborhoodId_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        testInserter.createWorker(uKey);
        testInserter.createAffiliation(uKey, nhKey);

        // Exercise
        boolean deleted = affiliationDaoImpl.deleteAffiliation(uKey, nhKey);

        // Validations & Post Conditions
        em.flush();
        assertTrue(deleted);
        assertEquals(NO_ELEMENTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.workers_neighborhoods.name()));
    }

    @Test
    public void delete_userId_neighborhoodId_invalid_userId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        testInserter.createWorker(uKey);
        testInserter.createAffiliation(uKey, nhKey);

        // Exercise
        boolean deleted = affiliationDaoImpl.deleteAffiliation(INVALID_ID, nhKey);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }

    @Test
    public void delete_userId_neighborhoodId_invalid_neighborhoodId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        testInserter.createWorker(uKey);
        testInserter.createAffiliation(uKey, nhKey);

        // Exercise
        boolean deleted = affiliationDaoImpl.deleteAffiliation(nhKey, INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }

    @Test
    public void delete_userId_neighborhoodId_invalid_userId_neighborhoodId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        testInserter.createWorker(uKey);
        testInserter.createAffiliation(uKey, nhKey);

        // Exercise
        boolean deleted = affiliationDaoImpl.deleteAffiliation(INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }
}
