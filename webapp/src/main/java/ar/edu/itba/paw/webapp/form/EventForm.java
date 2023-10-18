package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.ValidTimeRangeConstraint;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;
import java.sql.Date;
import java.sql.Time;
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

    private String startTime;

    private String endTime;

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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
