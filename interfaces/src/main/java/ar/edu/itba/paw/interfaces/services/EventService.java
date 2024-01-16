package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.models.Entities.Event;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventService {

    Event createEvent(String name, String description, Date date, String startTime, String endTime, long neighborhoodId);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Event> findEvent(long eventId);

    boolean hasEvents(String date, long neighborhoodId);

    List<Event> getEvents(String date, long neighborhoodId);

    List<Event> getEvents(long neighborhoodId);

    List<Date> getEventDates(long neighborhoodId);

    List<Long> getEventTimestamps(long neighborhoodId);

    String getEventTimestampsString(long neighborhoodId);

    String getSelectedMonth(int month, Language language);

    int getSelectedYear(int year);

    // -----------------------------------------------------------------------------------------------------------------

    Event updateEvent(long eventId, String name, String description, Date date, String startTime, String endTime);

    Event updateEventPartially(long eventId, String name, String description, Date date, String startTime, String endTime);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteEvent(long eventId);

}
