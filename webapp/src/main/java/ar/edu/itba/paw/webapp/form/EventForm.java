package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.NotBlank;
import java.sql.Time;

import javax.validation.constraints.Size;
import java.sql.Date;

public class EventForm {
    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 2000)
    private String description;

    private Date date;

    private int startTimeHours;

    private int startTimeMinutes;

    private int endTimeHours;

    private int endTimeMinutes;

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
    public int getStartTimeHours() { return startTimeHours; }
    public void setStartTimeHours(int startTimeHours) { this.startTimeHours = startTimeHours; }
    public int getStartTimeMinutes() { return startTimeMinutes; }
    public void setStartTimeMinutes(int startTimeMinutes) { this.startTimeMinutes = startTimeMinutes; }
    public int getEndTimeHours() { return endTimeHours; }
    public void setEndTimeHours(int endTimeHours) { this.endTimeHours = endTimeHours; }
    public int getEndTimeMinutes() { return endTimeMinutes; }
    public void setEndTimeMinutes(int endTimeMinutes) { this.endTimeMinutes = endTimeMinutes; }

    public Time getStartTime() { return new Time(startTimeHours, startTimeMinutes, 0); }

    public Time getEndTime() { return new Time(endTimeHours, endTimeMinutes, 0); }

    @Override
    public String toString() {
        return "PublishForm{" +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", date='"+ date + '\'' +
                ", startTimeHours='"+ startTimeHours + '\'' +
                ", startTimeMinutes='"+ startTimeMinutes + '\'' +
                ", endTimeHours='"+ endTimeHours + '\'' +
                ", endTimeMinutes='"+ endTimeMinutes + '\'' +
                '}';
    }
}
