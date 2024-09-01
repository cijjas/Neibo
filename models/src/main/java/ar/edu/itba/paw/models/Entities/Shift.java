package ar.edu.itba.paw.models.Entities;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "shifts")
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shifts_shiftid_seq")
    @SequenceGenerator(sequenceName = "shifts_shiftid_seq", name = "shifts_shiftid_seq", allocationSize = 1)
    private Long shiftId;

    @ManyToMany(mappedBy = "availableShifts", cascade = CascadeType.ALL)
    private List<Amenity> amenities;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "dayid")
    private Day day;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "starttime")
    private Time startTime;

    @Transient
    private java.sql.Time endTime;

    @Transient
    private Boolean taken;

    Shift() {
    }

    private Shift(Builder builder) {
        this.shiftId = builder.shiftId;
        this.day = builder.day;
        this.startTime = builder.startTime;
        this.taken = builder.taken;
        if (builder.startTime != null) {
            this.endTime = calculateEndTime(builder.startTime);
        }
    }

    private java.sql.Time calculateEndTime(Time startTime) {
        // Calculate endTime by adding 1 hour to the startTime
        long startTimeMillis = startTime.getTimeInterval().getTime();
        long endTimeMillis = startTimeMillis + 60 * 60 * 1000; // 60 minutes * 60 seconds * 1000 milliseconds
        return new java.sql.Time(endTimeMillis);
    }

    public Boolean getTaken() {
        return taken;
    }

    public void setTaken(Boolean taken) {
        this.taken = taken;
    }

    public void setShiftId(Long shiftId) {
        this.shiftId = shiftId;
    }

    public void setAmenities(List<Amenity> amenities) {
        this.amenities = amenities;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(java.sql.Time endTime) {
        this.endTime = endTime;
    }

    public Long getShiftId() {
        return shiftId;
    }

    public List<Amenity> getAmenities() {
        return amenities;
    }

    public Day getDay() {
        return day;
    }

    public Time getStartTime() {
        return startTime;
    }

    public java.sql.Time getEndTime() {
        if (endTime == null && startTime != null) {
            endTime = calculateEndTime(startTime);
        }
        return endTime;
    }

    @Override
    public String toString() {
        return "Shift{" +
                "shiftId=" + shiftId +
                ", day=" + day +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", taken=" + taken +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Shift)) return false;
        Shift shift = (Shift) o;
        return Objects.equals(shiftId, shift.shiftId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shiftId);
    }

    public static class Builder {
        private Long shiftId;
        private Day day;
        private Time startTime;
        private Boolean taken;

        public Builder shiftId(Long shiftId) {
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

        public Builder taken(Boolean taken) {
            this.taken = taken;
            return this;
        }

        public Shift build() {
            return new Shift(this);
        }
    }
}
