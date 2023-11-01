package ar.edu.itba.paw.models.MainEntities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "times")
public class Time {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "times_timeid_seq")
    @SequenceGenerator(sequenceName = "times_timeid_seq", name = "times_timeid_seq", allocationSize = 1)
    private Long timeId;
    @Column(name = "timeinterval", unique = true, nullable = false)
    private java.sql.Time timeInterval;

    @OneToMany(mappedBy = "startTime", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Shift> shifts = new HashSet<>();

    @OneToMany(mappedBy = "startTime", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Event> eventsStartingAtThisTime = new HashSet<>();

    @OneToMany(mappedBy = "endTime", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Event> eventsEndingAtThisTime = new HashSet<>();


    public Time(){}

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

    @Override
    public String toString() {
        return "Time{" +
                "timeId=" + timeId +
                ", timeInterval=" + timeInterval +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Time time = (Time) o;
        return Objects.equals(timeId, time.timeId) && Objects.equals(timeInterval, time.timeInterval);
    }

    public Time plusHours(int hours) {
        return new Time.Builder()
                .timeId(this.timeId)
                .timeInterval(new java.sql.Time(this.timeInterval.getTime() + hours * 3600000L))
                .build();
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeId, timeInterval);
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

    public Set<Shift> getShifts() {
        return shifts;
    }

    public Set<Event> getEventsStartingAtThisTime() {
        return eventsStartingAtThisTime;
    }

    public Set<Event> getEventsEndingAtThisTime() {
        return eventsEndingAtThisTime;
    }
}
