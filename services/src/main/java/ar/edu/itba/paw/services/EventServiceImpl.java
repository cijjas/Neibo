package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.interfaces.persistence.TimeDao;
import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.models.Event;

import java.sql.Time;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import ar.edu.itba.paw.models.User;
import enums.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {
    private final EventDao eventDao;

    private final TimeDao timeDao;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    private static final Logger LOGGER = LoggerFactory.getLogger(EventServiceImpl.class);

    @Autowired
    public EventServiceImpl(final EventDao eventDao, final TimeDao timeDao) {
        this.eventDao = eventDao;
        this.timeDao = timeDao;
    }


    @Override
    public Optional<Event> findEventById(long eventId) {
        LOGGER.info("Finding Event {}", eventId);
        return eventDao.findEventById(eventId); }

    @Override
    public Event createEvent(String name, String description, Date date, Time startTime, Time endTime, long neighborhoodId) {
        LOGGER.info("Creating Event {} for Neighborhood {}", name, neighborhoodId);
        return eventDao.createEvent(name, description, date, startTime, endTime, neighborhoodId); }

    @Override
    public List<Event> getEventsByDate(Date date, long neighborhoodId) {
        LOGGER.info("Getting Events for Neighborhood {} on Date {}", neighborhoodId, date);
        return eventDao.getEventsByDate(date, neighborhoodId); }

    @Override
    public List<Event> getEventsByNeighborhoodId(long neighborhoodId) {
        LOGGER.info("Getting Events for Neighborhood {}", neighborhoodId);
        return eventDao.getEventsByNeighborhoodId(neighborhoodId); }

    @Override
    public boolean hasEvents(Date date, long neighborhoodId) {
        LOGGER.info("Checking if Neighborhood {} has Events on {}", neighborhoodId, date);
        return !eventDao.getEventsByDate(date, neighborhoodId).isEmpty(); }

    @Override
    public List<Date> getEventDates(long neighborhoodId) {
        LOGGER.info("Getting Event Dates for Neighborhood {}", neighborhoodId);
        return eventDao.getEventDates(neighborhoodId); }

    @Override
    public List<Long> getEventTimestamps(long neighborhoodId) {
        LOGGER.info("Getting Event Timestamps for Neighborhood {}", neighborhoodId);
        List<Date> eventDates = getEventDates(neighborhoodId);

        return eventDates.stream()
                .map(Date::getTime)
                .collect(Collectors.toList());
    }

    @Override
    public String getEventTimestampsString(long neighborhoodId) {
        LOGGER.info("Getting Event Timestamps as String for Neighborhood {}", neighborhoodId);
        return getEventTimestamps(neighborhoodId).stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    @Override
    public boolean deleteEvent(long eventId) {
        LOGGER.info("Delete Event {}", eventId);
        return eventDao.deleteEvent(eventId); }

    @Override
    public String getSelectedMonth(int month, Language language) {
        LOGGER.info("Getting Selected Month {}", month);
        // Define arrays for month names in English and Spanish
        String[] monthsEnglish = {
                "January", "February", "March", "April",
                "May", "June", "July", "August",
                "September", "October", "November", "December"
        };

        String[] monthsSpanish = {
                "enero", "febrero", "marzo", "abril",
                "mayo", "junio", "julio", "agosto",
                "septiembre", "octubre", "noviembre", "diciembre"
        };

        return language == Language.ENGLISH ? monthsEnglish[month] : monthsSpanish[month];
    }

    @Override
    public int getSelectedYear(int year) {
        LOGGER.info("Getting selected Years {}", year);
        return year + 1900;
    }

    @Override
    public Optional<String> getStartTime(long eventId) {
        LOGGER.info("Getting Start Time for Event {}", eventId);
        Optional<Long> startTimeIdOptional = eventDao.findStartTimeIdByEventId(eventId);
        if (startTimeIdOptional.isPresent()) {
            long startTimeId = startTimeIdOptional.get();
            Time startTime = timeDao.findTimeById(startTimeId).get().getTimeInterval();
            return Optional.of(formatter.format(startTime.toLocalTime()));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getEndTime(long eventId) {
        LOGGER.info("Getting End Time for Event {}", eventId);
        Optional<Long> endTimeIdOptional = eventDao.findEndTimeIdByEventId(eventId);
        if (endTimeIdOptional.isPresent()) {
            long endTimeId = endTimeIdOptional.get();
            Time endTime = timeDao.findTimeById(endTimeId).get().getTimeInterval();
            return Optional.of(formatter.format(endTime.toLocalTime()));
        } else {
            return Optional.empty();
        }
    }

}
