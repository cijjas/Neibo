package ar.edu.itba.paw.models;

import ar.edu.itba.paw.models.JunctionEntities.Booking;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GroupedBooking {
    private List<Long> bookingIds;
    private String amenityName;
    private Date date;
    private String day;
    private Time startTime;
    private Time endTime;

    public GroupedBooking(String amenityName, Date date, String day, Time startTime, Time endTime) {
        this.bookingIds = new ArrayList<>();
        this.amenityName = amenityName;
        this.date = date;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public List<Long> getBookingIds() {
        return bookingIds;
    }

    public String getAmenityName() {
        return amenityName;
    }

    public Date getDate() {
        return date;
    }

    public String getDay() {
        return day;
    }

    public Time getStartTime() {
        return startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void addBookingId(Long bookingId) {
        bookingIds.add(bookingId);
    }

    public boolean canCombine(Booking booking) {
        return amenityName.equals(booking.getAmenityAvailability().getAmenity().getName()) &&
                date.equals(booking.getBookingDate()) &&
                day.equals(booking.getAmenityAvailability().getShift().getDay().getDayName()) &&
                endTime.equals(booking.getAmenityAvailability().getShift().getStartTime().getTimeInterval());
    }

    public void combine(Booking booking) {
        long startTimeMillis = booking.getAmenityAvailability().getShift().getStartTime().getTimeInterval().getTime();
        long endTimeMillis = startTimeMillis + 60 * 60 * 1000;
        this.endTime = new Time(endTimeMillis);
    }

    @Override
    public String toString() {
        return "GroupedBooking{" +
                "bookingIds=" + bookingIds +
                ", amenityName='" + amenityName + '\'' +
                ", date=" + date +
                ", day=" + day +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
