package ar.edu.itba.paw.webapp.form.validation;


import ar.edu.itba.paw.webapp.form.validation.constraints.ReservationTime;
import ar.edu.itba.paw.webapp.form.validation.constraints.TimeOrder;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.sql.Time;

@ReservationTime
public class ReservationTimeForm {

    private Date date;

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
