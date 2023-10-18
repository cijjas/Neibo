package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.interfaces.persistence.TimeDao;
import ar.edu.itba.paw.models.Event;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Time;
import java.util.Date;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EventServiceImplTest {

    private static final long ID = 1;
    private static final String NAME = "Feria";
    private static final String DESCRIPTION = "Feria de comida";
    private static final Date DATE = new Date(2023, 9, 11);
    private static final long DURATION = 180;
    private static final long NEIGHBORHOOD_ID = 1;
    private static final String NEIGHBORHOOD_NAME = "Varsovia";
    private static final Time START_TIME = new Time(12, 0, 0);
    private static final Time END_TIME = new Time(15, 0, 0);
    private static final String START_TIME_STRING ="12:00";
    private static final String END_TIME_STRING = "15:00";
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
        // 1. Preconditions
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
        when(timeDao.createTime(START_TIME)).thenReturn(new ar.edu.itba.paw.models.Time.Builder()
                .timeId(START_TIME_ID)
                .timeInterval(START_TIME)
                .build()
        );
        when(timeDao.createTime(END_TIME)).thenReturn(new ar.edu.itba.paw.models.Time.Builder()
                .timeId(END_TIME_ID)
                .timeInterval(END_TIME)
                .build()
        );

        // 2. Exercise
        Event newEvent = es.createEvent(NAME, DESCRIPTION, DATE, START_TIME_STRING, END_TIME_STRING, NEIGHBORHOOD_ID);

        // 3. Postconditions
        Assert.assertNotNull(newEvent);
        Assert.assertEquals(newEvent.getEventId(), ID);
        Assert.assertEquals(newEvent.getName(), NAME);
        Assert.assertEquals(newEvent.getDescription(), DESCRIPTION);
        Assert.assertEquals(newEvent.getDate(), DATE);
        Assert.assertEquals(newEvent.getStartTime(), START_TIME);
        Assert.assertEquals(newEvent.getEndTime(), END_TIME);
        Assert.assertEquals(newEvent.getNeighborhoodId(), NEIGHBORHOOD_ID);

    }

    @Test(expected = RuntimeException.class)
    public void testCreateAlreadyExists() {
        // 1. Preconditions
        //when(eventDao.createEvent(eq(NAME),eq(DESCRIPTION),eq(DATE),eq(START_TIME_ID),eq(END_TIME_ID),eq(NEIGHBORHOOD_ID))).thenThrow(RuntimeException.class);

        // 2. Exercise
        Event newEvent = es.createEvent(NAME, DESCRIPTION, DATE, START_TIME_STRING, END_TIME_STRING, NEIGHBORHOOD_ID);

        // 3. Postconditions
    }

}
