
package ar.edu.itba.paw.models;

import java.sql.Time;
import java.util.Objects;

public class Shift {
    private long shiftId;
    private String day; // Changed the type from Day to String
    private Time startTime; // Changed the type from Time to java.sql.Time
    private boolean taken;

    private Shift(Builder builder) {
        this.shiftId = builder.shiftId;
        this.day = builder.day;
        this.startTime = builder.startTime;
        this.taken = builder.taken;
    }

    public static class Builder {
        private long shiftId;
        private String day; // Changed the type from Day to String
        private Time startTime; // Changed the type from Time to java.sql.Time
        private boolean taken;

        public Builder shiftId(long shiftId) {
            this.shiftId = shiftId;
            return this;
        }

        public Builder day(String day) { // Changed the type from Day to String
            this.day = day;
            return this;
        }

        public Builder startTime(Time startTime) { // Changed the type from Time to java.sql.Time
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

    public String getDay() { // Changed the type from Day to String
        return day;
    }

    public Time getStartTime() { // Changed the type from Time to java.sql.Time
        return startTime;
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
}