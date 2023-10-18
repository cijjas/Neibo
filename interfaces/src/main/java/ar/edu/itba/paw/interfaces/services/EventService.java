package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.models.Event;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventService {

    Event createEvent(String name, String description, Date date, Time startTime, Time endTime, long neighborhoodId);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Event> findEventById(long eventId);

    boolean hasEvents(Date date, long neighborhoodId);

    List<Event> getEventsByDate(Date date, long neighborhoodId);

    List<Event> getEventsByNeighborhoodId(long neighborhoodId);

    List<Date> getEventDates(long neighborhoodId);

    List<Long> getEventTimestamps(long neighborhoodId);

    String getEventTimestampsString(long neighborhoodId);

    String getSelectedMonth(int month, Language language);

    int getSelectedYear(int year);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteEvent(long eventId);

}
