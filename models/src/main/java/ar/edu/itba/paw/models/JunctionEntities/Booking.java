package ar.edu.itba.paw.models.JunctionEntities;

import java.sql.Date;

import ar.edu.itba.paw.models.MainEntities.Amenity;
import ar.edu.itba.paw.models.MainEntities.Day;
import ar.edu.itba.paw.models.MainEntities.Time;

public class Booking {
    private final Long bookingId;
    private final Long userId;
    private final Amenity amenity;
    private final Day day;
    private final Time time;
    private final Date bookingDate;

    private Booking(Builder builder) {
        this.bookingId = builder.bookingId;
        this.userId = builder.userId;
        this.amenity = builder.amenity;
        this.day = builder.day;
        this.time = builder.time;
        this.bookingDate = builder.bookingDate;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public Long getUserId() {
        return userId;
    }

    public Amenity getAmenity() {
        return amenity;
    }

    public Day getDay() {
        return day;
    }

    public Time getTime() {
        return time;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", userId=" + userId +
                ", amenity=" + amenity +
                ", day=" + day +
                ", time=" + time +
                ", bookingDate=" + bookingDate +
                '}';
    }

    public static class Builder {
        private Long bookingId;
        private Long userId;
        private Amenity amenity;
        private Day day;
        private Time time;
        private Date bookingDate;

        public Builder bookingId(Long bookingId) {
            this.bookingId = bookingId;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder amenity(Amenity amenity) {
            this.amenity = amenity;
            return this;
        }

        public Builder day(Day day) {
            this.day = day;
            return this;
        }

        public Builder time(Time time) {
            this.time = time;
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

