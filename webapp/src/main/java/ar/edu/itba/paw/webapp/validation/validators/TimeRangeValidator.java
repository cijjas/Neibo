package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.webapp.validation.constraints.ValidTimeRangeConstraint;
import ar.edu.itba.paw.webapp.dto.EventDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TimeRangeValidator implements ConstraintValidator<ValidTimeRangeConstraint, EventDto> {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm"); // Handles both `9:00` and `09:00`

    @Override
    public void initialize(ValidTimeRangeConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(EventDto eventForm, ConstraintValidatorContext context) {
        if (eventForm.getStartTime() == null || eventForm.getEndTime() == null) {
            // Allow @Null validation to handle null cases
            return true;
        }

        try {
            // Parse and normalize the times
            LocalTime startTime = parseTime(eventForm.getStartTime());
            LocalTime endTime = parseTime(eventForm.getEndTime());

            // Check if startTime is before endTime
            return startTime.isBefore(endTime);
        } catch (DateTimeParseException e) {
            // Invalid time format; return false or true based on desired behavior
            return false;
        }
    }

    private LocalTime parseTime(String time) {
        return LocalTime.parse(time, TIME_FORMATTER);
    }
}
