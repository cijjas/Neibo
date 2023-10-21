package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.persistence.config.TestConfig;
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
import java.sql.Time;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Sql("classpath:hsqlValueCleanUp.sql")
public class EventDaoImplTest {

    private static final String EVENT_NAME = "Sample Event";
    private static final String EVENT_DESCRIPTION = "Sample Description";
    private static final Date EVENT_DATE = Date.valueOf("2022-12-12");
    private static final Time EVENT_START_TIME = Time.valueOf("22:00:00");
    private static final Time EVENT_END_TIME = Time.valueOf("23:00:00");

    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    private EventDaoImpl eventDao;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        eventDao = new EventDaoImpl(ds);
    }


    @Test
    public void testCreateEvent() {
        // Pre Conditions
        long tKey1 = testInserter.createTime(EVENT_START_TIME);
        long tKey2 = testInserter.createTime(EVENT_END_TIME);
        long nhKey = testInserter.createNeighborhood();

        // Exercise
        Event e = eventDao.createEvent(EVENT_NAME, EVENT_DESCRIPTION, EVENT_DATE, tKey1, tKey2, nhKey);

        // Validations & Post Conditions
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.events.name()));
        assertEquals(EVENT_NAME, e.getName());
        assertEquals(EVENT_DESCRIPTION, e.getDescription());
        assertEquals(EVENT_DATE, e.getDate());
    }


    @Test
    public void testFindEventById() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long tKey1 = testInserter.createTime(EVENT_START_TIME);
        long tKey2 = testInserter.createTime(EVENT_END_TIME);
        long eKey = testInserter.createEvent(nhKey, tKey1, tKey2);

        // Exercise
        Optional<Event> event = eventDao.findEventById(eKey);

        // Validations & Post Conditions
        assertTrue(event.isPresent());
        assertEquals(eKey, event.get().getEventId().longValue());
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
        long nhKey = testInserter.createNeighborhood();
        long tKey1 = testInserter.createTime(EVENT_START_TIME);
        long tKey2 = testInserter.createTime(EVENT_END_TIME);
        long eKey = testInserter.createEvent(nhKey, tKey1, tKey2);

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
        long nhKey = testInserter.createNeighborhood();
        long tKey1 = testInserter.createTime();
        long tKey2 = testInserter.createTime();
        eventDao.createEvent(EVENT_NAME, EVENT_DESCRIPTION, EVENT_DATE, tKey1, tKey2, nhKey);


        // Exercise
        List<Event> events = eventDao.getEventsByDate(EVENT_DATE, nhKey);

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
        long nhKey = testInserter.createNeighborhood();
        long tKey1 = testInserter.createTime(EVENT_START_TIME);
        long tKey2 = testInserter.createTime(EVENT_END_TIME);
        long eKey = testInserter.createEvent(nhKey, tKey1, tKey2);

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
        long nhKey = testInserter.createNeighborhood();
        long tKey1 = testInserter.createTime(EVENT_START_TIME);
        long tKey2 = testInserter.createTime(EVENT_END_TIME);
        long eKey = testInserter.createEvent(nhKey, tKey1, tKey2);

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

    @Test
    public void testDeleteEvent() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long tKey1 = testInserter.createTime(EVENT_START_TIME);
        long tKey2 = testInserter.createTime(EVENT_END_TIME);
        long eKey = testInserter.createEvent(nhKey, tKey1, tKey2);

        // Exercise
        boolean deleted = eventDao.deleteEvent(eKey);

        // Validations & Post Conditions
        assertTrue(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.events.name()));
    }

    @Test
    public void testDeleteInvalidEvent() {
        // Pre Conditions

        // Exercise
        boolean deleted = eventDao.deleteEvent(1);

        // Validations & Post Conditions
        assertFalse(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.events.name()));
    }
}
