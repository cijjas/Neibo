package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Event;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventDao {

    // ---------------------------------------------- EVENTS INSERT ----------------------------------------------------

    Event createEvent(String name, String description, Date date, long startTimeId, long endTimeId, long neighborhoodId);

    // ---------------------------------------------- EVENTS SELECT ----------------------------------------------------

    Optional<Event> findEvent(long eventId);

    Optional<Event> findEvent(long eventId, long neighborhoodId);

    List<Event> getEvents(String date, long neighborhoodId);

    List<Event> getEvents(long neighborhoodId);

    List<Event> getEvents(long neighborhoodId, Date startDate, Date endDate);

    List<Date> getEventDates(long neighborhoodId);

    boolean isUserSubscribedToEvent(long userId, long eventId);

    // ---------------------------------------------- EVENTS DELETE ----------------------------------------------------

    boolean deleteEvent(long eventId);
}
