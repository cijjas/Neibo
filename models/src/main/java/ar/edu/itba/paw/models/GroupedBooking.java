package ar.edu.itba.paw.models;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

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

    public void addBookingId(long bookingId) {
        bookingIds.add(bookingId);
    }

    public boolean canCombine(Booking booking) {
        return amenityName.equals(booking.getAmenityName()) &&
                date.equals(booking.getBookingDate()) &&
                day.equals(booking.getDayName()) &&
                endTime.equals(booking.getStartTime());
    }

    public void combine(Booking booking) {
        long startTimeMillis = booking.getStartTime().getTime();
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
