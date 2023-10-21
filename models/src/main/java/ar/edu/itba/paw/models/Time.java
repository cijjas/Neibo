package ar.edu.itba.paw.models;

import java.util.Objects;

public class Time {
    private Long timeId;
    private java.sql.Time timeInterval;

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
}
