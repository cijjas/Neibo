package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Event;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventDao {

    Event createEvent(String name, String description, Date date, long duration, long neighborhoodId);

    List<Event> getEvents();

    Optional<Event> findEventById(long eventId);

    List<Event> getEventsByDate(Date date, long neighborhoodId);

    List<Event> getEventsByNeighborhoodId(long neighborhoodId);

    List<Date> getEventDates(long neighborhoodId);

    boolean deleteEvent(long eventId);

}
