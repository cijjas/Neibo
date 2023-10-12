package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Event;
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
import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:hsqlValueCleanUp.sql")
public class EventDaoImplTest {

    private JdbcTemplate jdbcTemplate;
    private TestInsertionUtils testInsertionUtils;
    private EventDaoImpl eventDao;

    private static final String EVENT_NAME = "Sample Event";
    private static final String EVENT_DESCRIPTION = "Sample Description";
    private static final Date EVENT_DATE = Date.valueOf("2022-12-12");
    private static final long EVENT_DURATION = 60; // Minutes

    @Autowired
    private DataSource ds;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        eventDao = new EventDaoImpl(ds);
        testInsertionUtils = new TestInsertionUtils(jdbcTemplate, ds);
    }

    @Test
    public void testCreateEvent() {
        // Pre Conditions
        long neighborhoodId = testInsertionUtils.createNeighborhood();

        // Exercise
        Event e = eventDao.createEvent(EVENT_NAME, EVENT_DESCRIPTION, EVENT_DATE, EVENT_DURATION, neighborhoodId);

        // Validations & Post Conditions
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.events.name()));
        assertEquals(EVENT_NAME, e.getName());
        assertEquals(EVENT_DESCRIPTION, e.getDescription());
        assertEquals(EVENT_DATE, e.getDate());
        assertEquals(EVENT_DURATION, e.getDuration());
    }

    @Test
    public void testFindEventById() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();
        long eId = testInsertionUtils.createEvent(nhKey);

        // Exercise
        Optional<Event> event = eventDao.findEventById(eId);

        // Validations & Post Conditions
        assertTrue(event.isPresent());
        assertEquals(eId, event.get().getEventId());
    }

    @Test
    public void testFindEventByInvalidId() {
        // Pre Conditions

        // Exercise
        Optional<Event> event = eventDao.findEventById(1);

        // Validations & Post Conditions
        assertFalse(event.isPresent());
    }

    @Test
    public void testGetEvents() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();
        long eId = testInsertionUtils.createEvent(nhKey);

        // Exercise
        List<Event> events = eventDao.getEventsByNeighborhoodId(nhKey);

        // Validations & Post Conditions
        assertEquals(1, events.size());
    }

    @Test
    public void testGetNoEvents() {
        // Pre Conditions

        // Exercise
        List<Event> events = eventDao.getEventsByNeighborhoodId(1);

        // Validations & Post Conditions
        assertEquals(0, events.size());
    }

    @Test
    public void testGetEventsByDate() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();
        long eId = testInsertionUtils.createEvent("Event", "Event Desc", java.sql.Date.valueOf("2001-3-14"), 60, nhKey);

        // Exercise
        List<Event> events = eventDao.getEventsByDate(java.sql.Date.valueOf("2001-3-14"), nhKey);

        // Validations & Post Conditions
        assertEquals(1, events.size());
    }

    @Test
    public void testGetNoEventsByDate() {
        // Pre Conditions

        // Exercise
        List<Event> events = eventDao.getEventsByNeighborhoodId(1);

        // Validations & Post Conditions
        assertEquals(0, events.size());
    }

    @Test
    public void testGetEventsByNeighborhood() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();
        long eId = testInsertionUtils.createEvent(nhKey);

        // Exercise
        List<Event> events = eventDao.getEventsByNeighborhoodId(nhKey);

        // Validations & Post Conditions
        assertEquals(1, events.size());
    }

    @Test
    public void testGetNoEventsByNeighborhood() {
        // Pre Conditions

        // Exercise
        List<Event> events = eventDao.getEventsByNeighborhoodId(1);

        // Validations & Post Conditions
        assertEquals(0, events.size());
    }

    @Test
    public void testGetEventDates() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();
        long eId = testInsertionUtils.createEvent(nhKey);

        // Exercise
        List<java.util.Date> events = eventDao.getEventDates(nhKey);

        // Validations & Post Conditions
        assertEquals(1, events.size());
    }

    @Test
    public void testGetNoEventDates() {
        // Pre Conditions

        // Exercise
        List<java.util.Date> events = eventDao.getEventDates(1);

        // Validations & Post Conditions
        assertEquals(0, events.size());
    }
}
