package ar.edu.itba.paw.models.Entities;

import javax.persistence.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "events")
public class Event {

    @Transient
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "events_eventid_seq")
    @SequenceGenerator(sequenceName = "events_eventid_seq", name = "events_eventid_seq", allocationSize = 1)
    @Column(name = "eventid")
    private Long eventId;
    @Column(name = "name", length = 255, nullable = false)
    private String name;
    @Column(name = "description", length = 512, nullable = false)
    private String description;
    @Column(name = "date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date;
    @ManyToOne
    @JoinColumn(name = "neighborhoodid")
    private Neighborhood neighborhood;
    @ManyToOne
    @JoinColumn(name = "starttimeid")
    private Time startTime;
    @ManyToOne
    @JoinColumn(name = "endtimeid")
    private Time endTime;
    @ManyToMany
    @JoinTable(
            name = "events_users",
            joinColumns = @JoinColumn(name = "eventid"),
            inverseJoinColumns = @JoinColumn(name = "userid")
    )
    private Set<User> attendees;


    Event() {
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public DateTimeFormatter getFormatter() {
        return formatter;
    }

    public Set<User> getAttendees() {
        return attendees;
    }

    public void setAttendees(Set<User> attendees) {
        this.attendees = attendees;
    }

    public Neighborhood getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(Neighborhood neighborhood) {
        this.neighborhood = neighborhood;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return Objects.equals(eventId, event.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(eventId);
    }

    public static class Builder {
        private Long eventId;
        private String name;
        private String description;
        private Date date;
        private Time startTime;
        private Time endTime;
        private Neighborhood neighborhood;

        public Builder eventId(Long eventId) {
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

        public Builder neighborhood(Neighborhood neighborhood) {
            this.neighborhood = neighborhood;
            return this;
        }

        public Event build() {
            Event event = new Event();
            event.eventId = this.eventId;
            event.name = this.name;
            event.description = this.description;
            event.date = this.date;
            event.startTime = this.startTime;
            event.endTime = this.endTime;
            event.neighborhood = this.neighborhood;
            return event;
        }
    }
}
