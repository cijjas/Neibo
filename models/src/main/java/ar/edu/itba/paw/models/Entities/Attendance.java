package ar.edu.itba.paw.models.Entities;

import ar.edu.itba.paw.models.compositeKeys.AttendanceKey;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

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

    Attendance() {
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



    public void setId(AttendanceKey id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Attendance)) return false;
        Attendance that = (Attendance) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
