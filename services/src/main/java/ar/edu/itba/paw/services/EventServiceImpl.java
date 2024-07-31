package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.exceptions.UnexpectedException;
import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.interfaces.persistence.TimeDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

@Service
@Transactional
public class EventServiceImpl implements EventService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventServiceImpl.class);

    private final EventDao eventDao;
    private final TimeDao timeDao;
    private final NeighborhoodDao neighborhoodDao;
    private final EmailService emailService;

    private final UserService userService;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    public EventServiceImpl(final EventDao eventDao, final TimeDao timeDao, final EmailService emailService,
                            final UserService userService, NeighborhoodDao neighborhoodDao) {
        this.eventDao = eventDao;
        this.timeDao = timeDao;
        this.emailService = emailService;
        this.userService = userService;
        this.neighborhoodDao = neighborhoodDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Event createEvent(String name, String description, String date, String startTime, String endTime, long neighborhoodId) {
        LOGGER.info("Creating Event {} for Neighborhood {}", name, neighborhoodId);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(true);
        Date parsedDate;
        try {
            parsedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            LOGGER.error("Error whilst creating the Event");
            throw new UnexpectedException("Unexpected error while creating the Event");
        }
        java.sql.Date parsedSqlDate = new java.sql.Date(parsedDate.getTime());
        Long[] times = stringToTime(startTime, endTime);
        Event createdEvent = eventDao.createEvent(name, description, parsedSqlDate, times[0], times[1], neighborhoodId);
        emailService.sendEventMail(createdEvent, "event.custom.message2", userService.getNeighbors(neighborhoodId));
        return createdEvent;
    }


    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Event> findEvent(long eventId, long neighborhoodId) {
        LOGGER.info("Finding Event {} from Neighborhood {}", eventId, neighborhoodId);

        ValidationUtils.checkEventId(eventId);
        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        return eventDao.findEvent(eventId, neighborhoodId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getEvents(String date, long neighborhoodId, int page, int size) {
        LOGGER.info("Getting Events for Neighborhood {} on Date {}", neighborhoodId, date);

        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkOptionalDateString(date);
        ValidationUtils.checkPageAndSize(page, size);

        neighborhoodDao.findNeighborhood(neighborhoodId).orElseThrow(NotFoundException::new);

        return eventDao.getEvents(date, neighborhoodId, page, size);
    }

    @Override
    public int calculateEventPages(String date, long neighborhoodId, int size) {
        LOGGER.info("Calculating Event Pages for Neighborhood {}", neighborhoodId);

        ValidationUtils.checkNeighborhoodId(neighborhoodId);
        ValidationUtils.checkSize(size);

        return PaginationUtils.calculatePages(eventDao.countEvents(date, neighborhoodId), size);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Event updateEventPartially(long eventId, String name, String description, String date, String startTime, String endTime) {
        LOGGER.info("Updating Event {}", eventId);

        ValidationUtils.checkEventId(eventId);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(true);
        Date parsedDate;
        try {
            parsedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            LOGGER.error("Error whilst creating the Event");
            throw new UnexpectedException("Unexpected error while creating the Event");
        }
        java.sql.Date parsedSqlDate = new java.sql.Date(parsedDate.getTime());

        Event event = eventDao.findEvent(eventId).orElseThrow(() -> new NotFoundException("Event Not Found"));
        if (name != null && !name.isEmpty())
            event.setName(name);
        if (description != null && !description.isEmpty())
            event.setDescription(description);
        if (date != null)
            event.setDate(parsedSqlDate);
        if (startTime != null && endTime != null) {
            Long[] times = stringToTime(startTime, endTime);
            event.setStartTime(em.find(ar.edu.itba.paw.models.Entities.Time.class, times[0]));
            event.setEndTime(em.find(ar.edu.itba.paw.models.Entities.Time.class, times[1]));
        }
        return event;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteEvent(long eventId) {
        LOGGER.info("Delete Event {}", eventId);

        ValidationUtils.checkEventId(eventId);

        return eventDao.deleteEvent(eventId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    private Long[] stringToTime(String startTime, String endTime) {
        Time startTimeInTime = null;
        Time endTimeInTime = null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            java.util.Date parsedStartTime = sdf.parse(startTime);
            java.util.Date parsedEndTime = sdf.parse(endTime);
            startTimeInTime = new Time(parsedStartTime.getTime());
            endTimeInTime = new Time(parsedEndTime.getTime());
        } catch (ParseException e) {
            LOGGER.error("Error whilst creating the Event");
            throw new UnexpectedException("Unexpected error while creating the Event");
        }
        OptionalLong startTimeId = getTimeId(startTimeInTime);
        OptionalLong endTimeId = getTimeId(endTimeInTime);

        Long[] timeArray = new Long[2];
        if (startTimeId.isPresent() && endTimeId.isPresent()) {
            timeArray[0] = startTimeId.getAsLong();
            timeArray[1] = endTimeId.getAsLong();
        } else {
            // Handle the case where one or both Time objects were not found
            if (!startTimeId.isPresent()) {
                startTimeId = OptionalLong.of(timeDao.createTime(startTimeInTime).getTimeId());
            }
            if (!endTimeId.isPresent()) {
                endTimeId = OptionalLong.of(timeDao.createTime(endTimeInTime).getTimeId());
            }
            timeArray[0] = startTimeId.getAsLong();
            timeArray[1] = endTimeId.getAsLong();
        }
        return timeArray;
    }

    // -----------------------------------------------------------------------------------------------------------------

    private OptionalLong getTimeId(Time time) {
        return timeDao.findId(time);
    }
}
