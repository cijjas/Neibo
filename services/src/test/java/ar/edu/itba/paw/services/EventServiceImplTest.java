package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.interfaces.persistence.TimeDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.models.Entities.*;
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
    public void testCreateEvent() {
        // Setup test data
        String name = "Community Event";
        String description = "A fun community event";
        java.util.Date date = new java.util.Date();  // Use actual date
        String startTime = "10:00:00";
        String endTime = "12:00:00";
        long neighborhoodId = 123L;

        // Mock the behavior of timeDao
        java.sql.Time sqlStartTime = java.sql.Time.valueOf(startTime);
        java.sql.Time sqlEndTime = java.sql.Time.valueOf(endTime);

        Time startTimeEntity = new Time.Builder().timeInterval(java.sql.Time.valueOf(startTime)).timeId(1L).build();  // Simulate an entity with a timeId
        Time endTimeEntity = new Time.Builder().timeInterval(java.sql.Time.valueOf(endTime)).timeId(2L).build();  // Simulate an entity with a timeId

        when(timeDao.findTime(sqlStartTime)).thenReturn(Optional.of(startTimeEntity));
        when(timeDao.findTime(sqlEndTime)).thenReturn(Optional.of(endTimeEntity));

        // Mock event creation
        Event mockEvent = new Event.Builder().build();  // Mock the created event
        when(eventDao.createEvent(name, description, date, 1L, 2L, neighborhoodId)).thenReturn(mockEvent);

        // Call the method under test
        Event createdEvent = eventService.createEvent(name, description, date, startTime, endTime, neighborhoodId);

        // Verify the interactions with the mocked dependencies
        verify(timeDao, times(1)).findTime(sqlStartTime);
        verify(timeDao, times(1)).findTime(sqlEndTime);
        verify(timeDao, times(0)).createTime(sqlStartTime);  // Ensure no unnecessary creation
        verify(timeDao, times(0)).createTime(sqlEndTime);    // Ensure no unnecessary creation
        verify(eventDao, times(1)).createEvent(name, description, date, 1L, 2L, neighborhoodId);
        verify(emailService, times(1)).sendBatchEventMail(mockEvent, "event.custom.message2", neighborhoodId);

        // Assert that the event created is as expected
        assertNotNull(createdEvent);
    }

    @Test
    public void testUpdateEventWithBothStartTimeAndEndTime() {
        long eventId = 1L;
        String name = "Updated Event";
        String description = "Updated Description";
        Date date = new Date();
        String startTime = "10:00:00";
        String endTime = "12:00:00";
        java.sql.Time sqlStartTime = java.sql.Time.valueOf(startTime);
        java.sql.Time sqlEndTime = java.sql.Time.valueOf(endTime);

        Time startTimeEntity = new Time.Builder().timeInterval(java.sql.Time.valueOf(startTime)).timeId(1L).build();  // Simulate an entity with a timeId
        Time endTimeEntity = new Time.Builder().timeInterval(java.sql.Time.valueOf(endTime)).timeId(2L).build();  // Simulate an entity with a timeId

        Event event = new Event.Builder().build();
        when(eventDao.findEvent(eventId)).thenReturn(Optional.of(event));
        when(timeDao.findTime(sqlStartTime)).thenReturn(Optional.empty());
        when(timeDao.createTime(sqlStartTime)).thenReturn(startTimeEntity);
        when(timeDao.findTime(sqlEndTime)).thenReturn(Optional.empty());
        when(timeDao.createTime(sqlEndTime)).thenReturn(endTimeEntity);

        eventService.updateEventPartially(eventId, name, description, date, startTime, endTime);

        // Verify updates
        assertEquals(name, event.getName());
        assertEquals(description, event.getDescription());
        assertEquals(date, event.getDate());
        assertEquals(startTimeEntity, event.getStartTime());
        assertEquals(endTimeEntity, event.getEndTime());

        // Verify DAO interactions
        verify(eventDao, times(1)).findEvent(eventId);
        verify(timeDao, times(1)).findTime(sqlStartTime);
        verify(timeDao, times(1)).createTime(sqlStartTime);
        verify(timeDao, times(1)).findTime(sqlEndTime);
        verify(timeDao, times(1)).createTime(sqlEndTime);
    }

    @Test
    public void testUpdateEventWithOnlyStartTime() {
        long eventId = 1L;
        String startTime = "10:00:00";
        java.sql.Time sqlStartTime = java.sql.Time.valueOf(startTime);
        Time startTimeEntity = new Time.Builder().timeInterval(java.sql.Time.valueOf(startTime)).timeId(1L).build();  // Simulate an entity with a timeId

        Event event = new Event.Builder().build();
        when(eventDao.findEvent(eventId)).thenReturn(Optional.of(event));
        when(timeDao.findTime(sqlStartTime)).thenReturn(Optional.empty());
        when(timeDao.createTime(sqlStartTime)).thenReturn(startTimeEntity);

        eventService.updateEventPartially(eventId, null, null, null, startTime, null);

        // Verify updates
        assertEquals(startTimeEntity, event.getStartTime());
        assertNull(event.getEndTime());

        // Verify DAO interactions
        verify(eventDao, times(1)).findEvent(eventId);
        verify(timeDao, times(1)).findTime(sqlStartTime);
        verify(timeDao, times(1)).createTime(sqlStartTime);
        verify(timeDao, times(1)).findTime(any());
        verify(timeDao, times(1)).createTime(any());
    }

    @Test
    public void testUpdateEventWithOnlyEndTime() {
        long eventId = 1L;
        String endTime = "12:00:00";
        java.sql.Time sqlEndTime = java.sql.Time.valueOf(endTime);
        Time endTimeEntity = new Time.Builder().timeInterval(java.sql.Time.valueOf(endTime)).timeId(2L).build();  // Simulate an entity with a timeId

        Event event = new Event.Builder().build();
        when(eventDao.findEvent(eventId)).thenReturn(Optional.of(event));
        when(timeDao.findTime(sqlEndTime)).thenReturn(Optional.of(endTimeEntity));

        eventService.updateEventPartially(eventId, null, null, null, null, endTime);

        // Verify updates
        assertNull(event.getStartTime());
        assertEquals(endTimeEntity, event.getEndTime());

        // Verify DAO interactions
        verify(eventDao, times(1)).findEvent(eventId);
        verify(timeDao, times(1)).findTime(sqlEndTime);
        verify(timeDao, never()).createTime(any());
    }

    @Test
    public void testUpdateEventWithNoStartTimeOrEndTime() {
        long eventId = 1L;

        Event event = new Event.Builder().build();
        when(eventDao.findEvent(eventId)).thenReturn(Optional.of(event));

        eventService.updateEventPartially(eventId, null, null, null, null, null);

        // Verify updates
        assertNull(event.getStartTime());
        assertNull(event.getEndTime());

        // Verify DAO interactions
        verify(eventDao, times(1)).findEvent(eventId);
        verify(timeDao, never()).findTime(any());
        verify(timeDao, never()).createTime(any());
    }
}
