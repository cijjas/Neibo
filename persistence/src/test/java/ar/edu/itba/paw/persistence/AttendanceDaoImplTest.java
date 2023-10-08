package ar.edu.itba.paw.persistence;
import ar.edu.itba.paw.persistence.config.TestConfig;
import enums.Language;
import enums.UserRole;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:hsqlValueCleanUp.sql")
public class AttendanceDaoImplTest {

    private JdbcTemplate jdbcTemplate;
    private TestInsertionUtils testInsertionUtils;
    private AttendanceDaoImpl attendanceDao;

    @Autowired
    private DataSource ds;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        attendanceDao = new AttendanceDaoImpl(ds);
        testInsertionUtils = new TestInsertionUtils(jdbcTemplate, ds);
    }

    @Test
    public void testCreateAttendee() {
        // Pre Conditions
        Number nhKey = testInsertionUtils.createNeighborhood();
        Number uKey = testInsertionUtils.createUser(nhKey.longValue());
        Number eKey = testInsertionUtils.createEvent(nhKey.longValue());

        // Exercise
        attendanceDao.createAttendee(uKey.longValue(), eKey.longValue());

        // Validations & Post Conditions
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "events_users"));
    }

    @Test
    public void testDeleteAttendee() {
        // Pre Conditions
        Number nhKey = testInsertionUtils.createNeighborhood();
        Number uKey = testInsertionUtils.createUser(nhKey.longValue());
        Number eKey = testInsertionUtils.createEvent(nhKey.longValue());
        testInsertionUtils.createAttendance(uKey.longValue(), eKey.longValue());

        // Exercise
        boolean deleted = attendanceDao.deleteAttendee(uKey.longValue(), eKey.longValue());

        // Validations & Post Conditions
        assertTrue(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, "events_users"));
    }

    @Test
    public void testDeleteInvalidAttendee() {
        // Pre Conditions

        // Exercise
        boolean deleted = attendanceDao.deleteAttendee(1, 1);

        // Validations & Post Conditions
        assertFalse(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, "events_users"));
    }
}
