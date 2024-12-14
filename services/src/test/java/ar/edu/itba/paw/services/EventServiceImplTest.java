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

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
}
