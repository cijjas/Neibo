package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.ValidTimeRangeConstraint;
import org.hibernate.validator.constraints.NotBlank;
import java.sql.Time;

import javax.validation.constraints.Size;
import java.sql.Date;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@ValidTimeRangeConstraint
public class EventForm {
    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 2000)
    private String description;

    private Date date;

    private Time startTime;

    private Time endTime;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public Time getStartTime() { return startTime; }
    public void setStartTime(String startTimeString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            java.util.Date parsedDate = sdf.parse(startTimeString);
            this.startTime = new Time(parsedDate.getTime());
        } catch (ParseException e) {
            // Handle parsing exception
            e.printStackTrace();
        }
    }

    public Time getEndTime() { return endTime; }
    public void setEndTime(String endTimeString){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            java.util.Date parsedDate = sdf.parse(endTimeString);
            this.endTime = new Time(parsedDate.getTime());
        } catch (ParseException e) {
            // Handle parsing exception
            e.printStackTrace();
        }
    }


    @Override
    public String toString() {
        return "PublishForm{" +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
