package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.interfaces.persistence.DayDao;
import ar.edu.itba.paw.models.Day;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInsertionUtils.class})
@Sql("classpath:hsqlValueCleanUp.sql")
public class DayDaoImplTest {

    @Autowired
    private DataSource ds;
    @Autowired
    private TestInsertionUtils testInsertionUtils;
    private JdbcTemplate jdbcTemplate;
    private DayDao dayDao;

    private final String DAY_NAME = "TestDay";


    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        dayDao = new DayDaoImpl(ds);
    }

    @Test
    public void testCreateDay() {
        // Pre Conditions

        // Exercise
        Day createdDay = dayDao.createDay(DAY_NAME);

        // Validations & Post Conditions
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.days.name()));
        assertEquals(DAY_NAME, createdDay.getDayName());
    }

    @Test
    public void testFindDayById() {
        // Pre Conditions
        long dayKey = testInsertionUtils.createDay();

        // Exercise
        Optional<Day> foundDay = dayDao.findDayById(dayKey);

        // Validations & Post Conditions
        assertTrue(foundDay.isPresent());
    }

    @Test
    public void testFindDayByInvalidId() {
        // Pre Conditions

        // Exercise
        Optional<Day> foundDay = dayDao.findDayById(1);

        // Validations & Post Conditions
        assertFalse(foundDay.isPresent());
    }
}
