package ar.edu.itba.paw.models;

import java.util.Objects;

public class Shift {
    private long shiftId;
    private Day day;
    private Time startTime;
    private boolean taken; // Changed the variable name from 'free' to 'taken'

    private Shift(Builder builder) {
        this.shiftId = builder.shiftId;
        this.day = builder.day;
        this.startTime = builder.startTime;
        this.taken = builder.taken; // Updated the variable assignment
    }

    public static class Builder {
        private long shiftId;
        private Day day;
        private Time startTime;
        private boolean taken; // Changed the variable name from 'free' to 'taken'

        public Builder shiftId(long shiftId) {
            this.shiftId = shiftId;
            return this;
        }

        public Builder day(Day day) {
            this.day = day;
            return this;
        }

        public Builder startTime(Time startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder taken(boolean taken) { // Changed the method name from 'free' to 'taken'
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

    public Day getDay() {
        return day;
    }

    public Time getStartTime() {
        return startTime;
    }

    public boolean isTaken() { // Changed the method name from 'isFree' to 'isTaken'
        return taken;
    }

    @Override
    public String toString() {
        return "Shift{" +
                "shiftId=" + shiftId +
                ", day=" + day +
                ", startTime=" + startTime +
                ", taken=" + taken + // Updated the variable name in the toString method
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
