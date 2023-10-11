package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.models.DayTime;
import ar.edu.itba.paw.webapp.form.validation.constraints.ImageConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.TagsConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.TimeOrder;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.sql.Time;
import java.util.Map;

@TimeOrder
public class AmenityForm {
    @NotBlank
    @Size(max = 100)
    @Pattern(regexp = "[a-zA-Z0-9 ?!@_]+")
    private String name;

    @NotBlank
    @Size(max = 1000)
    @Pattern(regexp = "[a-zA-Z0-9 ?!@_]+")
    private String description;

    private Time mondayOpenTime;
    private Time mondayCloseTime;
    private Time tuesdayOpenTime;
    private Time tuesdayCloseTime;
    private Time wednesdayOpenTime;
    private Time wednesdayCloseTime;
    private Time thursdayOpenTime;
    private Time thursdayCloseTime;
    private Time fridayOpenTime;
    private Time fridayCloseTime;
    private Time saturdayOpenTime;
    private Time saturdayCloseTime;
    private Time sundayOpenTime;
    private Time sundayCloseTime;

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

    public Time getMondayOpenTime() {
        return mondayOpenTime;
    }

    public void setMondayOpenTime(Time mondayOpenTime) {
        this.mondayOpenTime = mondayOpenTime;
    }

    public Time getMondayCloseTime() {
        return mondayCloseTime;
    }

    public void setMondayCloseTime(Time mondayCloseTime) {
        this.mondayCloseTime = mondayCloseTime;
    }

    public Time getTuesdayOpenTime() {
        return tuesdayOpenTime;
    }

    public void setTuesdayOpenTime(Time tuesdayOpenTime) {
        this.tuesdayOpenTime = tuesdayOpenTime;
    }

    public Time getTuesdayCloseTime() {
        return tuesdayCloseTime;
    }

    public void setTuesdayCloseTime(Time tuesdayCloseTime) {
        this.tuesdayCloseTime = tuesdayCloseTime;
    }

    public Time getWednesdayOpenTime() {
        return wednesdayOpenTime;
    }

    public void setWednesdayOpenTime(Time wednesdayOpenTime) {
        this.wednesdayOpenTime = wednesdayOpenTime;
    }

    public Time getWednesdayCloseTime() {
        return wednesdayCloseTime;
    }

    public void setWednesdayCloseTime(Time wednesdayCloseTime) {
        this.wednesdayCloseTime = wednesdayCloseTime;
    }

    public Time getThursdayOpenTime() {
        return thursdayOpenTime;
    }

    public void setThursdayOpenTime(Time thursdayOpenTime) {
        this.thursdayOpenTime = thursdayOpenTime;
    }

    public Time getThursdayCloseTime() {
        return thursdayCloseTime;
    }

    public void setThursdayCloseTime(Time thursdayCloseTime) {
        this.thursdayCloseTime = thursdayCloseTime;
    }

    public Time getFridayOpenTime() {
        return fridayOpenTime;
    }

    public void setFridayOpenTime(Time fridayOpenTime) {
        this.fridayOpenTime = fridayOpenTime;
    }

    public Time getFridayCloseTime() {
        return fridayCloseTime;
    }

    public void setFridayCloseTime(Time fridayCloseTime) {
        this.fridayCloseTime = fridayCloseTime;
    }

    public Time getSaturdayOpenTime() {
        return saturdayOpenTime;
    }

    public void setSaturdayOpenTime(Time saturdayOpenTime) {
        this.saturdayOpenTime = saturdayOpenTime;
    }

    public Time getSaturdayCloseTime() {
        return saturdayCloseTime;
    }

    public void setSaturdayCloseTime(Time saturdayCloseTime) {
        this.saturdayCloseTime = saturdayCloseTime;
    }

    public Time getSundayOpenTime() {
        return sundayOpenTime;
    }

    public void setSundayOpenTime(Time sundayOpenTime) {
        this.sundayOpenTime = sundayOpenTime;
    }

    public Time getSundayCloseTime() {
        return sundayCloseTime;
    }

    public void setSundayCloseTime(Time sundayCloseTime) {
        this.sundayCloseTime = sundayCloseTime;
    }

    @Override
    public String toString() {
        return "PublishForm{" +
                ", subject='" + name + '\'' +
                ", message='" + description + '\'' +
                "mondayTimes: " + mondayOpenTime + " - " + mondayCloseTime + "\n" +
                "tuesdayTimes: " + tuesdayOpenTime + " - " + tuesdayCloseTime + "\n" +
                "wednesdayTimes: " + wednesdayOpenTime + " - " + wednesdayCloseTime + "\n" +
                "thursdayTimes: " + thursdayOpenTime + " - " + thursdayCloseTime + "\n" +
                "fridayTimes: " + fridayOpenTime + " - " + fridayCloseTime + "\n" +
                "saturdayTimes: " + saturdayOpenTime + " - " + saturdayCloseTime + "\n" +
                "sundayTimes: " + sundayOpenTime + " - " + sundayCloseTime + "\n" +
                '}';
    }
}

