package ar.edu.itba.paw.persistence.JunctionEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Entities.Booking;
import ar.edu.itba.paw.persistence.JunctionDaos.BookingDaoImpl;
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
public class BookingDaoImplTest {

    private static long nhKey;
    private static long nhKey2;
    private static long uKey1;
    private static long uKey2;
    private static long uKey3;
    private static long uKey4;
    private static long aKey1;
    private static long aKey2;
    private static long aKey3;

    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private BookingDaoImpl bookingDaoImpl;
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
        Booking booking = bookingDaoImpl.createBooking(uKey, avKey, DATE_1);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(booking);
        assertEquals(DATE_1, booking.getBookingDate());
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
        long bKey = testInserter.createBooking(uKey, avKey, DATE_1);

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

    // -------------------------------------------------- GETS ---------------------------------------------------------

    @Test
    public void get() {
        // Pre Conditions
        populateBookings();

        // Exercise
        List<Booking> bookingList = bookingDaoImpl.getBookings(nhKey, EMPTY_FIELD, EMPTY_FIELD, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(THREE_ELEMENTS, bookingList.size());
    }

    @Test
    public void get_userId() {
        // Pre Conditions
        populateBookings();

        // Exercise
        List<Booking> bookingList = bookingDaoImpl.getBookings(nhKey, uKey1, EMPTY_FIELD, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, bookingList.size());
    }

    @Test
    public void get_amenityId() {
        // Pre Conditions
        populateBookings();

        // Exercise
        List<Booking> bookingList = bookingDaoImpl.getBookings(nhKey, EMPTY_FIELD, aKey1, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, bookingList.size());
    }

    @Test
    public void get_userId_amenityId() {
        // Pre Conditions
        populateBookings();

        // Exercise
        List<Booking> bookingList = bookingDaoImpl.getBookings(nhKey, uKey1, aKey1, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, bookingList.size());
    }

    @Test
    public void get_empty() {
        // Pre Conditions

        // Exercise
        List<Booking> bookingList = bookingDaoImpl.getBookings(nhKey, EMPTY_FIELD, EMPTY_FIELD, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertTrue(bookingList.isEmpty());
    }

    // ---------------------------------------------- PAGINATION -------------------------------------------------------

    @Test
    public void get_pagination() {
        // Pre Conditions
        nhKey = testInserter.createNeighborhood();
        uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        uKey3 = testInserter.createUser(USER_MAIL_3, nhKey);
        aKey1 = testInserter.createAmenity(nhKey);
        aKey2 = testInserter.createAmenity(nhKey);
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey, tKey);
        long avKey1 = testInserter.createAvailability(aKey1, sKey);
        testInserter.createAvailability(aKey2, sKey);
        testInserter.createBooking(uKey1, avKey1, DATE_1);
        testInserter.createBooking(uKey1, avKey1, DATE_2);
        testInserter.createBooking(uKey1, avKey1, DATE_3);

        // Exercise
        List<Booking> bookingList = bookingDaoImpl.getBookings(nhKey, EMPTY_FIELD, EMPTY_FIELD, TEST_PAGE, TEST_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(ONE_ELEMENT, bookingList.size());
    }

    // ------------------------------------------------- COUNTS ---------------------------------------------------------

    @Test
    public void count() {
        // Pre Conditions
        populateBookings();

        // Exercise
        int countBookings = bookingDaoImpl.countBookings(nhKey, EMPTY_FIELD, EMPTY_FIELD);

        // Validations & Post Conditions
        assertEquals(THREE_ELEMENTS, countBookings);
    }

    @Test
    public void count_userId() {
        // Pre Conditions
        populateBookings();

        // Exercise
        int countBookings = bookingDaoImpl.countBookings(nhKey, uKey1, EMPTY_FIELD);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, countBookings);
    }

    @Test
    public void count_amenityId() {
        // Pre Conditions
        populateBookings();

        // Exercise
        int countBookings = bookingDaoImpl.countBookings(nhKey, EMPTY_FIELD, aKey1);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, countBookings);
    }

    @Test
    public void count_userId_amenityId() {
        // Pre Conditions
        populateBookings();

        // Exercise
        int countBookings = bookingDaoImpl.countBookings(nhKey, uKey1, aKey1);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, countBookings);
    }

    @Test
    public void count_empty() {
        // Pre Conditions

        // Exercise
        int countBookings = bookingDaoImpl.countBookings(nhKey, EMPTY_FIELD, EMPTY_FIELD);

        // Validations & Post Conditions
        assertEquals(NO_ELEMENTS, countBookings);
    }

    // ------------------------------------------------ DELETES --------------------------------------------------------

    @Test
    public void delete_neighborhoodId_bookingId_valid() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long aKey = testInserter.createAmenity(nhKey);
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey, tKey);
        long avKey = testInserter.createAvailability(aKey, sKey);
        long bKey = testInserter.createBooking(uKey, avKey, DATE_1);

        // Exercise
        boolean deleted = bookingDaoImpl.deleteBooking(nhKey, bKey);

        // Validations & Post Conditions
        em.flush();
        assertTrue(deleted);
        assertEquals(NO_ELEMENTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.users_availability.name()));
    }

    @Test
    public void delete_neighborhoodId_bookingId_invalid_neighborhoodId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long aKey = testInserter.createAmenity(nhKey);
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey, tKey);
        long avKey = testInserter.createAvailability(aKey, sKey);
        long bKey = testInserter.createBooking(uKey, avKey, DATE_1);

        // Exercise
        boolean deleted = bookingDaoImpl.deleteBooking(INVALID_ID, bKey);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }

    @Test
    public void delete_neighborhoodId_bookingId_invalid_bookingId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long aKey = testInserter.createAmenity(nhKey);
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey, tKey);
        long avKey = testInserter.createAvailability(aKey, sKey);
        long bKey = testInserter.createBooking(uKey, avKey, DATE_1);

        // Exercise
        boolean deleted = bookingDaoImpl.deleteBooking(nhKey, INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }

    @Test
    public void delete_neighborhoodId_bookingId_invalid_neighborhoodId_bookingId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey = testInserter.createUser(nhKey);
        long aKey = testInserter.createAmenity(nhKey);
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey, tKey);
        long avKey = testInserter.createAvailability(aKey, sKey);
        long bKey = testInserter.createBooking(uKey, avKey, DATE_1);

        // Exercise
        boolean deleted = bookingDaoImpl.deleteBooking(INVALID_ID, INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }

    // ----------------------------------------------- POPULATION ------------------------------------------------------

    private void populateBookings() {
        nhKey = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_1);
        nhKey2 = testInserter.createNeighborhood(NEIGHBORHOOD_NAME_2);
        uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        uKey3 = testInserter.createUser(USER_MAIL_3, nhKey);
        uKey4 = testInserter.createUser(USER_MAIL_4, nhKey2);
        aKey1 = testInserter.createAmenity(nhKey);
        aKey2 = testInserter.createAmenity(nhKey);
        aKey3 = testInserter.createAmenity(nhKey2);
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey, tKey);
        long avKey1 = testInserter.createAvailability(aKey1, sKey);
        long avKey2 = testInserter.createAvailability(aKey2, sKey);
        long avKey3 = testInserter.createAvailability(aKey3, sKey);
        long bKey1 = testInserter.createBooking(uKey1, avKey1, DATE_1);
        long bKey2 = testInserter.createBooking(uKey1, avKey1, DATE_2);
        long bKey3 = testInserter.createBooking(uKey2, avKey2, DATE_1);
        long bKey4 = testInserter.createBooking(uKey2, avKey3, DATE_1);
    }
}
