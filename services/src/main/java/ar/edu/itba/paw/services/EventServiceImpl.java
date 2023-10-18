package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.interfaces.persistence.TimeDao;
import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.models.Event;

import java.sql.Time;
import java.util.Date;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.Month;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventServiceImpl implements EventService {
    private final EventDao eventDao;

    private final TimeDao timeDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(EventServiceImpl.class);

    @Autowired
    public EventServiceImpl(final EventDao eventDao, final TimeDao timeDao) {
        this.eventDao = eventDao;
        this.timeDao = timeDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Event createEvent(String name, String description, Date date, Time startTime, Time endTime, long neighborhoodId) {
        LOGGER.info("Creating Event {} for Neighborhood {}", name, neighborhoodId);
        // find, if not exist -> create
        long startTimeId = timeDao.createTime(startTime).getTimeId();
        long endTimeId = timeDao.createTime(endTime).getTimeId();
        return eventDao.createEvent(name, description, date, startTimeId, endTimeId, neighborhoodId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Event> findEventById(long eventId) {
        LOGGER.info("Finding Event {}", eventId);
        return eventDao.findEventById(eventId); }

    @Override
    @Transactional(readOnly = true)
    public boolean hasEvents(Date date, long neighborhoodId) {
        LOGGER.info("Checking if Neighborhood {} has Events on {}", neighborhoodId, date);
        return !eventDao.getEventsByDate(date, neighborhoodId).isEmpty(); }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getEventsByDate(Date date, long neighborhoodId) {
        LOGGER.info("Getting Events for Neighborhood {} on Date {}", neighborhoodId, date);
        return eventDao.getEventsByDate(date, neighborhoodId); }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getEventsByNeighborhoodId(long neighborhoodId) {
        LOGGER.info("Getting Events for Neighborhood {}", neighborhoodId);
        return eventDao.getEventsByNeighborhoodId(neighborhoodId); }

    @Override
    @Transactional(readOnly = true)
    public List<Date> getEventDates(long neighborhoodId) {
        LOGGER.info("Getting Event Dates for Neighborhood {}", neighborhoodId);
        return eventDao.getEventDates(neighborhoodId); }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getEventTimestamps(long neighborhoodId) {
        LOGGER.info("Getting Event Timestamps for Neighborhood {}", neighborhoodId);
        List<Date> eventDates = getEventDates(neighborhoodId);

        return eventDates.stream()
                .map(Date::getTime)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public String getEventTimestampsString(long neighborhoodId) {
        LOGGER.info("Getting Event Timestamps as String for Neighborhood {}", neighborhoodId);
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

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteEvent(long eventId) {
        LOGGER.info("Delete Event {}", eventId);
        return eventDao.deleteEvent(eventId); }

}
