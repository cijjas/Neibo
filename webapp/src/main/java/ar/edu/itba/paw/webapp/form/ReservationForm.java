package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.DateAfterConstraint;

import java.sql.Date;

public class ReservationForm {

    @DateAfterConstraint
    private Date date;

    private long amenityId;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getAmenityId() {
        return amenityId;
    }

    public void setAmenityId(long amenityId) {
        this.amenityId = amenityId;
    }

    @Override
    public String toString() {
        return "ReservationForm{" +
                ", date=" + date +
                ", amenityId=" + amenityId +
                '}';
    }
}
