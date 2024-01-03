package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.models.Entities.Event;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventService {

    Event createEvent(String name, String description, Date date, String startTime, String endTime, long neighborhoodId);

    Event updateEvent(long eventId, String name, String description, Date date, String startTime, String endTime);

    Event updateEventPartially(long eventId, String name, String description, Date date, String startTime, String endTime);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Event> findEventById(long eventId);

    boolean hasEvents(Date date, long neighborhoodId);

    List<Event> getEventsByDate(Date date, long neighborhoodId);

    List<Event> getEventsByDate(String dateString, long neighborhoodId);

    List<Event> getEventsByNeighborhoodId(long neighborhoodId);

    List<Date> getEventDates(long neighborhoodId);

    List<Long> getEventTimestamps(long neighborhoodId);

    String getEventTimestampsString(long neighborhoodId);

    String getSelectedMonth(int month, Language language);

    int getSelectedYear(int year);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteEvent(long eventId);

}
