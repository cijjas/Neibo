package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.models.Event;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {

    private final EventDao eventDao;

    @Autowired
    public EventServiceImpl(final EventDao eventDao) {
        this.eventDao = eventDao;
    }

    @Override
    public List<Event> getEvents() { return eventDao.getEvents(); }

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

}
