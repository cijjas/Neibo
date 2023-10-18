package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.interfaces.persistence.TimeDao;
import ar.edu.itba.paw.models.Event;
import ar.edu.itba.paw.models.Neighborhood;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Time;
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
    private static final Time START_TIME = new Time(12, 0, 0);
    private static final Time END_TIME = new Time(15, 0, 0);
    private static final long START_TIME_ID = 1;
    private static final long END_TIME_ID = 2;

    @Mock
    private EventDao eventDao;
    @Mock
    private TimeDao timeDao;
    @InjectMocks
    private EventServiceImpl es;

    @Test
    public void testCreate() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(eventDao.createEvent(anyString(), anyString(), any(), anyLong(), anyLong(), anyLong())).thenReturn(new Event.Builder()
                .eventId(ID)
                .name(NAME)
                .description(DESCRIPTION)
                .date(DATE)
                .startTime(START_TIME)
                .endTime(END_TIME)
                .neighborhoodId(NEIGHBORHOOD_ID)
                .build()
        );

        // 2. Ejercitar
        // Pruebo la funcionalidad de usuarios
        Event newEvent = es.createEvent(NAME, DESCRIPTION, DATE, START_TIME, END_TIME, NEIGHBORHOOD_ID);

        // 3. Postcondiciones
        Assert.assertNotNull(newEvent);
        Assert.assertEquals(newEvent.getEventId(), ID);
        Assert.assertEquals(newEvent.getName(), NAME);
        Assert.assertEquals(newEvent.getDescription(), DESCRIPTION);
        Assert.assertEquals(newEvent.getDate(), DATE);
        Assert.assertEquals(newEvent.getStartTime(), START_TIME);
        Assert.assertEquals(newEvent.getEndTime(), END_TIME);
        Assert.assertEquals(newEvent.getNeighborhoodId(), NEIGHBORHOOD_ID);

        // Verifico que se haya llamado create del UserDao una vez
        // NUNCA HAGAN ESTO, PORQUE ESTAS PROBANDO EL UserServiceImpl QUE TE IMPORTA CÓMO EL USA EL UserDao
        // Mockito.verify(userDao, times(1)).create(EMAIL, PASSWORD);
    }
    @Test(expected = RuntimeException.class) // "Espero que este test lance y falle con una exception tal"
    public void testCreateAlreadyExists() {
        // 1. Precondiciones
        // Defino el comportamiento de la clase mock de UserDao
        when(eventDao.createEvent(eq(NAME),eq(DESCRIPTION),eq(DATE),eq(START_TIME_ID),eq(END_TIME_ID),eq(NEIGHBORHOOD_ID))).thenThrow(RuntimeException.class);

        // 2. Ejercitar
        Event newEvent = es.createEvent(NAME, DESCRIPTION, DATE, START_TIME, END_TIME, NEIGHBORHOOD_ID);

        // 3. Postcondiciones
        // (Nada, espero que lo anterior tire exception)
    }

}
