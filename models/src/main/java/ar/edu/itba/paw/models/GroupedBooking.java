
package ar.edu.itba.paw.models;

import java.sql.Time;
import java.util.Date;

public class GroupedBooking {
    private String amenityName;
    private Date date;
    private String day; // Change the type from Day to String
    private Time startTime;
    private Time endTime;

    public GroupedBooking(String amenityName, Date date, String day, Time startTime, Time endTime) { // Change the constructor parameter type
        this.amenityName = amenityName;
        this.date = date;
        this.day = day; // Change the type
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getAmenityName() {
        return amenityName;
    }

    public Date getDate() {
        return date;
    }

    public String getDay() { // Change the return type from Day to String
        return day;
    }

    public Time getStartTime() {
        return startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setAmenityName(String amenityName) {
        this.amenityName = amenityName;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDay(String day) { // Change the parameter type from Day to String
        this.day = day;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public boolean canCombine(Booking booking) {
        // Check if the endTime is one hour after the next booking's startTime
        return this.amenityName.equals(booking.getAmenity().getName()) &&
                this.date.equals(booking.getBookingDate()) &&
                this.day.equals(booking.getShift().getDay()) &&
                this.endTime.equals(booking.getShift().getStartTime());
    }

    public void combine(Booking booking) {
        // Calculate endTime by adding one hour to the next booking's startTime
        long startTimeMillis = booking.getShift().getStartTime().getTime();
        long endTimeMillis = startTimeMillis + 60 * 60 * 1000; // 60 minutes * 60 seconds * 1000 milliseconds
        this.endTime = new Time(endTimeMillis);
    }

    @Override
    public String toString() {
        return "GroupedBooking{" +
                "amenityName='" + amenityName + '\'' +
                ", date=" + date +
                ", day='" + day + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}