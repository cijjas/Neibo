package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.AvailabilityDao;
import ar.edu.itba.paw.interfaces.persistence.BookingDao;
import ar.edu.itba.paw.models.Entities.Booking;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalLong;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class) // Le decimos a JUnit que corra los tests con el runner de Mockito
public class BookingServiceImplTest {

    private static final long SHIFT_ID_1 = 1;
    private static final long SHIFT_ID_2 = 2;
    private static final long SHIFT_ID_3 = 3;
    private static final long AVAILABILITY_ID_1 = 1;
    private static final long AVAILABILITY_ID_2 = 2;
    private static final long AVAILABILITY_ID_3 = 3;
    private static final long ID = 1;
    private static final long USER_ID = 1;
    private static final String AMENITY_NAME = "Gym";
    private static final Time START_TIME = new Time(10, 0, 0);
    private static final Date BOOKING_DATE = new Date(2021, 9, 11);
    private static final String BOOKING_DATE_STRING = "2021-09-11";
    private static final long AMENITY_AVAILABILITY_ID = 1;
    private static final long AMENITY_ID = 1;
    private static final long ID_2 = 2;
    private static final String AMENITY_NAME_2 = "Gym";
    private static final Time START_TIME_2 = new Time(11, 0, 0);
    private static final Date BOOKING_DATE_2 = new Date(2021, 9, 11);
    private static final long ID_3 = 3;
    private static final String AMENITY_NAME_3 = "Wrestling pit";
    private static final Time START_TIME_3 = new Time(12, 0, 0);
    private static final Date BOOKING_DATE_3 = new Date(2021, 9, 11);
    private static final long ID_4 = 4;
    private static final String AMENITY_NAME_4 = "Wrestling pit";
    private static final Time START_TIME_4 = new Time(13, 0, 0);
    private static final Date BOOKING_DATE_4 = new Date(2021, 9, 12);
    private static final long BOOKING_ID = 1;
    private static final long BOOKING_ID_2 = 2;
    private static final long BOOKING_ID_3 = 3;
    private static final long BOOKING_ID_4 = 4;
    private Booking mockBooking1;
    private Booking mockBooking2;
    private Booking mockBooking3;
    private Booking mockBooking4;
    @Mock
    private BookingDao bookingDao;
    @Mock
    private AvailabilityDao availabilityDao;
    @InjectMocks
    private BookingServiceImpl bs;

    @Before
    public void setUp() {
        mockBooking1 = mock(Booking.class);
        mockBooking2 = mock(Booking.class);
        mockBooking3 = mock(Booking.class);
        mockBooking4 = mock(Booking.class);

        when(mockBooking1.getBookingId()).thenReturn(BOOKING_ID);

        when(mockBooking2.getBookingId()).thenReturn(BOOKING_ID_2);

        when(mockBooking3.getBookingId()).thenReturn(BOOKING_ID_3);
    }

    /*@Test
    public void testCreate() {
        // 1. Preconditions
        when(availabilityDao.findId(AMENITY_ID, SHIFT_ID_1)).thenReturn(OptionalLong.of(AVAILABILITY_ID_1));
        when(availabilityDao.findId(AMENITY_ID, SHIFT_ID_2)).thenReturn(OptionalLong.of(AVAILABILITY_ID_2));
        when(availabilityDao.findId(AMENITY_ID, SHIFT_ID_3)).thenReturn(OptionalLong.of(AVAILABILITY_ID_3));

        when(bookingDao.createBooking(eq(USER_ID), eq(AVAILABILITY_ID_1), eq(BOOKING_DATE))).thenReturn(mockBooking1);
        when(bookingDao.createBooking(eq(USER_ID), eq(AVAILABILITY_ID_2), eq(BOOKING_DATE))).thenReturn(mockBooking2);
        when(bookingDao.createBooking(eq(USER_ID), eq(AVAILABILITY_ID_3), eq(BOOKING_DATE))).thenReturn(mockBooking3);

        List<Long> shiftIds = new ArrayList<>();
        shiftIds.add(SHIFT_ID_1);
        shiftIds.add(SHIFT_ID_2);
        shiftIds.add(SHIFT_ID_3);

        // 2. Exercise
        long[] bookingIds = bs.createBooking(USER_ID, AMENITY_ID, shiftIds, BOOKING_DATE_STRING);

        // 3. Postconditions
        Assert.assertNotNull(bookingIds);
        Assert.assertEquals(bookingIds.length, 3);
        Assert.assertEquals(bookingIds[0], ID);
        Assert.assertEquals(bookingIds[1], ID_2);
        Assert.assertEquals(bookingIds[2], ID_3);

    }

    @Test(expected = RuntimeException.class)
    public void testCreateAlreadyExists() {
        // 1. Preconditions
        when(availabilityDao.findId(AMENITY_ID, SHIFT_ID_1)).thenReturn(OptionalLong.of(AVAILABILITY_ID_1));
        when(bookingDao.createBooking(eq(USER_ID), eq(AMENITY_AVAILABILITY_ID), eq(BOOKING_DATE))).thenThrow(RuntimeException.class);

        List<Long> shiftIds = new ArrayList<>();
        shiftIds.add(SHIFT_ID_1);

        // 2. Exercise
        long[] bookingIds = bs.createBooking(USER_ID, AMENITY_ID, shiftIds, BOOKING_DATE_STRING);

        // 3. Postconditions
    }*/

}