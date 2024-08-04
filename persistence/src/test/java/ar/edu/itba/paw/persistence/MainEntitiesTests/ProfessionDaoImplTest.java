package ar.edu.itba.paw.persistence.MainEntitiesTests;

import ar.edu.itba.paw.enums.Professions;
import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Entities.Attendance;
import ar.edu.itba.paw.models.Entities.Profession;
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
        Profession profession = professionDaoImpl.createProfession(Professions.PLUMBER.name());

        // Validations & Post Conditions
        em.flush();
        assertNotNull(profession);
        assertEquals(Professions.PLUMBER.name(), profession.getProfession());
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.professions.name()));
    }

    // -------------------------------------------------- GETS ---------------------------------------------------------

    @Test
    public void get() {
        // Pre Conditions
        populateProfessions();

        // Exercise
        List<Profession> professionList = professionDaoImpl.getProfessions(EMPTY_FIELD);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, professionList.size());
    }

    @Test
    public void get_workerId(){
        // Pre Conditions
        populateProfessions();

        // Exercise
        List<Profession> professionList = professionDaoImpl.getProfessions(uKey1);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, professionList.size());
    }

    @Test
    public void get_empty(){
        // Pre Conditions

        // Exercise
        List<Profession> professionList = professionDaoImpl.getProfessions(uKey1);

        // Validations & Post Conditions
        assertTrue(professionList.isEmpty());
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

        pKey1 = testInserter.createProfession(Professions.PLUMBER.name());
        pKey2 = testInserter.createProfession(Professions.CARPENTER.name());

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
