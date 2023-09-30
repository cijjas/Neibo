package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.form.AmenityForm;
import ar.edu.itba.paw.webapp.form.validation.constraints.TimeOrder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.sql.Time;
import java.time.LocalTime;

public class TimeOrderValidator implements ConstraintValidator<TimeOrder, AmenityForm> {

    @Override
    public void initialize(TimeOrder constraint) {
    }

    @Override
    public boolean isValid(AmenityForm form, ConstraintValidatorContext context) {
        return isTimeOrderValid(form.getMondayOpenTime(), form.getMondayCloseTime()) &&
                isTimeOrderValid(form.getTuesdayOpenTime(), form.getTuesdayCloseTime()) &&
                isTimeOrderValid(form.getWednesdayOpenTime(), form.getWednesdayCloseTime()) &&
                isTimeOrderValid(form.getThursdayOpenTime(), form.getThursdayCloseTime()) &&
                isTimeOrderValid(form.getFridayOpenTime(), form.getFridayCloseTime()) &&
                isTimeOrderValid(form.getSaturdayOpenTime(), form.getSaturdayCloseTime()) &&
                isTimeOrderValid(form.getSundayOpenTime(), form.getSundayCloseTime());
    }

    private boolean isTimeOrderValid(Time openTime, Time closeTime) {
        if (openTime == null || closeTime == null) {
            return true; // Skip validation if either time is null
        }

        LocalTime open = openTime.toLocalTime();
        LocalTime close = closeTime.toLocalTime();

        return open.isBefore(close);
    }
}
