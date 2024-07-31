package ar.edu.itba.paw.persistence.MainEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Entities.Event;
import ar.edu.itba.paw.persistence.MainEntitiesDaos.EventDaoImpl;
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
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class EventDaoImplTest {

    private static final String EVENT_NAME = "Sample Event";
    private static final String EVENT_DESCRIPTION = "Sample Description";
    private static final Date EVENT_DATE = Date.valueOf("2022-12-12");
    private static final String EVENT_DATE_2 = "2022-12-12";
    private static final Time EVENT_START_TIME = Time.valueOf("22:00:00");
    private static final Time EVENT_END_TIME = Time.valueOf("23:00:00");
    public static final Date DATE = Date.valueOf("2024-3-14");
    public static final Date START_DATE = Date.valueOf("2024-3-10");
    public static final Date INVALID_START_DATE = Date.valueOf("2094-3-10");
    public static final Date END_DATE = Date.valueOf("2024-3-24");
    public static final Date INVALID_END_DATE = Date.valueOf("2094-3-24");

    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EventDaoImpl eventDao;
    @PersistenceContext
    private EntityManager em;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
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
        em.flush();
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
        Optional<Event> event = eventDao.findEvent(eKey);

        // Validations & Post Conditions
        assertTrue(event.isPresent());
        assertEquals(eKey, event.get().getEventId().longValue());
    }

    @Test
    public void testFindEventByInvalidId() {
        // Pre Conditions

        // Exercise
        Optional<Event> event = eventDao.findEvent(1);

        // Validations & Post Conditions
        assertFalse(event.isPresent());
    }

    @Test
    public void testGetEventsByDate() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long tKey1 = testInserter.createTime();
        long tKey2 = testInserter.createTime();
        long eKey = testInserter.createEvent(EVENT_NAME, EVENT_DESCRIPTION, EVENT_DATE, tKey1, tKey2, nhKey);

        // Exercise
        List<Event> events = eventDao.getEvents(EVENT_DATE_2, nhKey, 1, 10);

        // Validations & Post Conditions
        assertEquals(1, events.size());
    }

    @Test
    public void testGetEventsByNeighborhoodIdAndDateRange(){
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long tKey1 = testInserter.createTime(EVENT_START_TIME);
        long tKey2 = testInserter.createTime(EVENT_END_TIME);
        long eKey1 = testInserter.createEvent(nhKey, tKey1, tKey2, DATE);
        long eKey2 = testInserter.createEvent(nhKey, tKey1, tKey2, DATE);
        long eKey3 = testInserter.createEvent(nhKey, tKey1, tKey2, DATE);

        // Exercise
        List<Event> events = eventDao.getEvents(nhKey, START_DATE, END_DATE);

        // Validations & Post Conditions
        assertEquals(3, events.size());
    }

    @Test
    public void testGetEventsByInvalidNeighborhoodIdAndDateRange(){
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long tKey1 = testInserter.createTime(EVENT_START_TIME);
        long tKey2 = testInserter.createTime(EVENT_END_TIME);
        long eKey1 = testInserter.createEvent(nhKey, tKey1, tKey2, DATE);
        long eKey2 = testInserter.createEvent(nhKey, tKey1, tKey2, DATE);
        long eKey3 = testInserter.createEvent(nhKey, tKey1, tKey2, DATE);

        // Exercise
        List<Event> events = eventDao.getEvents(-4, START_DATE, END_DATE);

        // Validations & Post Conditions
        assertTrue(events.isEmpty());
    }

    @Test
    public void testGetEventsByNeighborhoodIdAndInvalidDateRange(){
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long tKey1 = testInserter.createTime(EVENT_START_TIME);
        long tKey2 = testInserter.createTime(EVENT_END_TIME);
        long eKey1 = testInserter.createEvent(nhKey, tKey1, tKey2, DATE);
        long eKey2 = testInserter.createEvent(nhKey, tKey1, tKey2, DATE);
        long eKey3 = testInserter.createEvent(nhKey, tKey1, tKey2, DATE);

        // Exercise
        List<Event> events = eventDao.getEvents(nhKey, INVALID_START_DATE, INVALID_END_DATE);

        // Validations & Post Conditions
        assertTrue(events.isEmpty());
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
        em.flush();
        assertTrue(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.events.name()));
    }

    @Test
    public void testDeleteInvalidEvent() {
        // Pre Conditions

        // Exercise
        boolean deleted = eventDao.deleteEvent(1);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.events.name()));
    }
}
