package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.models.Event;
import java.util.Date;

import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private final EventDao eventDao;

    @Autowired
    public EventServiceImpl(final EventDao eventDao) {
        this.eventDao = eventDao;
    }

    @Override
    public List<Event> getEvents() { return null; }

    @Override
    public Optional<Event> findEventById(long eventId) { return eventDao.findEventById(eventId); }

    @Override
    public Event createEvent(String name, String description, Date date, long duration, long neighborhoodId) { return eventDao.createEvent(name, description, date, duration, neighborhoodId); }

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

}
