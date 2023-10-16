package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.form.ReservationTimeForm;
import ar.edu.itba.paw.webapp.form.validation.constraints.ReservationTimeConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.sql.Time;
import java.time.LocalTime;

public class ReservationTimeValidator implements ConstraintValidator<ReservationTimeConstraint, ReservationTimeForm> {

    @Override
    public void initialize(ReservationTimeConstraint constraint) {
    }

    @Override
    public boolean isValid(ReservationTimeForm form, ConstraintValidatorContext context) {
        return isTimeOrderValid(form.getStartTime(), form.getEndTime());
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
