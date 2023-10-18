package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.AvailabilityDao;
import ar.edu.itba.paw.interfaces.persistence.BookingDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.NeighborhoodService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Time;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class) // Le decimos a JUnit que corra los tests con el runner de Mockito
public class BookingServiceImplTest {

    private Booking mockBooking1;
    private Booking mockBooking2;
    private Booking mockBooking3;
    private Booking mockBooking4;
    private static final long SHIFT_ID_1 = 1;
    private static final long SHIFT_ID_2 = 2;
    private static final long SHIFT_ID_3 = 3;
    private static final long AVAILABILITY_ID_1 = 1;
    private static final long AVAILABILITY_ID_2 = 2;
    private static final long AVAILABILITY_ID_3 = 3;
    private static final long ID = 1;
    private static final long USER_ID = 1;
    private static final String AMENITY_NAME = "Gym";
    private static String DAY_NAME = "Monday";
    private static final Time START_TIME = new Time(10, 0, 0);
    private static final Date BOOKING_DATE = new Date(2021, 9, 11);
    private static final long AMENITY_AVAILABILITY_ID = 1;
    private static final long AMENITY_ID = 1;
    private static final long ID_2 = 2;
    private static final String AMENITY_NAME_2 = "Gym";
    private static String DAY_NAME_2 = "Monday";
    private static final Time START_TIME_2 = new Time(11, 0, 0);
    private static final Date BOOKING_DATE_2 = new Date(2021, 9, 11);
    private static final long ID_3 = 3;
    private static final String AMENITY_NAME_3 = "Wrestling pit";
    private static String DAY_NAME_3 = "Monday";
    private static final Time START_TIME_3 = new Time(12, 0, 0);
    private static final Date BOOKING_DATE_3 = new Date(2021, 9, 11);
    private static final long ID_4 = 4;
    private static final String AMENITY_NAME_4 = "Wrestling pit";
    private static String DAY_NAME_4 = "Tuesday";
    private static final Time START_TIME_4 = new Time(13, 0, 0);
    private static final Date BOOKING_DATE_4 = new Date(2021, 9, 12);


    // private final UserServiceImpl us = new UserServiceImpl(null);
    // Qué usamos como UserDao para el UserServiceImpl? No queremos conectarlo al Postgres de verdad, es una pérdida de
    // tiempo escribir un propio, por ejemplo, InMemoryTestUserDao que guarde los usuarios en un mapa en memoria...
    // Para esto generamos un mock con Mockito, y le pedimos que nos cree el UserServiceImpl inyectando la clase
    // mock-eada:
    @Mock // Le pedimos que nos genere una clase mock de UserDao
    private BookingDao bookingDao;
    @Mock
    private AvailabilityDao availabilityDao;
    @InjectMocks // Le pedimos que cree un UserServiceImpl, y que en el ctor (que toma un UserDao) inyecte un mock.
    private BookingServiceImpl bs;

    @Test
    public void testCreate() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(availabilityDao.findAvailabilityId(AMENITY_ID,SHIFT_ID_1)).thenReturn(OptionalLong.of(AVAILABILITY_ID_1));
        when(availabilityDao.findAvailabilityId(AMENITY_ID,SHIFT_ID_2)).thenReturn(OptionalLong.of(AVAILABILITY_ID_2));
        when(availabilityDao.findAvailabilityId(AMENITY_ID,SHIFT_ID_3)).thenReturn(OptionalLong.of(AVAILABILITY_ID_3));

        when(bookingDao.createBooking(eq(USER_ID), eq(AVAILABILITY_ID_1), eq(BOOKING_DATE))).thenReturn(ID);
        when(bookingDao.createBooking(eq(USER_ID), eq(AVAILABILITY_ID_2), eq(BOOKING_DATE))).thenReturn(ID_2);
        when(bookingDao.createBooking(eq(USER_ID), eq(AVAILABILITY_ID_3), eq(BOOKING_DATE))).thenReturn(ID_3);

        List<Long> shiftIds = new ArrayList<>();
        shiftIds.add(SHIFT_ID_1);
        shiftIds.add(SHIFT_ID_2);
        shiftIds.add(SHIFT_ID_3);

        // 2. Ejercitar
        // Pruebo la funcionalidad de usuarios
        long[] bookingIds = bs.createBooking(USER_ID, AMENITY_ID, shiftIds, BOOKING_DATE);

        // 3. Postcondiciones
        Assert.assertNotNull(bookingIds);
        Assert.assertEquals(bookingIds.length, 3);
        Assert.assertEquals(bookingIds[0], ID);
        Assert.assertEquals(bookingIds[1], ID_2);
        Assert.assertEquals(bookingIds[2], ID_3);

        // Verifico que se haya llamado create del UserDao una vez
        // NUNCA HAGAN ESTO, PORQUE ESTAS PROBANDO EL UserServiceImpl QUE TE IMPORTA CÓMO EL USA EL UserDao
        // Mockito.verify(userDao, times(1)).create(EMAIL, PASSWORD);
    }
    @Test(expected = RuntimeException.class) // "Espero que este test lance y falle con una exception tal"
    public void testCreateAlreadyExists() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(availabilityDao.findAvailabilityId(AMENITY_ID,SHIFT_ID_1)).thenReturn(OptionalLong.of(AVAILABILITY_ID_1));
        when(bookingDao.createBooking(eq(USER_ID), eq(AMENITY_AVAILABILITY_ID), eq(BOOKING_DATE))).thenThrow(RuntimeException.class);

        List<Long> shiftIds = new ArrayList<>();
        shiftIds.add(SHIFT_ID_1);

        // 2. Ejercitar
        long[] bookingIds = bs.createBooking(USER_ID, AMENITY_ID, shiftIds, BOOKING_DATE);

        // 3. Postcondiciones
        // (Nada, espero que lo anterior tire exception)
    }

    @Test
    public void testGetUserBookings() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        mockBooking1 = mock(Booking.class);
        mockBooking2 = mock(Booking.class);
        mockBooking3 = mock(Booking.class);
        mockBooking4 = mock(Booking.class);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(mockBooking1);
        bookings.add(mockBooking2);
        bookings.add(mockBooking3);
        bookings.add(mockBooking4);

        when(mockBooking1.getAmenityName()).thenReturn(AMENITY_NAME);
        when(mockBooking1.getBookingDate()).thenReturn(BOOKING_DATE);
        when(mockBooking1.getDayName()).thenReturn(DAY_NAME);
        when(mockBooking1.getStartTime()).thenReturn(START_TIME);

        when(mockBooking2.getAmenityName()).thenReturn(AMENITY_NAME_2);
        when(mockBooking2.getBookingDate()).thenReturn(BOOKING_DATE_2);
        when(mockBooking2.getDayName()).thenReturn(DAY_NAME_2);
        when(mockBooking2.getStartTime()).thenReturn(START_TIME_2);

        when(mockBooking3.getAmenityName()).thenReturn(AMENITY_NAME_3);
        when(mockBooking3.getBookingDate()).thenReturn(BOOKING_DATE_3);
        when(mockBooking3.getDayName()).thenReturn(DAY_NAME_3);
        when(mockBooking3.getStartTime()).thenReturn(START_TIME_3);

        when(mockBooking4.getAmenityName()).thenReturn(AMENITY_NAME_4);
        when(mockBooking4.getBookingDate()).thenReturn(BOOKING_DATE_4);
        when(mockBooking4.getDayName()).thenReturn(DAY_NAME_4);
        when(mockBooking4.getStartTime()).thenReturn(START_TIME_4);

        when(bookingDao.getUserBookings(USER_ID)).thenReturn(bookings);

        // 2. Ejercitar
        List<GroupedBooking> groupedBookings = bs.getUserBookings(USER_ID);

        // 3. Postcondiciones
        Assert.assertEquals(groupedBookings.size(), 3);
        Assert.assertEquals(groupedBookings.get(0).getAmenityName(), AMENITY_NAME);
        Assert.assertEquals(groupedBookings.get(0).getDate(), BOOKING_DATE);
        Assert.assertEquals(groupedBookings.get(0).getDay(), DAY_NAME);
        Assert.assertEquals(groupedBookings.get(0).getStartTime(), START_TIME);
        Assert.assertEquals(groupedBookings.get(0).getEndTime(), new Time(12, 0, 0));

        Assert.assertEquals(groupedBookings.get(1).getAmenityName(), AMENITY_NAME_3);
        Assert.assertEquals(groupedBookings.get(1).getDate(), BOOKING_DATE_3);
        Assert.assertEquals(groupedBookings.get(1).getDay(), DAY_NAME_3);
        Assert.assertEquals(groupedBookings.get(1).getStartTime(), START_TIME_3);
        Assert.assertEquals(groupedBookings.get(1).getEndTime(), new Time(13, 0, 0));

        Assert.assertEquals(groupedBookings.get(2).getAmenityName(), AMENITY_NAME_4);
        Assert.assertEquals(groupedBookings.get(2).getDate(), BOOKING_DATE_4);
        Assert.assertEquals(groupedBookings.get(2).getDay(), DAY_NAME_4);
        Assert.assertEquals(groupedBookings.get(2).getStartTime(), START_TIME_4);
        Assert.assertEquals(groupedBookings.get(2).getEndTime(), new Time(14, 0, 0));

    }

}
