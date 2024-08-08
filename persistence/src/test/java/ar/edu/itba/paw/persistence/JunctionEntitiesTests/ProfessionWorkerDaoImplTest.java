package ar.edu.itba.paw.persistence.JunctionEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Entities.Profession;
import ar.edu.itba.paw.models.Entities.Specialization;
import ar.edu.itba.paw.persistence.JunctionDaos.ProfessionWorkerDaoImpl;
import ar.edu.itba.paw.persistence.MainEntitiesDaos.ProfessionDaoImpl;
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

import static ar.edu.itba.paw.persistence.TestConstants.EMPTY_FIELD;
import static ar.edu.itba.paw.persistence.TestConstants.ONE_ELEMENT;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class ProfessionWorkerDaoImplTest {

    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ProfessionWorkerDaoImpl professionWorkerDaoImpl;
    @Autowired
    private ProfessionDaoImpl professionDaoImpl;

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
        long pKey = testInserter.createProfession();
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long wKey = testInserter.createWorker(uKey);

        // Exercise
        Specialization specialization = professionWorkerDaoImpl.createSpecialization(uKey, pKey);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(specialization);
        assertEquals(uKey, specialization.getWorker().getUser().getUserId().longValue());
        assertEquals(pKey, specialization.getProfession().getProfessionId().longValue());
        assertEquals(pKey, specialization.getProfession().getProfessionId().longValue());
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.workers_professions.name()));
    }

    // -------------------------------------------------- GETS ---------------------------------------------------------

    @Test
    public void get_workerId() {
        // Pre Conditions
        long pKey = testInserter.createProfession();
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);

        // Exercise
        List<Profession> professionList = professionDaoImpl.getProfessions(uKey);

        // Validations & Post Conditions
        assertFalse(professionList.isEmpty());
    }

    @Test
    public void get_empty() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        testInserter.createWorker(uKey);

        // Exercise
        List<Profession> professionList = professionDaoImpl.getProfessions(EMPTY_FIELD);

        // Validations & Post Conditions
        assertTrue(professionList.isEmpty());
    }
}
