package ar.edu.itba.paw.models;

import java.util.Objects;

public class Day {
    private Long dayId;
    private String dayName;

    private Day(Builder builder) {
        this.dayId = builder.dayId;
        this.dayName = builder.dayName;
    }

    // Getter methods

    public Long getDayId() {
        return dayId;
    }

    public String getDayName() {
        return dayName;
    }

    @Override
    public String toString() {
        return "Day{" +
                "dayId=" + dayId +
                ", dayName='" + dayName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Day day = (Day) o;
        return Objects.equals(dayId, day.dayId) && Objects.equals(dayName, day.dayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayId, dayName);
    }

    public static class Builder {
        private Long dayId;
        private String dayName;

        public Builder dayId(Long dayId) {
            this.dayId = dayId;
            return this;
        }

        public Builder dayName(String dayName) {
            this.dayName = dayName;
            return this;
        }

        public Day build() {
            return new Day(this);
        }
    }
}
