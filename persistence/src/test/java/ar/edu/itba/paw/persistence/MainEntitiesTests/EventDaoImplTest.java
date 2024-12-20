package ar.edu.itba.paw.persistence.MainEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Entities.Event;
import ar.edu.itba.paw.persistence.MainEntitiesDaos.EventDaoImpl;
import ar.edu.itba.paw.persistence.TestConstants;
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
import java.sql.Time;
import java.util.List;
import java.util.Optional;

import static ar.edu.itba.paw.persistence.TestConstants.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class EventDaoImplTest {

    private static final String EVENT_NAME = "Sample Event";
    private static final String EVENT_DESCRIPTION = "Sample Description";
    private static final Time EVENT_START_TIME = Time.valueOf("22:00:00");
    private static final Time EVENT_END_TIME = Time.valueOf("23:00:00");
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EventDaoImpl eventDaoImpl;
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
        long tKey1 = testInserter.createTime(EVENT_START_TIME);
        long tKey2 = testInserter.createTime(EVENT_END_TIME);
        long nhKey = testInserter.createNeighborhood();

        // Exercise
        Event event = eventDaoImpl.createEvent(nhKey, EVENT_NAME, EVENT_DESCRIPTION, TestConstants.DATE_1, tKey1, tKey2);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(event);
        assertEquals(tKey1, event.getStartTime().getTimeId().longValue());
        assertEquals(tKey2, event.getEndTime().getTimeId().longValue());
        assertEquals(nhKey, event.getNeighborhood().getNeighborhoodId().longValue());
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.events.name()));
        assertEquals(EVENT_NAME, event.getName());
        assertEquals(EVENT_DESCRIPTION, event.getDescription());
        assertEquals(TestConstants.DATE_1, event.getDate());
    }

    // -------------------------------------------------- FINDS --------------------------------------------------------

    @Test
    public void find_neighborhoodId_eventId_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long tKey1 = testInserter.createTime(EVENT_START_TIME);
        long tKey2 = testInserter.createTime(EVENT_END_TIME);
        long eKey = testInserter.createEvent(nhKey, tKey1, tKey2);

        // Exercise
        Optional<Event> optionalEvent = eventDaoImpl.findEvent(nhKey, eKey);

        // Validations & Post Conditions
        assertTrue(optionalEvent.isPresent());
        assertEquals(eKey, optionalEvent.get().getEventId().longValue());
    }

    @Test
    public void find_neighborhoodId_eventId_invalid_eventId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long tKey1 = testInserter.createTime(EVENT_START_TIME);
        long tKey2 = testInserter.createTime(EVENT_END_TIME);
        long eKey = testInserter.createEvent(nhKey, tKey1, tKey2);

        // Exercise
        Optional<Event> optionalEvent = eventDaoImpl.findEvent(nhKey, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalEvent.isPresent());
    }

    @Test
    public void find_neighborhoodId_eventId_invalid_neighborhoodId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long tKey1 = testInserter.createTime(EVENT_START_TIME);
        long tKey2 = testInserter.createTime(EVENT_END_TIME);
        long eKey = testInserter.createEvent(nhKey, tKey1, tKey2);

        // Exercise
        Optional<Event> optionalEvent = eventDaoImpl.findEvent(INVALID_ID, eKey);

        // Validations & Post Conditions
        assertFalse(optionalEvent.isPresent());
    }

    @Test
    public void find_neighborhoodId_eventId_invalid_eventId_neighborhoodId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long tKey1 = testInserter.createTime(EVENT_START_TIME);
        long tKey2 = testInserter.createTime(EVENT_END_TIME);
        long eKey = testInserter.createEvent(nhKey, tKey1, tKey2);

        // Exercise
        Optional<Event> optionalEvent = eventDaoImpl.findEvent(INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalEvent.isPresent());
    }

    // -------------------------------------------------- GETS ---------------------------------------------------------

    @Test
    public void get() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long tKey1 = testInserter.createTime(EVENT_START_TIME);
        long tKey2 = testInserter.createTime(EVENT_END_TIME);
        long eKey1 = testInserter.createEvent(nhKey, tKey1, tKey2, TestConstants.DATE_1);
        long eKey2 = testInserter.createEvent(nhKey, tKey1, tKey2, TestConstants.DATE_1);
        long eKey3 = testInserter.createEvent(nhKey, tKey1, tKey2, TestConstants.DATE_1);

        // Exercise
        List<Event> eventList = eventDaoImpl.getEvents(nhKey, DATE_1, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(THREE_ELEMENTS, eventList.size());
    }

    @Test
    public void get_empty() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long tKey1 = testInserter.createTime(EVENT_START_TIME);
        long tKey2 = testInserter.createTime(EVENT_END_TIME);

        // Exercise
        List<Event> eventList = eventDaoImpl.getEvents(nhKey, NO_DATE, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertTrue(eventList.isEmpty());
    }

    // ---------------------------------------------- PAGINATION -------------------------------------------------------

    @Test
    public void get_pagination() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long tKey1 = testInserter.createTime(EVENT_START_TIME);
        long tKey2 = testInserter.createTime(EVENT_END_TIME);
        long eKey1 = testInserter.createEvent(nhKey, tKey1, tKey2, TestConstants.DATE_1);
        long eKey2 = testInserter.createEvent(nhKey, tKey1, tKey2, TestConstants.DATE_1);
        long eKey3 = testInserter.createEvent(nhKey, tKey1, tKey2, TestConstants.DATE_1);

        // Exercise
        List<Event> eventList = eventDaoImpl.getEvents(nhKey, DATE_1, TEST_PAGE, TEST_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, eventList.size());
    }

    // ------------------------------------------------- COUNTS ---------------------------------------------------------

    @Test
    public void count() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long tKey1 = testInserter.createTime(EVENT_START_TIME);
        long tKey2 = testInserter.createTime(EVENT_END_TIME);
        long eKey1 = testInserter.createEvent(nhKey, tKey1, tKey2, TestConstants.DATE_1);
        long eKey2 = testInserter.createEvent(nhKey, tKey1, tKey2, TestConstants.DATE_1);
        long eKey3 = testInserter.createEvent(nhKey, tKey1, tKey2, TestConstants.DATE_1);

        // Exercise
        int countEvent = eventDaoImpl.countEvents(nhKey, DATE_1);

        // Validations & Post Conditions
        assertEquals(THREE_ELEMENTS, countEvent);
    }

    @Test
    public void count_empty() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();

        // Exercise
        int countEvents = eventDaoImpl.countEvents(nhKey, NO_DATE);

        // Validations & Post Conditions
        assertEquals(NO_ELEMENTS, countEvents);
    }

    // ------------------------------------------------ DELETES --------------------------------------------------------

    @Test
    public void delete_neighborhoodId_eventId_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long tKey1 = testInserter.createTime(EVENT_START_TIME);
        long tKey2 = testInserter.createTime(EVENT_END_TIME);
        long eKey = testInserter.createEvent(nhKey, tKey1, tKey2);

        // Exercise
        boolean deleted = eventDaoImpl.deleteEvent(nhKey, eKey);

        // Validations & Post Conditions
        em.flush();
        assertTrue(deleted);
        assertEquals(NO_ELEMENTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.events.name()));
    }

    @Test
    public void delete_neighborhoodId_eventId_invalid_neighborhoodId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long tKey1 = testInserter.createTime(EVENT_START_TIME);
        long tKey2 = testInserter.createTime(EVENT_END_TIME);
        long eKey = testInserter.createEvent(nhKey, tKey1, tKey2);

        // Exercise
        boolean deleted = eventDaoImpl.deleteEvent(nhKey, INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }

    @Test
    public void delete_neighborhoodId_eventId_invalid_eventId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long tKey1 = testInserter.createTime(EVENT_START_TIME);
        long tKey2 = testInserter.createTime(EVENT_END_TIME);
        long eKey = testInserter.createEvent(nhKey, tKey1, tKey2);

        // Exercise
        boolean deleted = eventDaoImpl.deleteEvent(INVALID_ID, eKey);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }

    @Test
    public void delete_neighborhoodId_eventId_invalid_neighborhoodId_eventId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long tKey1 = testInserter.createTime(EVENT_START_TIME);
        long tKey2 = testInserter.createTime(EVENT_END_TIME);
        long eKey = testInserter.createEvent(nhKey, tKey1, tKey2);

        // Exercise
        boolean deleted = eventDaoImpl.deleteEvent(INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }
}
