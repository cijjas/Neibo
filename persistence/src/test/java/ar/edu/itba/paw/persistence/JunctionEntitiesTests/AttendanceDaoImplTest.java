package ar.edu.itba.paw.persistence.JunctionEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.persistence.JunctionDaos.AttendanceDaoImpl;
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

import javax.sql.DataSource;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class AttendanceDaoImplTest {


    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;

    private JdbcTemplate jdbcTemplate;
    private AttendanceDaoImpl attendanceDao;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        attendanceDao = new AttendanceDaoImpl(ds);
    }

    @Test
    public void testCreateAttendee() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long tKey1 = testInserter.createTime();
        long tKey2 = testInserter.createTime();
        long eKey = testInserter.createEvent(nhKey, tKey1, tKey2);

        // Exercise
        attendanceDao.createAttendee(uKey, eKey);

        // Validations & Post Conditions
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.events_users.name()));
    }

    @Test
    public void testDeleteAttendee() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long tKey1 = testInserter.createTime();
        long tKey2 = testInserter.createTime();
        long eKey = testInserter.createEvent(nhKey, tKey1, tKey2);
        testInserter.createAttendance(uKey, eKey);

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
