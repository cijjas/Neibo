package ar.edu.itba.paw.services;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.Month;
import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.exceptions.UnexpectedException;
import ar.edu.itba.paw.interfaces.persistence.EventDao;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventServiceImpl implements EventService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventServiceImpl.class);
    private final EventDao eventDao;
    private final TimeDao timeDao;
    private final EmailService emailService;

    private final UserService userService;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    public EventServiceImpl(final EventDao eventDao, final TimeDao timeDao, final EmailService emailService, UserService userService) {
        this.eventDao = eventDao;
        this.timeDao = timeDao;
        this.emailService = emailService;
        this.userService = userService;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Event createEvent(String name, String description, Date date, String startTime, String endTime, long neighborhoodId) {
        LOGGER.info("Creating Event {} for Neighborhood {}", name, neighborhoodId);

        Long[] times = stringToTime(startTime, endTime);
        Event createdEvent = eventDao.createEvent(name, description, date, times[0], times[1], neighborhoodId);
        emailService.sendEventMail(createdEvent, "event.custom.message2", userService.getNeighbors(neighborhoodId));
        return createdEvent;
    }

    // -----------------------------------------------------------------------------------------------------------------

    private Event getEvent(long eventId){

        ValidationUtils.checkEventId(eventId);

        return eventDao.findEventById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
    }
    @Override
    public Event updateEventPartially(long eventId, String name, String description, Date date, String startTime, String endTime){

        ValidationUtils.checkEventId(eventId);

        Event event = getEvent(eventId);
        if(name != null && !name.isEmpty())
            event.setName(name);
        if(description != null && !description.isEmpty())
            event.setDescription(description);
        if(date != null)
            event.setDate(date);
        if(startTime != null && endTime != null){
            Long[] times = stringToTime(startTime, endTime);
            event.setStartTime(em.find(ar.edu.itba.paw.models.Entities.Time.class, times[0]));
            event.setEndTime(em.find(ar.edu.itba.paw.models.Entities.Time.class, times[1]));
        }
        return event;
    }

    private OptionalLong getTimeId(Time time) {
        return timeDao.findIdByTime(time);
    }


    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Event> findEventById(long eventId) {
        LOGGER.info("Finding Event {}", eventId);

        ValidationUtils.checkEventId(eventId);

        return eventDao.findEventById(eventId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getEventsByDate(String date, long neighborhoodId) {
        LOGGER.info("Getting Events for Neighborhood {} on Date {}", neighborhoodId, date);

        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return eventDao.getEventsByDate(dateFormat.parse(date), neighborhoodId);
        } catch (ParseException e) {
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getEventsByNeighborhoodId(long neighborhoodId) {
        LOGGER.info("Getting Events for Neighborhood {}", neighborhoodId);

        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        return eventDao.getEventsByNeighborhoodId(neighborhoodId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Date> getEventDates(long neighborhoodId) {
        LOGGER.info("Getting Event Dates for Neighborhood {}", neighborhoodId);

        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        return eventDao.getEventDates(neighborhoodId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getEventTimestamps(long neighborhoodId) {
        LOGGER.info("Getting Event Timestamps for Neighborhood {}", neighborhoodId);

        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        List<Date> eventDates = getEventDates(neighborhoodId);

        return eventDates.stream()
                .map(Date::getTime)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public String getEventTimestampsString(long neighborhoodId) {
        LOGGER.info("Getting Event Timestamps as String for Neighborhood {}", neighborhoodId);

        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        return getEventTimestamps(neighborhoodId).stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    @Override
    @Transactional(readOnly = true)
    public String getSelectedMonth(int month, Language language) {
        LOGGER.info("Getting Selected Month {}", month);

        if (month < 0 || month >= Month.values().length) {
            throw new IllegalArgumentException("Invalid month index");
        }

        return Month.values()[month].getName(language);
    }

    @Override
    @Transactional(readOnly = true)
    public int getSelectedYear(int year) {
        LOGGER.info("Getting selected Years {}", year);
        return year + 1900;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasEvents(Date date, long neighborhoodId) {
        LOGGER.info("Checking if Neighborhood {} has Events on {}", neighborhoodId, date);

        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        return !eventDao.getEventsByDate(date, neighborhoodId).isEmpty();
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Event updateEvent(long eventId, String name, String description, Date date, String startTime, String endTime) {

        Long[] times = stringToTime(startTime, endTime);
        Event event = em.find(Event.class, eventId);
        event.setName(name);
        event.setDescription(description);
        event.setDate(date);
        event.setStartTime(em.find(ar.edu.itba.paw.models.Entities.Time.class, times[0]));
        event.setEndTime(em.find(ar.edu.itba.paw.models.Entities.Time.class, times[1]));
        emailService.sendEventMail(event, "event.custom.message1", userService.getNeighbors(event.getNeighborhood().getNeighborhoodId()));
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
}
