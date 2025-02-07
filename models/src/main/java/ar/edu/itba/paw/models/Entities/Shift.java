package ar.edu.itba.paw.models.Entities;

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
    private Boolean taken;

    Shift() {
    }

    private Shift(Builder builder) {
        this.shiftId = builder.shiftId;
        this.day = builder.day;
        this.startTime = builder.startTime;
        this.taken = builder.taken;
    }

    public Boolean getTaken() {
        return taken;
    }

    public void setTaken(Boolean taken) {
        this.taken = taken;
    }

    public Long getShiftId() {
        return shiftId;
    }

    public void setShiftId(Long shiftId) {
        this.shiftId = shiftId;
    }

    public List<Amenity> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<Amenity> amenities) {
        this.amenities = amenities;
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
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
