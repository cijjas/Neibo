package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Event;
import java.sql.Time;
import enums.Language;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventService {


    Optional<Event> findEventById(long eventId);

    Event createEvent(String name, String description, Date date, Time startTime, Time endTime, long neighborhoodId);

    List<Event> getEventsByDate(Date date, long neighborhoodId);

    List<Event> getEventsByNeighborhoodId(long neighborhoodId);

    boolean hasEvents(Date date, long neighborhoodId);

    List<Date> getEventDates(long neighborhoodId);

    List<Long> getEventTimestamps(long neighborhoodId);

    String getEventTimestampsString(long neighborhoodId);

    boolean deleteEvent(long eventId);

    String getSelectedMonth(int month, Language language);

    int getSelectedYear(int year);

    Optional<String> getStartTime(long eventId);

    Optional<String> getEndTime(long eventId);

}
