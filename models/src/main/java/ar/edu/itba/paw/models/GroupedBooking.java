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
        System.out.println("SUSPOSEDLY NOT NULL END TIME  RECEIVEDDDDDD= "+endTime);
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
        System.out.println("Amenity equals");
        System.out.println(amenityName);
        System.out.println(booking.getAmenityAvailability().getAmenity().getName());
        System.out.println(amenityName.equals(booking.getAmenityAvailability().getAmenity().getName()));
        System.out.println("Date equals");
        System.out.println(date);
        System.out.println(booking.getBookingDate());
        System.out.println(date.equals(booking.getBookingDate()));
        System.out.println("Day equals");
        System.out.println(day);
        System.out.println(booking.getAmenityAvailability().getShift().getDay().getDayName());
        System.out.println(day.equals(booking.getAmenityAvailability().getShift().getDay().getDayName()));
        System.out.println("End time equals");
        System.out.println(endTime);
        System.out.println(booking.getAmenityAvailability().getShift().getStartTime().getTimeInterval().getTime());
        System.out.println(endTime.equals(booking.getAmenityAvailability().getShift().getStartTime().getTimeInterval()));
        System.out.println("-----------------------------------");
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
