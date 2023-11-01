package ar.edu.itba.paw.persistence.JunctionEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.models.JunctionEntities.Booking;
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
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class BookingDaoImplTest {

    private final Date RESERVATION_DATE = Date.valueOf("2024-12-12");
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private BookingDao bookingDao;
    @PersistenceContext
    private EntityManager em;


    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreateBooking() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long aKey = testInserter.createAmenity(nhKey);
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey, tKey);
        long avKey = testInserter.createAvailability(aKey, sKey);

        // Exercise
        Booking bookingId = bookingDao.createBooking(uKey, avKey, RESERVATION_DATE);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(bookingId);
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.users_availability.name()));
    }

    @Test
    public void testGetUserBookings() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long aKey = testInserter.createAmenity(nhKey);
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey, tKey);
        long avKey = testInserter.createAvailability(aKey, sKey);
        long bKey = testInserter.createBooking(uKey, avKey, RESERVATION_DATE);

        // Exercise
        List<Booking> userBookings = bookingDao.getUserBookings(uKey);

        // Validations & Post Conditions
        assertEquals(1, userBookings.size());
    }

    @Test
    public void testGetNoUserBookings() {
        // Pre Conditions

        // Exercise
        List<Booking> userBookings = bookingDao.getUserBookings(1);

        // Validations & Post Conditions
        assertEquals(0, userBookings.size());
    }

    @Test
    public void testDeleteBooking() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long aKey = testInserter.createAmenity(nhKey);
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey, tKey);
        long avKey = testInserter.createAvailability(aKey, sKey);
        long bKey = testInserter.createBooking(uKey, avKey, RESERVATION_DATE);

        // Exercise
        boolean deleted = bookingDao.deleteBooking(bKey);

        // Validations & Post Conditions
        assertTrue(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.users_availability.name()));
    }

    @Test
    public void testDeleteInvalidBooking() {
        // Pre Conditions

        // Exercise
        boolean deleted = bookingDao.deleteBooking(1);

        // Validations & Post Conditions
        assertFalse(deleted);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.users_availability.name()));
    }
}
