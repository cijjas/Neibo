package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.AmenityURNConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.ReservationDateConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.ShiftURNConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.UserURNReferenceConstraint;

import javax.validation.constraints.NotNull;

public class BookingForm {
    @NotNull
    @AmenityURNConstraint
    private String amenity; // http://localhost:8080/neighborhoods/{neighborhoodId}/amenities/{amenityId}

    @NotNull
    @ShiftURNConstraint
    private String shift; // http://localhost:8080/shifts/{shiftId}

    @NotNull
    @ReservationDateConstraint
    private String reservationDate;

    @NotNull
    @UserURNReferenceConstraint
    private String user;

    public String getAmenity() {
        return amenity;
    }

    public void setAmenity(String amenity) {
        this.amenity = amenity;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(String reservationDate) {
        this.reservationDate = reservationDate;
    }

    @Override
    public String toString() {
        return "BookingForm{" +
                "amenity='" + amenity + '\'' +
                ", shift='" + shift + '\'' +
                ", reservationDate='" + reservationDate + '\'' +
                ", user='" + user + '\'' +
                '}';
    }
}

