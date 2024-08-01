package ar.edu.itba.paw.persistence.JunctionEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Entities.Attendance;
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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import java.util.List;
import java.util.Optional;

import static ar.edu.itba.paw.persistence.TestConstants.*;
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
    @Autowired
    private AttendanceDaoImpl attendanceDao;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void create_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long tKey1 = testInserter.createTime();
        long tKey2 = testInserter.createTime();
        long eKey = testInserter.createEvent(nhKey, tKey1, tKey2);

        // Exercise
        attendanceDao.createAttendee(uKey, eKey);

        // Validations & Post Conditions
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.events_users.name()));
    }

        @Test
    public void find_userId_eventId_neighborhoodId_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long tKey1 = testInserter.createTime();
        long tKey2 = testInserter.createTime();
        long eKey1 = testInserter.createEvent(nhKey, tKey1, tKey2);
        testInserter.createAttendance(uKey1, eKey1);

        // Exercise
        Optional<Attendance> attendance = attendanceDao.findAttendance(uKey1, eKey1, nhKey);

        // Validations & Post Conditions
        assertTrue(attendance.isPresent());
    }

    @Test
    public void find_userId_eventId_neighborhoodId_invalid_userId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long tKey1 = testInserter.createTime();
        long tKey2 = testInserter.createTime();
        long eKey1 = testInserter.createEvent(nhKey, tKey1, tKey2);
        testInserter.createAttendance(uKey1, eKey1);

        // Exercise
        Optional<Attendance> attendance = attendanceDao.findAttendance(INVALID_ID, eKey1, nhKey);

        // Validations & Post Conditions
        assertFalse(attendance.isPresent());
    }

        @Test
    public void find_userId_eventId_neighborhoodId_invalid_eventId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long tKey1 = testInserter.createTime();
        long tKey2 = testInserter.createTime();
        long eKey1 = testInserter.createEvent(nhKey, tKey1, tKey2);
        testInserter.createAttendance(uKey1, eKey1);

        // Exercise
        Optional<Attendance> attendance = attendanceDao.findAttendance(uKey1, INVALID_ID, nhKey);

        // Validations & Post Conditions
        assertFalse(attendance.isPresent());
    }

        @Test
    public void find_userId_eventId_neighborhoodId_invalid_neighborhoodId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long tKey1 = testInserter.createTime();
        long tKey2 = testInserter.createTime();
        long eKey1 = testInserter.createEvent(nhKey, tKey1, tKey2);
        testInserter.createAttendance(uKey1, eKey1);

        // Exercise
        Optional<Attendance> attendance = attendanceDao.findAttendance(uKey1, eKey1, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(attendance.isPresent());
    }

        @Test
    public void find_userId_eventId_neighborhoodId_invalid_userId_eventId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long tKey1 = testInserter.createTime();
        long tKey2 = testInserter.createTime();
        long eKey1 = testInserter.createEvent(nhKey, tKey1, tKey2);
        testInserter.createAttendance(uKey1, eKey1);

        // Exercise
        Optional<Attendance> attendance = attendanceDao.findAttendance(INVALID_ID, INVALID_ID, nhKey);

        // Validations & Post Conditions
        assertFalse(attendance.isPresent());
    }

        @Test
    public void find_userId_eventId_neighborhoodId_invalid_userId_neighborhoodId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long tKey1 = testInserter.createTime();
        long tKey2 = testInserter.createTime();
        long eKey1 = testInserter.createEvent(nhKey, tKey1, tKey2);
        testInserter.createAttendance(uKey1, eKey1);

        // Exercise
        Optional<Attendance> attendance = attendanceDao.findAttendance(INVALID_ID, eKey1, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(attendance.isPresent());
    }

        @Test
    public void find_userId_eventId_neighborhoodId_invalid_eventId_neighborhoodId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long tKey1 = testInserter.createTime();
        long tKey2 = testInserter.createTime();
        long eKey1 = testInserter.createEvent(nhKey, tKey1, tKey2);
        testInserter.createAttendance(uKey1, eKey1);

        // Exercise
        Optional<Attendance> attendance = attendanceDao.findAttendance(uKey1, INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(attendance.isPresent());
    }

        @Test
    public void find_userId_eventId_neighborhoodId_invalid_userId_eventId_neighborhoodId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long tKey1 = testInserter.createTime();
        long tKey2 = testInserter.createTime();
        long eKey1 = testInserter.createEvent(nhKey, tKey1, tKey2);
        testInserter.createAttendance(uKey1, eKey1);

        // Exercise
        Optional<Attendance> attendance = attendanceDao.findAttendance(INVALID_ID, INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(attendance.isPresent());
    }

    @Test
    public void get_eventId(){
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long tKey1 = testInserter.createTime();
        long tKey2 = testInserter.createTime();
        long eKey1 = testInserter.createEvent(nhKey, tKey1, tKey2);
        long eKey2 = testInserter.createEvent(nhKey, tKey1, tKey2);
        testInserter.createAttendance(uKey1, eKey1);
        testInserter.createAttendance(uKey1, eKey2);
        testInserter.createAttendance(uKey2, eKey1);

        // Exercise
        List<Attendance> attendances = attendanceDao.getAttendance(eKey1, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(2, attendances.size());
    }

    @Test
    public void get_empty(){
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long tKey1 = testInserter.createTime();
        long tKey2 = testInserter.createTime();
        long eKey1 = testInserter.createEvent(nhKey, tKey1, tKey2);

        // Exercise
        List<Attendance> attendances = attendanceDao.getAttendance(eKey1, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(0, attendances.size());
    }

    @Test
    public void delete_userId_eventId_valid() {
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
        em.flush();
        assertTrue(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.events_users.name()));
    }

    @Test
    public void delete_userId_eventId_invalid_userId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long tKey1 = testInserter.createTime();
        long tKey2 = testInserter.createTime();
        long eKey = testInserter.createEvent(nhKey, tKey1, tKey2);
        testInserter.createAttendance(uKey, eKey);

        // Exercise
        boolean deleted = attendanceDao.deleteAttendee(INVALID_ID, eKey);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.events_users.name()));
    }

    @Test
    public void delete_userId_eventId_invalid_eventId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long tKey1 = testInserter.createTime();
        long tKey2 = testInserter.createTime();
        long eKey = testInserter.createEvent(nhKey, tKey1, tKey2);
        testInserter.createAttendance(uKey, eKey);

        // Exercise
        boolean deleted = attendanceDao.deleteAttendee(uKey, INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.events_users.name()));
    }

    @Test
    public void delete_userId_eventId_invalid_userId_eventId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long tKey1 = testInserter.createTime();
        long tKey2 = testInserter.createTime();
        long eKey = testInserter.createEvent(nhKey, tKey1, tKey2);
        testInserter.createAttendance(uKey, eKey);

        // Exercise
        boolean deleted = attendanceDao.deleteAttendee(INVALID_ID, INVALID_ID );

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.events_users.name()));
    }
}
