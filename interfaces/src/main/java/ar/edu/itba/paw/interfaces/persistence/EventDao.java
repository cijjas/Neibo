package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Event;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventDao {

    // ---------------------------------------------- EVENTS INSERT ----------------------------------------------------

    Event createEvent(String name, String description, Date date, long startTimeId, long endTimeId, long neighborhoodId);

    // ---------------------------------------------- EVENTS SELECT ----------------------------------------------------

    Optional<Event> findEventById(long eventId);

    List<Event> getEventsByDate(Date date, long neighborhoodId);

    List<Event> getEventsByNeighborhoodId(long neighborhoodId);

    List<Event> getEventsByNeighborhoodIdAndDateRange(long neighborhoodId, Date startDate, Date endDate);

    List<Date> getEventDates(long neighborhoodId);

    boolean isUserSubscribedToEvent(long userId, long eventId);

        // ---------------------------------------------- EVENTS DELETE ----------------------------------------------------

    boolean deleteEvent(long eventId);
}
