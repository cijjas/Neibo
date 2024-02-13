package ar.edu.itba.paw.webapp.form;


import ar.edu.itba.paw.webapp.form.validation.constraints.ExistingAmenityConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.ReservationTimeConstraint;

import java.sql.Date;
import java.sql.Time;

@ReservationTimeConstraint
public class ReservationTimeForm {
    private Date date;

    @ExistingAmenityConstraint
    private long amenityId;

    private Time startTime;

    private Time endTime;

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
