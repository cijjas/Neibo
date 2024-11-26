package ar.edu.itba.paw.webapp.validation.validators.specific;

import ar.edu.itba.paw.webapp.dto.EventDto;
import ar.edu.itba.paw.webapp.validation.constraints.specific.TimeRangeConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TimeRangeValidator implements ConstraintValidator<TimeRangeConstraint, EventDto> {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public void initialize(TimeRangeConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(EventDto eventForm, ConstraintValidatorContext context) {
        if (eventForm == null || eventForm.getStartTime() == null || eventForm.getEndTime() == null)
            return true;

        try {
            LocalTime startTime = parseTime(eventForm.getStartTime());
            LocalTime endTime = parseTime(eventForm.getEndTime());

            return startTime.isBefore(endTime);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private LocalTime parseTime(String time) {
        return LocalTime.parse(time, TIME_FORMATTER);
    }
}
