package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Event;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventDao {

    // ---------------------------------------------- EVENTS INSERT ----------------------------------------------------

    Event createEvent(String name, String description, Date date, long startTimeId, long endTimeId, long neighborhoodId);

    // ---------------------------------------------- EVENTS SELECT ----------------------------------------------------

    Optional<Event> findEvent(long eventId);

    Optional<Event> findEvent(long eventId, long neighborhoodId);

    List<Event> getEvents(Date date, long neighborhoodId, int page, int size);

    int countEvents(Date date, long neighborhoodId);

    // ---------------------------------------------- EVENTS DELETE ----------------------------------------------------

    boolean deleteEvent(long neighborhoodId, long eventId);
}
