package ar.edu.itba.paw.persistence.JunctionEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Entities.Neighborhood;
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
import java.util.Set;

import static ar.edu.itba.paw.persistence.TestConstants.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class AffiliationDaoImplTest {

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

    @Test
    public void create_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        testInserter.createWorker(uKey);

        // Exercise
        affiliationDaoImpl.createAffiliation(uKey, nhKey, 2L);

        // Validations & Post Conditions
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.workers_neighborhoods.name()));
    }

    @Test
    public void find_workerId_neighborhoodId_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        testInserter.createWorker(uKey);
        testInserter.createAffiliation(uKey, nhKey);


        // Exercise
        Optional<Affiliation> affiliation = affiliationDaoImpl.findAffiliation(uKey, nhKey);

        // Validations & Post Conditions
        assertTrue(affiliation.isPresent());
    }

    @Test
    public void find_workerId_neighborhoodId_invalid_workerId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        testInserter.createWorker(uKey);
        testInserter.createAffiliation(uKey, nhKey);

        // Exercise
        Optional<Affiliation> affiliation = affiliationDaoImpl.findAffiliation(INVALID_ID, nhKey);

        // Validations & Post Conditions
        assertFalse(affiliation.isPresent());
    }

    @Test
    public void find_workerId_neighborhoodId_invalid_neighborhoodId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        testInserter.createWorker(uKey);
        testInserter.createAffiliation(uKey, nhKey);

        // Exercise
        Optional<Affiliation> affiliation = affiliationDaoImpl.findAffiliation(uKey, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(affiliation.isPresent());
    }

    @Test
    public void find_workerId_neighborhoodId_invalid_workerId_neighborhoodId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        testInserter.createWorker(uKey);
        testInserter.createAffiliation(uKey, nhKey);

        // Exercise
        Optional<Affiliation> affiliation = affiliationDaoImpl.findAffiliation(1, 1);

        // Validations & Post Conditions
        assertFalse(affiliation.isPresent());
    }

    @Test
    public void get() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        testInserter.createWorker(uKey);
        testInserter.createAffiliation(uKey, nhKey);

        // Exercise
        List<Affiliation> affiliations = affiliationDaoImpl.getAffiliations(null, null, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(1, affiliations.size());
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
        List<Affiliation> affiliations = affiliationDaoImpl.getAffiliations(uKey, null, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(1, affiliations.size());
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
        List<Affiliation> affiliations = affiliationDaoImpl.getAffiliations(null, nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(1, affiliations.size());
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
        List<Affiliation> affiliations = affiliationDaoImpl.getAffiliations(uKey, nhKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(1, affiliations.size());
    }

    @Test
    public void get_empty() {
        // Pre Conditions

        // Exercise
        List<Affiliation> affiliations = affiliationDaoImpl.getAffiliations(null , null, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(0, affiliations.size());
    }

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
