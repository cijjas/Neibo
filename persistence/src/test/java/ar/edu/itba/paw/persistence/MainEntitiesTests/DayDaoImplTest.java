package ar.edu.itba.paw.persistence.MainEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Entities.Day;
import ar.edu.itba.paw.persistence.MainEntitiesDaos.DayDaoImpl;
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

import static ar.edu.itba.paw.persistence.TestConstants.INVALID_ID;
import static ar.edu.itba.paw.persistence.TestConstants.ONE_ELEMENT;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class DayDaoImplTest {

    private final String DAY_NAME = "TestDay";
    @PersistenceContext
    EntityManager em;
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DayDaoImpl dayDaoImpl;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    // ------------------------------------------------- CREATE --------------------------------------------------------

    @Test
    public void create_valid() {
        // Pre Conditions

        // Exercise
        Day day = dayDaoImpl.createDay(DAY_NAME);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(day);
        assertEquals(DAY_NAME, day.getDayName());
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.days.name()));
    }

    // -------------------------------------------------- FINDS --------------------------------------------------------

    @Test
    public void find_dayId_valid() {
        // Pre Conditions
        long dayKey = testInserter.createDay();

        // Exercise
        Optional<Day> optionalDay = dayDaoImpl.findDay(dayKey);

        // Validations & Post Conditions
        assertTrue(optionalDay.isPresent());
        assertEquals(dayKey, optionalDay.get().getDayId().longValue());
    }

    @Test
    public void find_dayId_invalid_dayId() {
        // Pre Conditions
        long dayKey = testInserter.createDay();

        // Exercise
        Optional<Day> optionalDay = dayDaoImpl.findDay(INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalDay.isPresent());
    }
}
