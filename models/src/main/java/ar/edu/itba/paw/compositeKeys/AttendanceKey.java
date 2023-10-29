package ar.edu.itba.paw.compositeKeys;

import java.io.Serializable;
import java.util.Objects;

public class AttendanceKey implements Serializable {
    private Long userId;
    private Long eventId;

    public AttendanceKey() {
    }

    public AttendanceKey(Long userId, Long eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttendanceKey that = (AttendanceKey) o;
        return userId.equals(that.userId) && eventId.equals(that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, eventId);
    }
}
