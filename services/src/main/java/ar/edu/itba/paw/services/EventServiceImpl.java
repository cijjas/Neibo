package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.models.Event;

import java.sql.Time;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(EventServiceImpl.class);

    @Autowired
    public EventServiceImpl(final EventDao eventDao) {
        this.eventDao = eventDao;
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
    public List<Date> getEventDates(long neighborhoodId) { return eventDao.getEventDates(neighborhoodId); }

    @Override
    public List<Long> getEventTimestamps(long neighborhoodId) {
        List<Date> eventDates = getEventDates(neighborhoodId);

        return eventDates.stream()
                .map(Date::getTime)
                .collect(Collectors.toList());
    }

    @Override
    public String getEventTimestampsString(long neighborhoodId) {
        return getEventTimestamps(neighborhoodId).stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    @Override
    public boolean deleteEvent(long eventId) { return eventDao.deleteEvent(eventId); }

    @Override
    public String getSelectedMonth(int month, Language language) {
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
        return year + 1900;
    }

}
