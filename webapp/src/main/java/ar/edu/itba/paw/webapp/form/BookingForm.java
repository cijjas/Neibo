package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.ExistingAmenityConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.ReservationDateConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.ReservationTimeConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.ShiftsConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.util.List;

public class BookingForm {
    @NotNull
    @ExistingAmenityConstraint
    private long amenityId;

    @NotNull
    @ShiftsConstraint
    private List<Long> shiftIds;

    @NotNull
    @ReservationDateConstraint
    private String reservationDate;

    public long getAmenityId() {
        return amenityId;
    }

    public void setAmenityId(long amenityId) {
        this.amenityId = amenityId;
    }

    public List<Long> getShiftIds() {
        return shiftIds;
    }

    public void setShiftIds(List<Long> shiftIds) {
        this.shiftIds = shiftIds;
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
                "amenityId=" + amenityId +
                ", shiftIds=" + shiftIds +
                ", reservationDate=" + reservationDate +
                '}';
    }
}

