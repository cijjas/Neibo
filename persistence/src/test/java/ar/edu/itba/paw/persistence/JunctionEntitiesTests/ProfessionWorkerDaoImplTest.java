package ar.edu.itba.paw.persistence.JunctionEntitiesTests;

import ar.edu.itba.paw.enums.Professions;
import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Entities.Specialization;
import ar.edu.itba.paw.models.Entities.Profession;
import ar.edu.itba.paw.persistence.JunctionDaos.ProfessionWorkerDaoImpl;
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

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class ProfessionWorkerDaoImplTest {

    private final String PROFESSION_NAME = "Plumber";
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ProfessionWorkerDaoImpl professionWorkerDao;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreateSpecialization() {
        // Pre Conditions
        long pKey = testInserter.createProfession();
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        testInserter.createWorker(uKey);


        // Exercise
        Specialization specialization = professionWorkerDao.createSpecialization(uKey, pKey);

        // Validations & Post Conditions
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.workers_professions.name()));
        assertEquals(Professions.PLUMBER.name(), specialization.getProfession().getProfession().name());
    }

    @Test
    public void testGetWorkerProfession() {
        // Pre Conditions
//        long pKey = testInserter.createProfession(PROFESSION_NAME);
        long pKey = testInserter.createProfession();
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        testInserter.createWorker(uKey);
        testInserter.createSpecialization(uKey, pKey);

        // Exercise
        List<Profession> profession = professionWorkerDao.getWorkerProfessions(uKey);

        // Validations & Post Conditions
        assertFalse(profession.isEmpty());
    }

    @Test
    public void testNoGetWorkerProfession() {
        // Pre Conditions
//        long pKey = testInserter.createProfession(PROFESSION_NAME);

        // Exercise
        List<Profession> profession = professionWorkerDao.getWorkerProfessions(1);

        // Validations & Post Conditions
        assertTrue(profession.isEmpty());
    }
}
