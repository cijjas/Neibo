package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.util.List;

public class BookingForm {
    @NotNull
    private long amenityId;

    @NotNull
    private List<Long> shiftIds;

    @NotNull
    private Date reservationDate;

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

    public Date getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(Date reservationDate) {
        this.reservationDate = reservationDate;
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

