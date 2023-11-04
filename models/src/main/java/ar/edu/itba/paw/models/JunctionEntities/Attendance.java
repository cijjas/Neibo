package ar.edu.itba.paw.models.JunctionEntities;

import ar.edu.itba.paw.models.compositeKeys.AttendanceKey;
import ar.edu.itba.paw.models.MainEntities.Event;
import ar.edu.itba.paw.models.MainEntities.User;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "events_users")
public class Attendance implements Serializable {
    @EmbeddedId
    private AttendanceKey id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "userid")
    private User user;

    @ManyToOne
    @MapsId("eventId")
    @JoinColumn(name = "eventid")
    private Event event;

    public Attendance() {
        this.id = new AttendanceKey();
    }

    public Attendance(User user, Event event) {
        this.id = new AttendanceKey(user.getUserId(), event.getEventId());
        this.user = user;
        this.event = event;
    }

    public AttendanceKey getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Event getEvent() {
        return event;
    }

    public void setId(AttendanceKey id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
