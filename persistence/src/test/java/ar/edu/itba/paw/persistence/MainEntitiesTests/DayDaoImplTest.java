package ar.edu.itba.paw.persistence.MainEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.interfaces.persistence.DayDao;
import ar.edu.itba.paw.models.Entities.Day;
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

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class DayDaoImplTest {

    private final String DAY_NAME = "TestDay";
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DayDao dayDao;

    @PersistenceContext
    EntityManager em;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void create_valid() {
        // Pre Conditions

        // Exercise
        Day createdDay = dayDao.createDay(DAY_NAME);

        // Validations & Post Conditions
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.days.name()));
        assertEquals(DAY_NAME, createdDay.getDayName());
    }

    @Test
    public void find_dayId_valid() {
        // Pre Conditions
        long dayKey = testInserter.createDay();

        // Exercise
        Optional<Day> foundDay = dayDao.findDay(dayKey);

        // Validations & Post Conditions
        assertTrue(foundDay.isPresent());
    }

    @Test
    public void find_dayId_invalid_dayId() {
        // Pre Conditions

        // Exercise
        Optional<Day> foundDay = dayDao.findDay(1);

        // Validations & Post Conditions
        assertFalse(foundDay.isPresent());
    }
}
