package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Event;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventService {

    Event createEvent(long neighborhoodId, String description, Date date, String startTime, String endTime, String name);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Event> findEvent(long neighborhoodId, long eventId);

    List<Event> getEvents(long neighborhoodId, Date date, int page, int size);

    int calculateEventPages(long neighborhoodId, Date date, int size);

    // -----------------------------------------------------------------------------------------------------------------

    Event updateEventPartially(long eventId, String name, String description, Date date, String startTime, String endTime);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteEvent(long neighborhoodId, long eventId);

}
