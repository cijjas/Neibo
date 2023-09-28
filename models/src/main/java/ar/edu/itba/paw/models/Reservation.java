package ar.edu.itba.paw.models;

import java.sql.Time;
import java.util.Date;

public class Reservation {
    private final long reservationId;
    private final Amenity amenity;
    private final User user;
    private final Date date;
    private final Time startTime;
    private final Time endTime;

    private Reservation(Builder builder){
        this.reservationId = builder.reservationId;
        this.amenity = builder.amenity;
        this.user = builder.user;
        this.date = builder.date;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
    }

    public static class Builder {
        private long reservationId;
        private Amenity amenity;
        private User user;
        private Date date;
        private Time startTime;
        private Time endTime;

        public Builder reservationId(long reservationId) {
            this.reservationId = reservationId;
            return this;
        }
        public Builder amenity(Amenity amenity) {
            this.amenity = amenity;
            return this;
        }
        public Builder user(User user) {
            this.user = user;
            return this;
        }
        public Builder date(Date date) {
            this.date = date;
            return this;
        }
        public Builder startTime(Time startTime) {
            this.startTime = startTime;
            return this;
        }
        public Builder endTime(Time endTime) {
            this.endTime = endTime;
            return this;
        }
        public Reservation build(){
            return new Reservation(this);
        }
    }

    public long getReservationId() {
        return reservationId;
    }
    public Amenity getAmenity() {
        return amenity;
    }
    public User getUser() {
        return user;
    }
    public Date getDate() {
        return date;
    }
    public Time getStartTime() {
        return startTime;
    }
    public Time getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "Reservation [reservationId=" + reservationId + ", amenity=" + amenity + ", user=" + user + ", date="
                + date + ", startTime=" + startTime + ", endTime=" + endTime + "]";
    }

}
