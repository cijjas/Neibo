package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.form.EventForm;
import ar.edu.itba.paw.webapp.form.validation.constraints.ValidTimeRangeConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TimeRangeValidator implements ConstraintValidator<ValidTimeRangeConstraint, EventForm> {
    @Override
    public void initialize(ValidTimeRangeConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(EventForm eventForm, ConstraintValidatorContext context) {
        // Check if startTime is before endTime
        return eventForm.getStartTime().compareTo(eventForm.getEndTime()) < 0;
    }
}

