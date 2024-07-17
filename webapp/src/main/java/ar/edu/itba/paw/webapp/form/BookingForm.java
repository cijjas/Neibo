package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.*;
import javax.validation.constraints.NotNull;

public class BookingForm {
    @NotNull
    @AmenityURNConstraint
    private String amenityURN; // http://localhost:8080/neighborhoods/{neighborhoodId}/amenities/{amenityId}

    @NotNull
    @ShiftURNConstraint
    private String shiftURN; // http://localhost:8080/shifts/{shiftId}

    @NotNull
    @ReservationDateConstraint
    private String reservationDate;

    public String getAmenityURN() {
        return amenityURN;
    }

    public void setAmenityURN(String amenityURN) {
        this.amenityURN = amenityURN;
    }

    public String getShiftURN() {
        return shiftURN;
    }

    public void setShiftURN(String shiftURN) {
        this.shiftURN = shiftURN;
    }

    public void setReservationDate(String reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getReservationDate() {
        return reservationDate;
    }

    @Override
    public String toString() {
        return "BookingForm{" +
                ", reservationDate=" + reservationDate +
                '}';
    }
}

