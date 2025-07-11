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
    private AttendanceDaoImpl attendanceDaoImpl;

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
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long tKey1 = testInserter.createTime();
        long tKey2 = testInserter.createTime();
        long eKey = testInserter.createEvent(nhKey, tKey1, tKey2);

        // Exercise
        Attendance attendance = attendanceDaoImpl.createAttendee(uKey, eKey);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(attendance);
        assertEquals(eKey, attendance.getEvent().getEventId().longValue());
        assertEquals(uKey, attendance.getUser().getUserId().longValue());
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.events_users.name()));
    }

    // -------------------------------------------------- GETS ---------------------------------------------------------

    @Test
    public void get() {
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
        List<Attendance> attendanceList = attendanceDaoImpl.getAttendance(nhKey, EMPTY_FIELD, EMPTY_FIELD, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(THREE_ELEMENTS, attendanceList.size());
    }

    @Test
    public void get_eventId() {
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
        List<Attendance> attendanceList = attendanceDaoImpl.getAttendance(nhKey, eKey2, EMPTY_FIELD, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, attendanceList.size());
    }

    @Test
    public void get_userId() {
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
        List<Attendance> attendanceList = attendanceDaoImpl.getAttendance(nhKey, EMPTY_FIELD, uKey1, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, attendanceList.size());
    }

    @Test
    public void get_eventId_userId() {
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
        List<Attendance> attendanceList = attendanceDaoImpl.getAttendance(nhKey, eKey1, uKey1, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, attendanceList.size());
    }

    @Test
    public void get_empty() {
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
        List<Attendance> attendanceList = attendanceDaoImpl.getAttendance(nhKey, eKey2, uKey2, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(NO_ELEMENTS, attendanceList.size());
    }

    // ---------------------------------------------- PAGINATION -------------------------------------------------------

    @Test
    public void get_pagination() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long uKey3 = testInserter.createUser(USER_MAIL_3, nhKey);
        long tKey1 = testInserter.createTime();
        long tKey2 = testInserter.createTime();
        long eKey1 = testInserter.createEvent(nhKey, tKey1, tKey2);
        testInserter.createAttendance(uKey1, eKey1);
        testInserter.createAttendance(uKey2, eKey1);
        testInserter.createAttendance(uKey3, eKey1);

        // Exercise
        List<Attendance> attendanceList = attendanceDaoImpl.getAttendance(nhKey, eKey1, EMPTY_FIELD, TEST_PAGE, TEST_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, attendanceList.size());
    }

    // ------------------------------------------------- COUNTS ---------------------------------------------------------

    @Test
    public void count() {
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
        int countAttendance = attendanceDaoImpl.countAttendance(nhKey, EMPTY_FIELD, EMPTY_FIELD);

        // Validations & Post Conditions
        assertEquals(THREE_ELEMENTS, countAttendance);
    }

    @Test
    public void count_eventId() {
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
        int countAttendance = attendanceDaoImpl.countAttendance(nhKey, eKey2, EMPTY_FIELD);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, countAttendance);
    }

    @Test
    public void count_userId() {
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
        int countAttendance = attendanceDaoImpl.countAttendance(nhKey, EMPTY_FIELD, uKey1);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, countAttendance);
    }

    @Test
    public void count_eventId_userId() {
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
        int countAttendance = attendanceDaoImpl.countAttendance(nhKey, eKey1, uKey1);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, countAttendance);
    }

    @Test
    public void count_empty() {
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
        int countAttendance = attendanceDaoImpl.countAttendance(nhKey, eKey2, uKey2);

        // Validations & Post Conditions
        assertEquals(NO_ELEMENTS, countAttendance);
    }

    // ------------------------------------------------ DELETES --------------------------------------------------------

    @Test
    public void delete_neighborhoodId_eventId_userId_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long tKey1 = testInserter.createTime();
        long tKey2 = testInserter.createTime();
        long eKey = testInserter.createEvent(nhKey, tKey1, tKey2);
        testInserter.createAttendance(uKey, eKey);

        // Exercise
        boolean deleted = attendanceDaoImpl.deleteAttendee(eKey, uKey);

        // Validations & Post Conditions
        em.flush();
        assertTrue(deleted);
        assertEquals(NO_ELEMENTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.events_users.name()));
    }

    @Test
    public void delete_eventId_userId_invalid_userId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long tKey1 = testInserter.createTime();
        long tKey2 = testInserter.createTime();
        long eKey = testInserter.createEvent(nhKey, tKey1, tKey2);
        testInserter.createAttendance(uKey, eKey);

        // Exercise
        boolean deleted = attendanceDaoImpl.deleteAttendee(eKey, INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }

    @Test
    public void delete_eventId_userId_invalid_eventId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long tKey1 = testInserter.createTime();
        long tKey2 = testInserter.createTime();
        long eKey = testInserter.createEvent(nhKey, tKey1, tKey2);
        testInserter.createAttendance(uKey, eKey);

        // Exercise
        boolean deleted = attendanceDaoImpl.deleteAttendee(INVALID_ID, uKey);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }

    @Test
    public void delete_eventId_userId_invalid_eventId_userId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long tKey1 = testInserter.createTime();
        long tKey2 = testInserter.createTime();
        long eKey = testInserter.createEvent(nhKey, tKey1, tKey2);
        testInserter.createAttendance(uKey, eKey);

        // Exercise
        boolean deleted = attendanceDaoImpl.deleteAttendee(INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }
}
