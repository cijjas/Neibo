package ar.edu.itba.paw.persistence.JunctionEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.interfaces.persistence.BookingDao;
import ar.edu.itba.paw.models.Entities.Booking;
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
import java.util.Optional;

import static ar.edu.itba.paw.persistence.TestConstants.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class BookingDaoImplTest {

    private final Date BOOKING_DATE = Date.valueOf("2024-12-12");
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private BookingDao bookingDaoImpl;
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
        long aKey = testInserter.createAmenity(nhKey);
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey, tKey);
        long avKey = testInserter.createAvailability(aKey, sKey);

        // Exercise
        Booking booking= bookingDaoImpl.createBooking(uKey, avKey, BOOKING_DATE);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(booking);
        assertEquals(BOOKING_DATE, booking.getBookingDate());
        assertEquals(uKey, booking.getUser().getUserId().longValue());
        assertEquals(avKey, booking.getAmenityAvailability().getAmenityAvailabilityId().longValue());
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.users_availability.name()));
    }

    // -------------------------------------------------- FINDS --------------------------------------------------------

    @Test
    public void find_bookingId_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long aKey = testInserter.createAmenity(nhKey);
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey, tKey);
        long avKey = testInserter.createAvailability(aKey, sKey);
        long bKey = testInserter.createBooking(uKey, avKey, BOOKING_DATE);

        // Exercise
        Optional<Booking> optionalBooking = bookingDaoImpl.findBooking(bKey);

        // Validations & Post Conditions
        assertTrue(optionalBooking.isPresent());
        assertEquals(bKey, optionalBooking.get().getBookingId().longValue());
    }

    @Test
    public void find_bookingId_invalid_bookingId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long aKey = testInserter.createAmenity(nhKey);
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey, tKey);
        long avKey = testInserter.createAvailability(aKey, sKey);

        // Exercise
        Optional<Booking> optionalBooking = bookingDaoImpl.findBooking(INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalBooking.isPresent());
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
        long bKey = testInserter.createBooking(uKey, avKey, BOOKING_DATE);

        // Exercise
        List<Booking> bookingList = bookingDaoImpl.getBookings(uKey, aKey, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, bookingList.size());
    }

    @Test
    public void testGetNoUserBookings() {
        // Pre Conditions

        // Exercise
        List<Booking> bookingList = bookingDaoImpl.getBookings(EMPTY_FIELD, EMPTY_FIELD, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertTrue(bookingList.isEmpty());
    }

    // ------------------------------------------------ DELETES --------------------------------------------------------

    @Test
    public void delete_bookingId_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long aKey = testInserter.createAmenity(nhKey);
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey, tKey);
        long avKey = testInserter.createAvailability(aKey, sKey);
        long bKey = testInserter.createBooking(uKey, avKey, BOOKING_DATE);

        // Exercise
        boolean deleted = bookingDaoImpl.deleteBooking(bKey);

        // Validations & Post Conditions
        em.flush();
        assertTrue(deleted);
        assertEquals(NO_ELEMENTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.users_availability.name()));
    }

    @Test
    public void delete_bookingId_invalid_bookingId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long aKey = testInserter.createAmenity(nhKey);
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey, tKey);
        long avKey = testInserter.createAvailability(aKey, sKey);
        long bKey = testInserter.createBooking(uKey, avKey, BOOKING_DATE);

        // Exercise
        boolean deleted = bookingDaoImpl.deleteBooking(INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }
}
