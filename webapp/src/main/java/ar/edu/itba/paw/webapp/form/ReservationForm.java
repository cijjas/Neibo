package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.AmenityURNConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.DateAfterConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.ExistingAmenityConstraint;

import java.sql.Date;

public class ReservationForm {

    @DateAfterConstraint
    private Date date;

    @AmenityURNConstraint
    private String amenityURN;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAmenityURN() {
        return amenityURN;
    }

    public void setAmenityURN(String amenityURN) {
        this.amenityURN = amenityURN;
    }

    @Override
    public String toString() {
        return "ReservationForm{" +
                ", date=" + date +
                ", amenityURN=" + amenityURN +
                '}';
    }
}
