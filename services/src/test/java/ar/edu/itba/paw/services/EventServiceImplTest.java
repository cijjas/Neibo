package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.interfaces.persistence.TimeDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.models.Entities.Event;
import ar.edu.itba.paw.models.Entities.Time;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
public class EventServiceImplTest {

    @Mock
    private EventDao eventDao;
    @Mock
    private TimeDao timeDao;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    public void event_startTime_endTime() {
        // Pre Conditions
        String name = "Community Event";
        String description = "A fun community event";
        java.util.Date date = new java.util.Date();  // Use actual date
        String startTime = "10:00:00";
        String endTime = "12:00:00";
        long neighborhoodId = 123L;

        java.sql.Time sqlStartTime = java.sql.Time.valueOf(startTime);
        java.sql.Time sqlEndTime = java.sql.Time.valueOf(endTime);

        Time startTimeEntity = new Time.Builder().timeInterval(java.sql.Time.valueOf(startTime)).timeId(1L).build();
        Time endTimeEntity = new Time.Builder().timeInterval(java.sql.Time.valueOf(endTime)).timeId(2L).build();

        when(timeDao.findTime(sqlStartTime)).thenReturn(Optional.of(startTimeEntity));
        when(timeDao.findTime(sqlEndTime)).thenReturn(Optional.of(endTimeEntity));

        Event mockEvent = new Event.Builder().build();
        when(eventDao.createEvent(neighborhoodId, name, description, date, 1L, 2L)).thenReturn(mockEvent);

        // Exercise
        Event createdEvent = eventService.createEvent(neighborhoodId, description, date, startTime, endTime, name);

        // Validations & Post Conditions
        verify(timeDao, times(1)).findTime(sqlStartTime);
        verify(timeDao, times(1)).findTime(sqlEndTime);
        verify(timeDao, times(0)).createTime(sqlStartTime);
        verify(timeDao, times(0)).createTime(sqlEndTime);
        verify(eventDao, times(1)).createEvent(neighborhoodId, name, description, date, 1L, 2L);
        verify(emailService, times(1)).sendBatchEventMail(mockEvent, "event.custom.message2", neighborhoodId);

        assertNotNull(createdEvent);
    }

    @Test
    public void update_noStartTime_noEndTime() {
        // Pre Conditions
        long neighborhoodId = 1L;
        long eventId = 1L;
        String name = "Updated Event";
        String description = "Updated Description";
        Date date = new Date();
        String startTime = "10:00:00";
        String endTime = "12:00:00";
        java.sql.Time sqlStartTime = java.sql.Time.valueOf(startTime);
        java.sql.Time sqlEndTime = java.sql.Time.valueOf(endTime);

        Time startTimeEntity = new Time.Builder().timeInterval(java.sql.Time.valueOf(startTime)).timeId(1L).build();
        Time endTimeEntity = new Time.Builder().timeInterval(java.sql.Time.valueOf(endTime)).timeId(2L).build();

        Event event = new Event.Builder().build();
        when(eventDao.findEvent(neighborhoodId, eventId)).thenReturn(Optional.of(event));
        when(timeDao.findTime(sqlStartTime)).thenReturn(Optional.empty());
        when(timeDao.createTime(sqlStartTime)).thenReturn(startTimeEntity);
        when(timeDao.findTime(sqlEndTime)).thenReturn(Optional.empty());
        when(timeDao.createTime(sqlEndTime)).thenReturn(endTimeEntity);

        // Exercise
        eventService.updateEvent(neighborhoodId, eventId, name, description, date, startTime, endTime);

        // Validations & Post Conditions
        assertEquals(name, event.getName());
        assertEquals(description, event.getDescription());
        assertEquals(date, event.getDate());
        assertEquals(startTimeEntity, event.getStartTime());
        assertEquals(endTimeEntity, event.getEndTime());

        verify(eventDao, times(1)).findEvent(neighborhoodId, eventId);
        verify(timeDao, times(1)).findTime(sqlStartTime);
        verify(timeDao, times(1)).createTime(sqlStartTime);
        verify(timeDao, times(1)).findTime(sqlEndTime);
        verify(timeDao, times(1)).createTime(sqlEndTime);
    }

    @Test
    public void update_noStartTime_endTime() {
        // Pre Conditions
        long neighborhoodId = 1L;
        long eventId = 1L;
        String startTime = "10:00:00";
        java.sql.Time sqlStartTime = java.sql.Time.valueOf(startTime);
        Time startTimeEntity = new Time.Builder().timeInterval(java.sql.Time.valueOf(startTime)).timeId(1L).build();

        Event event = new Event.Builder().build();
        when(eventDao.findEvent(neighborhoodId, eventId)).thenReturn(Optional.of(event));
        when(timeDao.findTime(sqlStartTime)).thenReturn(Optional.empty());
        when(timeDao.createTime(sqlStartTime)).thenReturn(startTimeEntity);

        // Exercise
        eventService.updateEvent(neighborhoodId, eventId, null, null, null, startTime, null);

        // Validations & Post Conditions
        assertEquals(startTimeEntity, event.getStartTime());
        assertNull(event.getEndTime());

        verify(eventDao, times(1)).findEvent(neighborhoodId, eventId);
        verify(timeDao, times(1)).findTime(sqlStartTime);
        verify(timeDao, times(1)).createTime(sqlStartTime);
        verify(timeDao, times(1)).findTime(any());
        verify(timeDao, times(1)).createTime(any());
    }

    @Test
    public void update_startTime_noEndTime() {
        // Pre Conditions
        long neighborhoodId = 1L;
        long eventId = 1L;
        String endTime = "12:00:00";
        java.sql.Time sqlEndTime = java.sql.Time.valueOf(endTime);
        Time endTimeEntity = new Time.Builder().timeInterval(java.sql.Time.valueOf(endTime)).timeId(2L).build();

        Event event = new Event.Builder().build();
        when(eventDao.findEvent(neighborhoodId, eventId)).thenReturn(Optional.of(event));
        when(timeDao.findTime(sqlEndTime)).thenReturn(Optional.of(endTimeEntity));

        // Exercise
        eventService.updateEvent(neighborhoodId, eventId, null, null, null, null, endTime);

        // Validations & Post Conditions
        assertNull(event.getStartTime());
        assertEquals(endTimeEntity, event.getEndTime());

        verify(eventDao, times(1)).findEvent(neighborhoodId, eventId);
        verify(timeDao, times(1)).findTime(sqlEndTime);
        verify(timeDao, never()).createTime(any());
    }
}
