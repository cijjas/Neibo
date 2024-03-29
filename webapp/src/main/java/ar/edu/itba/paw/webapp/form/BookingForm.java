package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.util.List;

public class BookingForm {
    @NotNull
    @AmenityURNConstraint
    private String amenityURN; // http://localhost:8080/neighborhoods/{neighborhoodId}/amenities/{amenityId}

    @NotNull
    @ShiftsURNConstraint
    private List<String> shiftURNs; // http://localhost:8080/shifts/{shiftId}

    @NotNull
    @ReservationDateConstraint
    private String reservationDate;

    public String getAmenityURN() {
        return amenityURN;
    }

    public void setAmenityURN(String amenityURN) {
        this.amenityURN = amenityURN;
    }

    public List<String> getShiftURNs() {
        return shiftURNs;
    }

    public void setShiftURNs(List<String> shiftURNs) {
        this.shiftURNs = shiftURNs;
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

