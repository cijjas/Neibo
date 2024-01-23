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
import java.util.Optional;
import java.util.Set;

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
    public void testCreateAffiliation() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        testInserter.createWorker(uKey);

        // Exercise
        affiliationDaoImpl.createAffiliation(uKey, nhKey);

        // Validations & Post Conditions
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.workers_neighborhoods.name()));
    }

    @Test
    public void testFindWorkerByArea() {
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
    public void testFindWorkerByInvalidArea() {
        // Pre Conditions

        // Exercise
        Optional<Affiliation> affiliation = affiliationDaoImpl.findAffiliation(1, 1);

        // Validations & Post Conditions
        assertFalse(affiliation.isPresent());
    }


    @Test
    public void testGetNeighborhoods() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        testInserter.createWorker(uKey);
        testInserter.createAffiliation(uKey, nhKey);

        // Exercise
        Set<Neighborhood> neighborhoods = affiliationDaoImpl.getNeighborhoods(uKey);

        // Validations & Post Conditions
        assertEquals(1, neighborhoods.size());
    }

    @Test
    public void testGetNoNeighborhoods() {
        // Pre Conditions

        // Exercise
        Set<Neighborhood> neighborhoods = affiliationDaoImpl.getNeighborhoods(1);

        // Validations & Post Conditions
        assertEquals(0, neighborhoods.size());
    }


    @Test
    public void testDeleteAffiliation() {
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
    public void testDeleteInvalidAffiliation() {
        // Pre Conditions

        // Exercise
        boolean deleted = affiliationDaoImpl.deleteAffiliation(1, 1);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }
}
