package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.AmenityDao;
import ar.edu.itba.paw.interfaces.persistence.AvailabilityDao;
import ar.edu.itba.paw.interfaces.persistence.ShiftDao;
import ar.edu.itba.paw.models.Amenity;
import ar.edu.itba.paw.models.Availability;
import ar.edu.itba.paw.models.Neighborhood;
import ar.edu.itba.paw.models.Shift;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AvailabilityServiceImplTest {

    private Amenity mockAmenity;
    private static final long ID = 1;
    private static final List<Shift> SCHEDULE = new ArrayList<>();
    private static final long AMENITY_ID = 1;
    private static final long SHIFT_ID = 1;


    // private final UserServiceImpl us = new UserServiceImpl(null);
    // Qué usamos como UserDao para el UserServiceImpl? No queremos conectarlo al Postgres de verdad, es una pérdida de
    // tiempo escribir un propio, por ejemplo, InMemoryTestUserDao que guarde los usuarios en un mapa en memoria...
    // Para esto generamos un mock con Mockito, y le pedimos que nos cree el UserServiceImpl inyectando la clase
    // mock-eada:
    @Mock // Le pedimos que nos genere una clase mock de UserDao
    private AvailabilityDao availabilityDao;
    @Mock
    private ShiftDao shiftDao;
    @InjectMocks // Le pedimos que cree un UserServiceImpl, y que en el ctor (que toma un UserDao) inyecte un mock.
    private AvailabilityServiceImpl as;
    @Before
    public void setUp() {
        // Create and set up the mock Neighborhood object
        mockAmenity = mock(Amenity.class);
    }
    @Test
    public void testUpdate() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(availabilityDao.createAvailability(anyLong(),anyLong())).thenReturn(ID);
        when(availabilityDao.deleteAvailability(anyLong(),anyLong())).thenReturn(true);
        when(shiftDao.getAmenityShifts(anyLong())).thenReturn(SCHEDULE);

        // 2. Ejercitar
        // Pruebo la funcionalidad de usuarios
        boolean result = as.updateAvailability(AMENITY_ID, new ArrayList<>());

        // 3. Postcondiciones
        Assert.assertEquals(result, true);


        // Verifico que se haya llamado create del UserDao una vez
        // NUNCA HAGAN ESTO, PORQUE ESTAS PROBANDO EL UserServiceImpl QUE TE IMPORTA CÓMO EL USA EL UserDao
        // Mockito.verify(userDao, times(1)).create(EMAIL, PASSWORD);
    }

}
