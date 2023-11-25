package ar.edu.itba.paw.models.MainEntities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "days")
public class Day {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "days_dayid_seq")
    @SequenceGenerator(sequenceName = "days_dayid_seq", name = "days_dayid_seq", allocationSize = 1)
    private Long dayId;

    @Column(name = "dayname", length = 20, unique = true, nullable = false)
    private String dayName;

    @OneToMany(mappedBy = "day", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private final Set<Shift> shifts = new HashSet<>();

    Day() {
    }

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

    public Set<Shift> getShifts() {
        return shifts;
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
}
