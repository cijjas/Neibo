package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Event;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventService {

    Event createEvent(String name, String description, Date date, String startTime, String endTime, long neighborhoodId);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Event> findEvent(long eventId, long neighborhoodId);

    List<Event> getEvents(Date date, long neighborhoodId, int page, int size);

    // ---------------------------------------------------

    int calculateEventPages(Date date, long neighborhoodId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    Event updateEventPartially(long eventId, String name, String description, Date date, String startTime, String endTime);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteEvent(long eventId);

}
