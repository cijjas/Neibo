package ar.edu.itba.paw.models.JunctionEntities;

import ar.edu.itba.paw.models.MainEntities.Amenity;
import ar.edu.itba.paw.models.MainEntities.Shift;

import javax.persistence.*;

@Entity
@Table(name = "amenities_shifts_availability")
public class Availability {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "amenities_shifts_availability_amenityavailabilityid_seq")
    @SequenceGenerator(sequenceName = "amenities_shifts_availability_amenityavailabilityid_seq", name = "amenities_shifts_availability_amenityavailabilityid_seq", allocationSize = 1)
    @Column(name = "amenityavailabilityid")
    private Long availabilityId;

    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "amenityid")
    private Amenity amenity;

    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "shiftid")
    private Shift shift;

    private Availability(Builder builder) {
        this.availabilityId = builder.availabilityId;
        this.amenity = builder.amenity;
        this.shift = builder.shift;
    }

    public Long getAvailabilityId() {
        return availabilityId;
    }

    public Amenity getAmenity() {
        return amenity;
    }

    public Shift getShift() {
        return shift;
    }

    @Override
    public String toString() {
        return "Availability{" +
                "availabilityId=" + availabilityId +
                ", amenity=" + amenity +
                ", shift=" + shift +
                '}';
    }

    public static class Builder {
        private Long availabilityId;
        private Amenity amenity;
        private Shift shift;

        public Builder availabilityId(Long availabilityId) {
            this.availabilityId = availabilityId;
            return this;
        }

        public Builder amenity(Amenity amenity) {
            this.amenity = amenity;
            return this;
        }

        public Builder shift(Shift shift) {
            this.shift = shift;
            return this;
        }

        public Availability build() {
            return new Availability(this);
        }
    }
}
