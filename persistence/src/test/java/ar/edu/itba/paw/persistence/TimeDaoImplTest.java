package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.TimeDao;
import ar.edu.itba.paw.models.Time;
import ar.edu.itba.paw.persistence.config.TestConfig;
import enums.Table;
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
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:hsqlValueCleanUp.sql")
public class TimeDaoImplTest {

    private JdbcTemplate jdbcTemplate;
    private TestInsertionUtils testInsertionUtils;
    private TimeDao timeDao;

    private java.sql.Time TIME_INTERVAL = new java.sql.Time(System.currentTimeMillis());

    @Autowired
    private DataSource ds;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        testInsertionUtils = new TestInsertionUtils(jdbcTemplate, ds);
        timeDao = new TimeDaoImpl(ds);
    }

    @Test
    public void testCreateTime() {
        // Pre Conditions

        // Exercise
        long timeKey = testInsertionUtils.createTime();

        // Validations & Post Conditions
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.times.name()));
    }

    @Test
    public void testFindTimeById() {
        // Pre Conditions
        long timeKey = testInsertionUtils.createTime();

        // Exercise
        Optional<Time> foundTime = timeDao.findTimeById(timeKey);

        // Validations & Post Conditions
        assertTrue(foundTime.isPresent());
    }

    @Test
    public void testFindTimeByInvalidId() {
        // Pre Conditions

        // Exercise
        Optional<Time> foundTime = timeDao.findTimeById(1);

        // Validations & Post Conditions
        assertFalse(foundTime.isPresent());
    }
}
