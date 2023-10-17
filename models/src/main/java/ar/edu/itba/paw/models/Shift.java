package ar.edu.itba.paw.models;

import java.sql.Time;
import java.util.Objects;

public class Shift {
    private long shiftId;
    private String day;
    private Time startTime;
    private Time endTime;
    private boolean taken;

    private Shift(Builder builder) {
        this.shiftId = builder.shiftId;
        this.day = builder.day;
        this.startTime = builder.startTime;
        this.taken = builder.taken;
        if (builder.startTime != null) {
            this.endTime = calculateEndTime(builder.startTime);
        }
    }

    private Time calculateEndTime(Time startTime) {
        // Calculate endTime by adding 1 hour to the startTime
        long startTimeMillis = startTime.getTime();
        long endTimeMillis = startTimeMillis + 60 * 60 * 1000; // 60 minutes * 60 seconds * 1000 milliseconds
        return new Time(endTimeMillis);
    }

    public static class Builder {
        private long shiftId;
        private String day;
        private Time startTime;
        private boolean taken;

        public Builder shiftId(long shiftId) {
            this.shiftId = shiftId;
            return this;
        }

        public Builder day(String day) {
            this.day = day;
            return this;
        }

        public Builder startTime(Time startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder taken(boolean taken) {
            this.taken = taken;
            return this;
        }

        public Shift build() {
            return new Shift(this);
        }
    }

    public long getShiftId() {
        return shiftId;
    }

    public String getDay() {
        return day;
    }

    public Time getStartTime() {
        return startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public boolean isTaken() {
        return taken;
    }

    @Override
    public String toString() {
        return "Shift{" +
                "shiftId=" + shiftId +
                ", day='" + day + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", taken=" + taken +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shift shift = (Shift) o;
        return shiftId == shift.shiftId && taken == shift.taken && Objects.equals(day, shift.day) && Objects.equals(startTime, shift.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shiftId, day, startTime, endTime, taken);
    }
}
