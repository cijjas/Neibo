package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.interfaces.persistence.*;
import ar.edu.itba.paw.models.Booking;
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
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInsertionUtils.class})
@Sql("classpath:hsqlValueCleanUp.sql")
public class BookingDaoImplTest {

    @Autowired
    private DataSource ds;
    @Autowired
    private TestInsertionUtils testInsertionUtils;
    private JdbcTemplate jdbcTemplate;
    private BookingDao bookingDao;
    private ShiftDao shiftDao;
    private AmenityDao amenityDao;
    private DayDao dayDao;
    private TimeDao timeDao;

    private final Date RESERVATION_DATE = Date.valueOf("2024-12-12");


    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        dayDao = new DayDaoImpl(ds);
        timeDao = new TimeDaoImpl(ds);
        shiftDao = new ShiftDaoImpl(ds, dayDao, timeDao);
        amenityDao = new AmenityDaoImpl(ds, shiftDao);
        bookingDao = new BookingDaoImpl(ds, shiftDao, amenityDao);
    }

    @Test
    public void testCreateBooking() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();
        long uKey = testInsertionUtils.createUser(nhKey);
        long aKey = testInsertionUtils.createAmenity(nhKey);
        long dKey = testInsertionUtils.createDay();
        long tKey = testInsertionUtils.createTime();
        long sKey = testInsertionUtils.createShift(dKey, tKey);
        long avKey = testInsertionUtils.createAvailability(aKey, sKey);

        // Exercise
        Number bookingId = bookingDao.createBooking(uKey, avKey, RESERVATION_DATE);

        // Validations & Post Conditions
        assertNotNull(bookingId);
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.users_availability.name()));
    }

    @Test
    public void testGetUserBookings() {
        // Pre Conditions
        long nhKey = testInsertionUtils.createNeighborhood();
        long uKey = testInsertionUtils.createUser(nhKey);
        long aKey = testInsertionUtils.createAmenity(nhKey);
        long dKey = testInsertionUtils.createDay();
        long tKey = testInsertionUtils.createTime();
        long sKey = testInsertionUtils.createShift(dKey, tKey);
        long avKey = testInsertionUtils.createAvailability(aKey, sKey);
        long bKey = testInsertionUtils.createBooking(uKey, avKey, RESERVATION_DATE);

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
        long nhKey = testInsertionUtils.createNeighborhood();
        long uKey = testInsertionUtils.createUser(nhKey);
        long aKey = testInsertionUtils.createAmenity(nhKey);
        long dKey = testInsertionUtils.createDay();
        long tKey = testInsertionUtils.createTime();
        long sKey = testInsertionUtils.createShift(dKey, tKey);
        long avKey = testInsertionUtils.createAvailability(aKey, sKey);
        long bKey = testInsertionUtils.createBooking(uKey, avKey, RESERVATION_DATE);

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
