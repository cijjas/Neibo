package ar.edu.itba.paw.models;

import java.util.Date;

public class Booking {
    private final long bookingId;
    private final long userId; // Represent user as a long
    private final Amenity amenity;
    private final Shift shift;
    private final Date bookingDate; // Renamed from shiftDate

    private Booking(Builder builder) {
        this.bookingId = builder.bookingId;
        this.userId = builder.userId; // Represent user as a long
        this.amenity = builder.amenity;
        this.shift = builder.shift;
        this.bookingDate = builder.bookingDate; // Renamed from shiftDate
    }

    public static class Builder {
        private long bookingId;
        private long userId; // Represent user as a long
        private Amenity amenity;
        private Shift shift;
        private Date bookingDate; // Renamed from shiftDate

        public Builder bookingId(long bookingId) {
            this.bookingId = bookingId;
            return this;
        }

        public Builder userId(long userId) {
            this.userId = userId; // Represent user as a long
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

        public Builder bookingDate(Date bookingDate) {
            this.bookingDate = bookingDate; // Renamed from shiftDate
            return this;
        }

        public Booking build() {
            return new Booking(this);
        }
    }

    public long getBookingId() {
        return bookingId;
    }

    public long getUserId() {
        return userId; // Represent user as a long
    }

    public Amenity getAmenity() {
        return amenity;
    }

    public Shift getShift() {
        return shift;
    }

    public Date getBookingDate() {
        return bookingDate; // Renamed from shiftDate
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", userId=" + userId + // Represent user as a long
                ", amenity=" + amenity +
                ", shift=" + shift +
                ", bookingDate=" + bookingDate + // Renamed from shiftDate
                '}';
    }
}
