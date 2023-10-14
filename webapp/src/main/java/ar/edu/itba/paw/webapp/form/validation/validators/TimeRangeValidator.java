package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.form.EventForm;
import ar.edu.itba.paw.webapp.form.validation.constraints.ValidTimeRange;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TimeRangeValidator implements ConstraintValidator<ValidTimeRange, EventForm> {
    @Override
    public void initialize(ValidTimeRange constraintAnnotation) {
    }

    @Override
    public boolean isValid(EventForm eventForm, ConstraintValidatorContext context) {
        // Check if startTime is before endTime
        if (eventForm.getStartTime().compareTo(eventForm.getEndTime()) < 0) {
            return true;
        }
        return false;
    }
}

