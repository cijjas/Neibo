package ar.edu.itba.paw.models.JunctionEntities;

import ar.edu.itba.paw.compositeKeys.AttendanceKey;
import ar.edu.itba.paw.models.MainEntities.Event;
import ar.edu.itba.paw.models.MainEntities.User;

import javax.persistence.*;
import java.io.Serializable;

// @Entity
@Table(name = "events_users")
@IdClass(AttendanceKey.class)
public class Attendance implements Serializable {
    @Id
    @ManyToOne
    @JoinColumn(name = "userid")
    private Long userId;

    @Id
    @ManyToOne
    @JoinColumn(name = "eventid")
    private Long eventId;

    // Other fields, if any

    public Attendance() {}

    public Attendance(Long userId, Long eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    // Other getters and setters for additional fields

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long userId;
        private Long eventId;

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder eventId(Long eventId) {
            this.eventId = eventId;
            return this;
        }

        public Attendance build() {
            return new Attendance(userId, eventId);
        }
    }
}
