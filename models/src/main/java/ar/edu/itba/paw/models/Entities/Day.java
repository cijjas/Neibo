package ar.edu.itba.paw.models.Entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "days")
public class Day {
    @OneToMany(mappedBy = "day")
    private final Set<Shift> shifts = new HashSet<>();
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "days_dayid_seq")
    @SequenceGenerator(sequenceName = "days_dayid_seq", name = "days_dayid_seq", allocationSize = 1)
    private Long dayId;
    @Column(name = "dayname", length = 20, unique = true, nullable = false)
    private String dayName;

    Day() {
    }

    private Day(Builder builder) {
        this.dayId = builder.dayId;
        this.dayName = builder.dayName;
    }

    public Long getDayId() {
        return dayId;
    }

    public String getDayName() {
        return dayName;
    }

    public Set<Shift> getShifts() {
        return shifts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Day)) return false;
        Day day = (Day) o;
        return Objects.equals(dayId, day.dayId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayId);
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
