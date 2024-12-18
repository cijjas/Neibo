package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.interfaces.persistence.TimeDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.models.Entities.Event;
import ar.edu.itba.paw.models.Entities.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EventServiceImpl implements EventService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventServiceImpl.class);

    private final EventDao eventDao;
    private final TimeDao timeDao;
    private final EmailService emailService;

    @Autowired
    public EventServiceImpl(EventDao eventDao, TimeDao timeDao, EmailService emailService) {
        this.eventDao = eventDao;
        this.timeDao = timeDao;
        this.emailService = emailService;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Event createEvent(long neighborhoodId, String description, Date date, String startTime, String endTime, String name) {
        LOGGER.info("Creating Event {} for Neighborhood {}", name, neighborhoodId);

        java.sql.Time sqlStartTime = java.sql.Time.valueOf(startTime);
        java.sql.Time sqlEndTime = java.sql.Time.valueOf(endTime);
        Time startTimeEntity = timeDao.findTime(sqlStartTime).orElseGet(() -> timeDao.createTime(sqlStartTime));
        Time endTimeEntity = timeDao.findTime(sqlEndTime).orElseGet(() -> timeDao.createTime(sqlEndTime));

        Event createdEvent = eventDao.createEvent(neighborhoodId, name, description, date, startTimeEntity.getTimeId(), endTimeEntity.getTimeId());

        emailService.sendBatchEventMail(createdEvent, "event.custom.message2", neighborhoodId);

        return createdEvent;
    }


    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Event> findEvent(long neighborhoodId, long eventId) {
        LOGGER.info("Finding Event {} from Neighborhood {}", eventId, neighborhoodId);

        return eventDao.findEvent(neighborhoodId, eventId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getEvents(long neighborhoodId, Date date, int page, int size) {
        LOGGER.info("Getting Events for Neighborhood {} on Date {}", neighborhoodId, date);

        return eventDao.getEvents(neighborhoodId, date, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int calculateEventPages(long neighborhoodId, Date date, int size) {
        LOGGER.info("Calculating Event Pages for Neighborhood {}", neighborhoodId);

        return PaginationUtils.calculatePages(eventDao.countEvents(neighborhoodId, date), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Event updateEventPartially(long eventId, String name, String description, Date date, String startTime, String endTime) {
        LOGGER.info("Updating Event {}", eventId);

        Event event = eventDao.findEvent(eventId).orElseThrow(NotFoundException::new);

        if (name != null && !name.isEmpty())
            event.setName(name);
        if (description != null && !description.isEmpty())
            event.setDescription(description);
        if (date != null)
            event.setDate(date);
        if (startTime != null && !startTime.isEmpty()) {
            java.sql.Time sqlStartTime = java.sql.Time.valueOf(startTime);
            event.setStartTime(timeDao.findTime(sqlStartTime).orElseGet(() -> timeDao.createTime(sqlStartTime)));
        }
        if (endTime != null && !endTime.isEmpty()) {
            java.sql.Time sqlEndTime = java.sql.Time.valueOf(endTime);
            event.setEndTime(timeDao.findTime(sqlEndTime).orElseGet(() -> timeDao.createTime(sqlEndTime)));
        }

        return event;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteEvent(long neighborhoodId, long eventId) {
        LOGGER.info("Delete Event {}", eventId);

        return eventDao.deleteEvent(neighborhoodId, eventId);
    }
}
