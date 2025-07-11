package ar.edu.itba.paw.models.Entities;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "users_availability")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_availability_bookingid_seq")
    @SequenceGenerator(sequenceName = "users_availability_bookingid_seq", name = "users_availability_bookingid_seq", allocationSize = 1)
    @Column(name = "bookingid")
    private Long bookingId;

    @ManyToOne
    @JoinColumn(name = "userid")
    private User user;

    @ManyToOne
    @JoinColumn(name = "amenityavailabilityid")
    private Availability amenityAvailability;

    @Column(name = "date")
    @Temporal(TemporalType.DATE)
    private Date bookingDate;

    Booking() {
    }

    private Booking(Builder builder) {
        this.bookingId = builder.bookingId;
        this.user = builder.user;
        this.amenityAvailability = builder.amenityAvailability;
        this.bookingDate = builder.bookingDate;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Availability getAmenityAvailability() {
        return amenityAvailability;
    }

    public void setAmenityAvailability(Availability amenityAvailability) {
        this.amenityAvailability = amenityAvailability;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking)) return false;
        Booking booking = (Booking) o;
        return Objects.equals(bookingId, booking.bookingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId);
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
