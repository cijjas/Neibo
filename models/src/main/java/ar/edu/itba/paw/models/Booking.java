package ar.edu.itba.paw.models;

import java.sql.Time;
import java.util.Date;

public class Booking {
    private final long bookingId;
    private final long userId;
    private final String amenityName;
    private final String dayName;
    private final Time startTime;
    private final Date bookingDate;

    private Booking(Builder builder) {
        this.bookingId = builder.bookingId;
        this.userId = builder.userId;
        this.amenityName = builder.amenityName;
        this.dayName = builder.dayName;
        this.startTime = builder.startTime;
        this.bookingDate = builder.bookingDate;
    }

    public long getBookingId() {
        return bookingId;
    }

    public long getUserId() {
        return userId;
    }

    public String getAmenityName() {
        return amenityName;
    }

    public String getDayName() {
        return dayName;
    }

    public Time getStartTime() {
        return startTime;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", userId=" + userId +
                ", amenityName='" + amenityName + '\'' +
                ", dayName='" + dayName + '\'' +
                ", startTime=" + startTime +
                ", bookingDate=" + bookingDate +
                '}';
    }

    public static class Builder {
        private long bookingId;
        private long userId;
        private String amenityName;
        private String dayName;
        private Time startTime;
        private Date bookingDate;

        public Builder bookingId(long bookingId) {
            this.bookingId = bookingId;
            return this;
        }

        public Builder userId(long userId) {
            this.userId = userId;
            return this;
        }

        public Builder amenityName(String amenityName) {
            this.amenityName = amenityName;
            return this;
        }

        public Builder dayName(String dayName) {
            this.dayName = dayName;
            return this;
        }

        public Builder startTime(Time startTime) {
            this.startTime = startTime;
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
