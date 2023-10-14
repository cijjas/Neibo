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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private final EventDao eventDao;

    private final TimeDao timeDao;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    @Autowired
    public EventServiceImpl(final EventDao eventDao, final TimeDao timeDao) {
        this.eventDao = eventDao;
        this.timeDao = timeDao;
    }


    @Override
    public Optional<Event> findEventById(long eventId) { return eventDao.findEventById(eventId); }

    @Override
    public Event createEvent(String name, String description, Date date, Time startTime, Time endTime, long neighborhoodId) { return eventDao.createEvent(name, description, date, startTime, endTime, neighborhoodId); }

    @Override
    public List<Event> getEventsByDate(Date date, long neighborhoodId) { return eventDao.getEventsByDate(date, neighborhoodId); }

    @Override
    public List<Event> getEventsByNeighborhoodId(long neighborhoodId) { return eventDao.getEventsByNeighborhoodId(neighborhoodId); }

    @Override
    public boolean hasEvents(Date date, long neighborhoodId) { return !eventDao.getEventsByDate(date, neighborhoodId).isEmpty(); }

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

    @Override
    public Optional<String> getStartTime(long eventId) {
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
