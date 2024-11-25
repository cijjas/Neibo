package ar.edu.itba.paw.persistence.MainEntitiesTests;

import ar.edu.itba.paw.enums.Table;
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
public class ProfessionDaoImplTest {

    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ar.edu.itba.paw.persistence.MainEntitiesDaos.ProfessionDaoImpl professionDaoImpl;

    @PersistenceContext
    private EntityManager em;
    private long nhKey1;
    private long nhKey2;
    private long nhKey3;
    private long nhKey4;
    private long uKey1;
    private long uKey2;
    private long uKey3;
    private long uKey4;
    private long pKey1;
    private long pKey2;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    // ------------------------------------------------- CREATE --------------------------------------------------------

    @Test
    public void create_valid() {
        // Pre Conditions

        // Exercise
        ar.edu.itba.paw.models.Entities.Profession profession = professionDaoImpl.createProfession(PROFESSION_NAME_1);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(profession);
        assertEquals(PROFESSION_NAME_1, profession.getProfession());
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.professions.name()));
    }

    // -------------------------------------------------- FINDS --------------------------------------------------------

    @Test
    public void find_profession_valid() {
        // Pre Conditions
        pKey1 = testInserter.createProfession();

        // Exercise
        Optional<ar.edu.itba.paw.models.Entities.Profession> optionalProfession = professionDaoImpl.findProfession(pKey1);

        // Validations & Post Conditions
        assertTrue(optionalProfession.isPresent());
        assertEquals(pKey1, optionalProfession.get().getProfessionId().longValue());
    }

    @Test
    public void find_profession_invalid_professionId() {
        // Pre Conditions

        // Exercise
        Optional<ar.edu.itba.paw.models.Entities.Profession> optionalProfession = professionDaoImpl.findProfession(INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalProfession.isPresent());
    }

    // -------------------------------------------------- GETS ---------------------------------------------------------

    @Test
    public void get() {
        // Pre Conditions
        populateProfessions();

        // Exercise
        List<ar.edu.itba.paw.models.Entities.Profession> professionList = professionDaoImpl.getProfessions(EMPTY_FIELD);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, professionList.size());
    }

    @Test
    public void get_workerId() {
        // Pre Conditions
        populateProfessions();

        // Exercise
        List<ar.edu.itba.paw.models.Entities.Profession> professionList = professionDaoImpl.getProfessions(uKey1);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, professionList.size());
    }

    @Test
    public void get_empty() {
        // Pre Conditions

        // Exercise
        List<ar.edu.itba.paw.models.Entities.Profession> professionList = professionDaoImpl.getProfessions(uKey1);

        // Validations & Post Conditions
        assertTrue(professionList.isEmpty());
    }

    // ------------------------------------------------ DELETES --------------------------------------------------------

    @Test
    public void delete_professionId_valid() {
        // Pre Conditions
        long pKey = testInserter.createProfession();

        // Exercise
        boolean deleted = professionDaoImpl.deleteProfession(pKey);

        // Validations & Post Conditions
        em.flush();
        assertTrue(deleted);
        assertEquals(NO_ELEMENTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.professions.name()));
    }

    @Test
    public void delete_professionId_invalid_professionId() {
        // Pre Conditions

        // Exercise
        boolean deleted = professionDaoImpl.deleteProfession(INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }


    private void populateProfessions() {
        nhKey1 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1); // Workers Neighborhood
        nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        nhKey3 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_3);
        nhKey4 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_4);

        uKey1 = testInserter.createUser(WORKER_MAIL_1, nhKey1);
        uKey2 = testInserter.createUser(WORKER_MAIL_2, nhKey1);
        uKey3 = testInserter.createUser(WORKER_MAIL_3, nhKey1);
        uKey4 = testInserter.createUser(WORKER_MAIL_4, nhKey1);

        pKey1 = testInserter.createProfession(PROFESSION_NAME_1);
        pKey2 = testInserter.createProfession(PROFESSION_NAME_2);

        testInserter.createWorker(uKey1);
        testInserter.createWorker(uKey2);
        testInserter.createWorker(uKey3);
        testInserter.createWorker(uKey4);

        testInserter.createSpecialization(uKey1, pKey1);
        testInserter.createSpecialization(uKey1, pKey2);
        testInserter.createSpecialization(uKey2, pKey1);
        testInserter.createSpecialization(uKey3, pKey2);
        testInserter.createSpecialization(uKey4, pKey2);
    }
}
