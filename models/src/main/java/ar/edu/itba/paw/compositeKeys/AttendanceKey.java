package ar.edu.itba.paw.compositeKeys;

import java.io.Serializable;
import java.util.Objects;

public class AttendanceKey implements Serializable {
    private Long eventId;
    private Long userId;

    private AttendanceKey() {
        // Private constructor for the builder
    }

    public Long getEventId() {
        return eventId;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttendanceKey that = (AttendanceKey) o;
        return Objects.equals(eventId, that.eventId) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, userId);
    }

    public static class Builder {
        private final AttendanceKey attendanceKey = new AttendanceKey();

        public Builder eventId(Long eventId) {
            attendanceKey.eventId = eventId;
            return this;
        }

        public Builder userId(Long userId) {
            attendanceKey.userId = userId;
            return this;
        }

        public AttendanceKey build() {
            return attendanceKey;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
