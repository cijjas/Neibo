package ar.edu.itba.paw.models.JunctionEntities;

import ar.edu.itba.paw.models.MainEntities.Amenity;
import ar.edu.itba.paw.models.MainEntities.Shift;
import ar.edu.itba.paw.models.MainEntities.User;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "amenities_shifts_availability")
public class Availability {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "amenities_shifts_availability_amenityavailabilityid_seq")
    @SequenceGenerator(sequenceName = "amenities_shifts_availability_amenityavailabilityid_seq", name = "amenities_shifts_availability_amenityavailabilityid_seq", allocationSize = 1)
    @Column(name = "amenityavailabilityid")
    private Long amenityAvailabilityId;

    @ManyToOne
    @JoinColumn(name = "amenityid")
    private Amenity amenity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shiftid")
    private Shift shift;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "users_availability",
            joinColumns = @JoinColumn(name = "amenityavailabilityid"),
            inverseJoinColumns = @JoinColumn(name = "userid"))
    private Set<User> bookedByUsers;

    public Availability(){}

    private Availability(Builder builder) {
        this.amenityAvailabilityId = builder.amenityAvailabilityId;
        this.amenity = builder.amenity;
        this.shift = builder.shift;
    }

    public Long getAmenityAvailabilityId() {
        return amenityAvailabilityId;
    }

    public Amenity getAmenity() {
        return amenity;
    }

    public Shift getShift() {
        return shift;
    }

    public Set<User> getBookedByUsers() {
        return bookedByUsers;
    }

    @Override
    public String toString() {
        return "Availability{" +
                "amenityAvailabilityId=" + amenityAvailabilityId +
                ", amenity=" + amenity +
                ", shift=" + shift +
                '}';
    }

    public static class Builder {
        private Long amenityAvailabilityId;
        private Amenity amenity;
        private Shift shift;

        public Builder amenityAvailabilityId(Long amenityAvailabilityId) {
            this.amenityAvailabilityId = amenityAvailabilityId;
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
