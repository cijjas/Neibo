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

    private final Date BOOKING_DATE_1 = Date.valueOf("2024-12-12");
    private final Date BOOKING_DATE_2 = Date.valueOf("2024-12-11");
    private final Date BOOKING_DATE_3 = Date.valueOf("2024-12-10");
    private final Date BOOKING_DATE_4 = Date.valueOf("2024-12-9");
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
        Booking booking= bookingDaoImpl.createBooking(uKey, avKey, BOOKING_DATE_1);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(booking);
        assertEquals(BOOKING_DATE_1, booking.getBookingDate());
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
        long bKey = testInserter.createBooking(uKey, avKey, BOOKING_DATE_1);

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
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long uKey3 = testInserter.createUser(USER_MAIL_3, nhKey);
        long aKey1 = testInserter.createAmenity(nhKey);
        long aKey2 = testInserter.createAmenity(nhKey);
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey, tKey);
        long avKey1 = testInserter.createAvailability(aKey1, sKey);
        long avKey2 = testInserter.createAvailability(aKey2, sKey);
        long bKey1 = testInserter.createBooking(uKey1, avKey1, BOOKING_DATE_1);
        long bKey2 = testInserter.createBooking(uKey1, avKey1, BOOKING_DATE_2);
        long bKey3 = testInserter.createBooking(uKey2, avKey2, BOOKING_DATE_1);

        // Exercise
        List<Booking> bookingList = bookingDaoImpl.getBookings(EMPTY_FIELD, EMPTY_FIELD, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(THREE_ELEMENTS, bookingList.size());
    }

        @Test
    public void get_userId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long uKey3 = testInserter.createUser(USER_MAIL_3, nhKey);
        long aKey1 = testInserter.createAmenity(nhKey);
        long aKey2 = testInserter.createAmenity(nhKey);
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey, tKey);
        long avKey1 = testInserter.createAvailability(aKey1, sKey);
        long avKey2 = testInserter.createAvailability(aKey2, sKey);
        long bKey1 = testInserter.createBooking(uKey1, avKey1, BOOKING_DATE_1);
        long bKey2 = testInserter.createBooking(uKey1, avKey1, BOOKING_DATE_2);
        long bKey3 = testInserter.createBooking(uKey2, avKey2, BOOKING_DATE_1);

        // Exercise
        List<Booking> bookingList = bookingDaoImpl.getBookings(uKey1, EMPTY_FIELD, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, bookingList.size());
    }

            @Test
    public void get_amenityId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long uKey3 = testInserter.createUser(USER_MAIL_3, nhKey);
        long aKey1 = testInserter.createAmenity(nhKey);
        long aKey2 = testInserter.createAmenity(nhKey);
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey, tKey);
        long avKey1 = testInserter.createAvailability(aKey1, sKey);
        long avKey2 = testInserter.createAvailability(aKey2, sKey);
        long bKey1 = testInserter.createBooking(uKey1, avKey1, BOOKING_DATE_1);
        long bKey2 = testInserter.createBooking(uKey1, avKey1, BOOKING_DATE_2);
        long bKey3 = testInserter.createBooking(uKey2, avKey2, BOOKING_DATE_1);

        // Exercise
        List<Booking> bookingList = bookingDaoImpl.getBookings(EMPTY_FIELD, aKey1, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, bookingList.size());
    }

    @Test
    public void get_userId_amenityId() {
        // Pre Conditions
        long nhKey = testInserter.createNeighborhood();
        long uKey1 = testInserter.createUser(USER_MAIL_1, nhKey);
        long uKey2 = testInserter.createUser(USER_MAIL_2, nhKey);
        long uKey3 = testInserter.createUser(USER_MAIL_3, nhKey);
        long aKey1 = testInserter.createAmenity(nhKey);
        long aKey2 = testInserter.createAmenity(nhKey);
        long dKey = testInserter.createDay();
        long tKey = testInserter.createTime();
        long sKey = testInserter.createShift(dKey, tKey);
        long avKey1 = testInserter.createAvailability(aKey1, sKey);
        long avKey2 = testInserter.createAvailability(aKey2, sKey);
        long bKey1 = testInserter.createBooking(uKey1, avKey1, BOOKING_DATE_1);
        long bKey2 = testInserter.createBooking(uKey1, avKey1, BOOKING_DATE_2);
        long bKey3 = testInserter.createBooking(uKey2, avKey2, BOOKING_DATE_1);

        // Exercise
        List<Booking> bookingList = bookingDaoImpl.getBookings(uKey1, avKey1, BASE_PAGE, BASE_PAGE_SIZE);

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, bookingList.size());
    }

    @Test
    public void get_empty() {
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
        long bKey = testInserter.createBooking(uKey, avKey, BOOKING_DATE_1);

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
        long bKey = testInserter.createBooking(uKey, avKey, BOOKING_DATE_1);

        // Exercise
        boolean deleted = bookingDaoImpl.deleteBooking(INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }
}
