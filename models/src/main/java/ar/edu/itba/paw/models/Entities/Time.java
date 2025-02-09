package ar.edu.itba.paw.models.Entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "times")
public class Time {
    @OneToMany(mappedBy = "startTime")
    private final Set<Shift> shifts = new HashSet<>();
    @OneToMany(mappedBy = "startTime")
    private final Set<Event> eventsStartingAtThisTime = new HashSet<>();
    @OneToMany(mappedBy = "endTime")
    private final Set<Event> eventsEndingAtThisTime = new HashSet<>();
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "times_timeid_seq")
    @SequenceGenerator(sequenceName = "times_timeid_seq", name = "times_timeid_seq", allocationSize = 1)
    private Long timeId;
    @Column(name = "timeinterval", unique = true, nullable = false)
    private java.sql.Time timeInterval;


    Time() {
    }

    private Time(Builder builder) {
        this.timeId = builder.timeId;
        this.timeInterval = builder.timeInterval;
    }

    public Long getTimeId() {
        return timeId;
    }

    public java.sql.Time getTimeInterval() {
        return timeInterval;
    }

    public Time plusHours(int hours) {
        return new Time.Builder()
                .timeId(this.timeId)
                .timeInterval(new java.sql.Time(this.timeInterval.getTime() + hours * 3600000L))
                .build();
    }

    public Set<Shift> getShifts() {
        return shifts;
    }

    public Set<Event> getEventsStartingAtThisTime() {
        return eventsStartingAtThisTime;
    }

    public Set<Event> getEventsEndingAtThisTime() {
        return eventsEndingAtThisTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Time)) return false;
        Time time = (Time) o;
        return Objects.equals(timeId, time.timeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeId);
    }

    public static class Builder {
        private Long timeId;
        private java.sql.Time timeInterval;

        public Builder timeId(Long timeId) {
            this.timeId = timeId;
            return this;
        }

        public Builder timeInterval(java.sql.Time timeInterval) {
            this.timeInterval = timeInterval;
            return this;
        }

        public Time build() {
            return new Time(this);
        }
    }
}
