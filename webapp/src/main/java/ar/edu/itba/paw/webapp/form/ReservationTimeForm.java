package ar.edu.itba.paw.webapp.form;


import ar.edu.itba.paw.webapp.form.validation.constraints.AmenityURNConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.ExistingAmenityConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.ReservationTimeConstraint;

import java.sql.Date;
import java.sql.Time;

@ReservationTimeConstraint
public class ReservationTimeForm {
    private Date date;

    @AmenityURNConstraint
    private String amenityURN;

    private Time startTime;

    private Time endTime;

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

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "ReservationForm{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
