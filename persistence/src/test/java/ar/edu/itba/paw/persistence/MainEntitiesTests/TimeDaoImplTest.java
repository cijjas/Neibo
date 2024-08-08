package ar.edu.itba.paw.persistence.MainEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Entities.Time;
import ar.edu.itba.paw.persistence.MainEntitiesDaos.TimeDaoImpl;
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

import static ar.edu.itba.paw.persistence.TestConstants.INVALID_ID;
import static ar.edu.itba.paw.persistence.TestConstants.ONE_ELEMENT;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class TimeDaoImplTest {

    public static final java.sql.Time TIME_MILLISECONDS = new java.sql.Time(System.currentTimeMillis());
    public static final java.sql.Time INVALID_TIME_MILLISECONDS = new java.sql.Time(1L);
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TimeDaoImpl timeDaoImpl;

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

        // Exercise
        Time time = timeDaoImpl.createTime(TIME_MILLISECONDS);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(time);
        assertEquals(TIME_MILLISECONDS, time.getTimeInterval());
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.times.name()));
    }

    // -------------------------------------------------- FINDS --------------------------------------------------------

    @Test
    public void find_timeId_valid() {
        // Pre Conditions
        long timeKey = testInserter.createTime();

        // Exercise
        Optional<Time> optionalTime = timeDaoImpl.findTime(timeKey);

        // Validations & Post Conditions
        assertTrue(optionalTime.isPresent());
        assertEquals(timeKey, optionalTime.get().getTimeId().longValue());
    }

    @Test
    public void find_timeId_invalid_timeId() {
        // Pre Conditions
        long timeKey = testInserter.createTime();

        // Exercise
        Optional<Time> optionalTime = timeDaoImpl.findTime(INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalTime.isPresent());
    }

    @Test
    public void findId_timeName_valid() {
        // Pre Conditions
        long timeKey = testInserter.createTime(TIME_MILLISECONDS);

        // Exercise
        OptionalLong optionalLong = timeDaoImpl.findId(TIME_MILLISECONDS);

        // Validations & Post Conditions
        assertTrue(optionalLong.isPresent());
        assertEquals(timeKey, optionalLong.getAsLong());
    }

    @Test
    public void findId_timeName_invalid_timeName() {
        // Pre Conditions
        long timeKey = testInserter.createTime(TIME_MILLISECONDS);

        // Exercise
        OptionalLong optionalLong = timeDaoImpl.findId(INVALID_TIME_MILLISECONDS);

        // Validations & Post Conditions
        assertFalse(optionalLong.isPresent());
    }
}
