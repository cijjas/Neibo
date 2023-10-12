package ar.edu.itba.paw.persistence;
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
        long nhKey = testInsertionUtils.createNeighborhood();
        long uKey = testInsertionUtils.createUser(nhKey);
        long eKey = testInsertionUtils.createEvent(nhKey);

        // Exercise
        attendanceDao.createAttendee(uKey, eKey);

        // Validations & Post Conditions
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.events_users.name()));
    }

    @Test
    public void testDeleteAttendee() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();
        long uKey = testInsertionUtils.createUser(nhKey);
        long eKey = testInsertionUtils.createEvent(nhKey);
        testInsertionUtils.createAttendance(uKey, eKey);

        // Exercise
        boolean deleted = attendanceDao.deleteAttendee(uKey, eKey);

        // Validations & Post Conditions
        assertTrue(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.events_users.name()));
    }

    @Test
    public void testDeleteInvalidAttendee() {
        // Pre Conditions

        // Exercise
        boolean deleted = attendanceDao.deleteAttendee(1, 1);

        // Validations & Post Conditions
        assertFalse(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.events_users.name()));
    }
}
