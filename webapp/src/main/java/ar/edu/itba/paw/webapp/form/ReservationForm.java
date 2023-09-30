package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.ReservationTime;
import ar.edu.itba.paw.webapp.form.validation.constraints.TimeOrder;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.sql.Time;

@ReservationTime
public class ReservationForm {

    private Date date;

    private Time startTime;

    private Time endTime;

    private long amenityId;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public long getAmenityId() {
        return amenityId;
    }

    public void setAmenityId(long amenityId) {
        this.amenityId = amenityId;
    }



    @Override
    public String toString() {
        return "ReservationForm{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", date=" + date +
                ", amenityId=" + amenityId +
                '}';
    }
}
