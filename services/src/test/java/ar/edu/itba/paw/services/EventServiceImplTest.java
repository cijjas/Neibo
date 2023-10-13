package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.Neighborhood;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class) // Le decimos a JUnit que corra los tests con el runner de Mockito
public class EventServiceImplTest {

    private Neighborhood mockNeighborhood;
    private static final long ID = 1;
    private static final String NAME = "Feria";
    private static final String DESCRIPTION = "Feria de comida";
    private static final Date DATE = new Date(2023, 9, 11);
    private static final long DURATION = 180;
    private static final long NEIGHBORHOOD_ID = 1;
    private static final String NEIGHBORHOOD_NAME = "Varsovia";

    @Mock
    private EventDao eventDao;
    @InjectMocks
    private EventServiceImpl es;

//    @Test
//    public void testCreate() {
//        // 1. Precondiciones
//        // Defino el comportamiento de la clase mock de UserDao
//        when(eventDao.createEvent(anyString(), anyString(), any(), anyLong(), anyLong())).thenReturn(new Event.Builder()
//                .eventId(ID)
//                .name(NAME)
//                .description(DESCRIPTION)
//                .date(DATE)
//                .duration(DURATION)
//                .neighborhoodId(NEIGHBORHOOD_ID)
//                .build()
//        );
//
//        // 2. Ejercitar
//        // Pruebo la funcionalidad de usuarios
//        Event newEvent = es.createEvent(NAME, DESCRIPTION, DATE, DURATION, NEIGHBORHOOD_ID);
//
//        // 3. Postcondiciones
//        Assert.assertNotNull(newEvent);
//        Assert.assertEquals(newEvent.getEventId(), ID);
//        Assert.assertEquals(newEvent.getName(), NAME);
//        Assert.assertEquals(newEvent.getDescription(), DESCRIPTION);
//        Assert.assertEquals(newEvent.getDate(), DATE);
//        Assert.assertEquals(newEvent.getDuration(), DURATION);
//        Assert.assertEquals(newEvent.getNeighborhoodId(), NEIGHBORHOOD_ID);
//
//        // Verifico que se haya llamado create del UserDao una vez
//        // NUNCA HAGAN ESTO, PORQUE ESTAS PROBANDO EL UserServiceImpl QUE TE IMPORTA CÓMO EL USA EL UserDao
//        // Mockito.verify(userDao, times(1)).create(EMAIL, PASSWORD);
//    }
//    @Test(expected = RuntimeException.class) // "Espero que este test lance y falle con una exception tal"
//    public void testCreateAlreadyExists() {
//        // 1. Precondiciones
//        // Defino el comportamiento de la clase mock de UserDao
//        when(eventDao.createEvent(eq(NAME),eq(DESCRIPTION),eq(DATE),eq(DURATION),eq(NEIGHBORHOOD_ID))).thenThrow(RuntimeException.class);
//
//        // 2. Ejercitar
//        Event newEvent = es.createEvent(NAME, DESCRIPTION, DATE, DURATION, NEIGHBORHOOD_ID);
//
//        // 3. Postcondiciones
//        // (Nada, espero que lo anterior tire exception)
//    }

    @Test
    public void testFindById() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(eventDao.findEventById(eq(ID))).thenReturn(Optional.of(new Event.Builder()
                .eventId(ID)
                .name(NAME)
                .description(DESCRIPTION)
                .date(DATE)
                .duration(DURATION)
                .neighborhoodId(NEIGHBORHOOD_ID)
                .build()
        ));

        // 2. Ejercitar
        Optional<Event> newEvent = es.findEventById(ID);

        // 3. Postcondiciones
        Assert.assertTrue(newEvent.isPresent());
        Assert.assertEquals(ID, newEvent.get().getEventId());
    }

}
