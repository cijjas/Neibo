package ar.edu.itba.paw.models.Entities;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
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

    @OneToMany(mappedBy = "amenityAvailability", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    Availability() {
    }

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

    public void setAmenityAvailabilityId(Long amenityAvailabilityId) {
        this.amenityAvailabilityId = amenityAvailabilityId;
    }

    public void setAmenity(Amenity amenity) {
        this.amenity = amenity;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Builder)) return false;
            Builder builder = (Builder) o;
            return Objects.equals(amenityAvailabilityId, builder.amenityAvailabilityId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(amenityAvailabilityId);
        }
    }
}
