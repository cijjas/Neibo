package ar.edu.itba.paw.models.JunctionEntities;

import java.sql.Date;

import ar.edu.itba.paw.models.MainEntities.Amenity;
import ar.edu.itba.paw.models.MainEntities.User;

import javax.persistence.*;

@Entity
@Table(name = "users_availability")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_availability_bookingid_seq")
    @SequenceGenerator(sequenceName = "users_availability_bookingid_seq", name = "users_availability_bookingid_seq", allocationSize = 1)
    @Column(name = "bookingid")
    private Long bookingId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userid", referencedColumnName = "userid")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "amenityavailabilityid", referencedColumnName = "amenityavailabilityid")
    private Availability amenityAvailability;

    @Column(name = "date")
    private Date bookingDate;

    public Booking() {}

    private Booking(Builder builder) {
        this.bookingId = builder.bookingId;
        this.user = builder.user;
        this.amenityAvailability = builder.amenityAvailability;
        this.bookingDate = builder.bookingDate;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public User getUser() {
        return user;
    }

    public Availability getAmenityAvailability() {
        return amenityAvailability;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", user=" + user +
                ", amenityAvailability=" + amenityAvailability +
                ", bookingDate=" + bookingDate +
                '}';
    }

    public static class Builder {
        private Long bookingId;
        private User user;
        private Availability amenityAvailability;
        private Date bookingDate;

        public Builder bookingId(Long bookingId) {
            this.bookingId = bookingId;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder amenityAvailability(Availability amenityAvailability) {
            this.amenityAvailability = amenityAvailability;
            return this;
        }

        public Builder bookingDate(Date bookingDate) {
            this.bookingDate = bookingDate;
            return this;
        }

        public Booking build() {
            return new Booking(this);
        }
    }
}
