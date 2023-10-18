package ar.edu.itba.paw.models;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.sql.Time;

public class Event {
    private final long eventId;
    private final String name;
    private final String description;
    private final Date date;
    private final Time startTime;
    private final Time endTime;
    private final long neighborhoodId;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    private Event(Builder builder) {
        this.eventId = builder.eventId;
        this.name = builder.name;
        this.description = builder.description;
        this.date = builder.date;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.neighborhoodId = builder.neighborhoodId;
    }

    public static class Builder {
        private long eventId;
        private String name;
        private String description;
        private Date date;
        private Time startTime;
        private Time endTime;
        private long neighborhoodId;

        public Builder eventId(long eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder date(Date date) {
            this.date = date;
            return this;
        }

        public Builder startTime(Time startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder endTime(Time endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder neighborhoodId(long neighborhoodId) {
            this.neighborhoodId = neighborhoodId;
            return this;
        }

        public Event build() {
            return new Event(this);
        }
    }

    public long getEventId() {
        return eventId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public Time getStartTime() { return startTime; }

    public Time getEndTime() { return endTime; }

    public long getNeighborhoodId() {
        return neighborhoodId;
    }

    public String getStartTimeString() {
        if(startTime == null) {
            return "";
        }   else {
            return startTime.toLocalTime().format(formatter);
        }
    }

    public String getEndTimeString() {
        if(endTime == null) {
            return "";
        }   else {
            return endTime.toLocalTime().format(formatter);
        }
    }

    public String getDuration() {
        if (startTime != null && endTime != null) {
            LocalTime start = startTime.toLocalTime();
            LocalTime end = endTime.toLocalTime();
            long minutes = ChronoUnit.MINUTES.between(start, end);
            return String.valueOf(minutes);
        }
        return "N/A";
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", neighborhoodId=" + neighborhoodId +
                '}';
    }
}
