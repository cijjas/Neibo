package ar.edu.itba.paw.models;

import java.util.Date;
public class Event {
    private final long eventId;
    private final String name;
    private final String description;
    private final Date date;
    private final long duration;
    private final long neighborhoodId;

    private Event(Builder builder) {
        this.eventId = builder.eventId;
        this.name = builder.name;
        this.description = builder.description;
        this.date = builder.date;
        this.duration = builder.duration;
        this.neighborhoodId = builder.neighborhoodId;
    }

    public static class Builder {
        private long eventId;
        private String name;
        private String description;
        private Date date;
        private long duration;
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

        public Builder duration(long duration) {
            this.duration = duration;
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

    public long getDuration() {
        return duration;
    }

    public long getNeighborhoodId() {
        return neighborhoodId;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", duration=" + duration +
                ", neighborhoodId=" + neighborhoodId +
                '}';
    }

}
