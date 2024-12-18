package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Event;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventDao {

    // ---------------------------------------------- EVENTS INSERT ----------------------------------------------------

    Event createEvent(long neighborhoodId, String name, String description, Date date, long startTimeId, long endTimeId);

    // ---------------------------------------------- EVENTS SELECT ----------------------------------------------------

    Optional<Event> findEvent(long eventId);

    Optional<Event> findEvent(long neighborhoodId, long eventId);

    List<Event> getEvents(long neighborhoodId, Date date, int page, int size);

    int countEvents(long neighborhoodId, Date date);

    // ---------------------------------------------- EVENTS DELETE ----------------------------------------------------

    boolean deleteEvent(long neighborhoodId, long eventId);
}
