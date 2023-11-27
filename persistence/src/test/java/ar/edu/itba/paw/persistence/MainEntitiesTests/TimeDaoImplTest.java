package ar.edu.itba.paw.persistence.MainEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.interfaces.persistence.TimeDao;
import ar.edu.itba.paw.models.Entities.Time;
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
import java.util.OptionalLong;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class TimeDaoImplTest {

    public static final java.sql.Time TIME = new java.sql.Time(System.currentTimeMillis());
    private final java.sql.Time TIME_INTERVAL = new java.sql.Time(System.currentTimeMillis());
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TimeDao timeDao;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreateTime() {
        // Pre Conditions

        // Exercise
        Time timeKey = timeDao.createTime(TIME_INTERVAL);

        // Validations & Post Conditions
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.times.name()));
    }

    @Test
    public void testFindTimeById() {
        // Pre Conditions
        long timeKey = testInserter.createTime();

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

    @Test
    public void testFindIdByTime() {
        // Pre Conditions
        long timeKey = testInserter.createTime(TIME);

        // Exercise
        OptionalLong foundTime = timeDao.findIdByTime(TIME);

        // Validations & Post Conditions
        assertTrue(foundTime.isPresent());
    }

    @Test
    public void testFindIdByInvalidTime() {
        // Pre Conditions

        // Exercise
        OptionalLong foundTime = timeDao.findIdByTime(TIME);

        // Validations & Post Conditions
        assertFalse(foundTime.isPresent());
    }
}
