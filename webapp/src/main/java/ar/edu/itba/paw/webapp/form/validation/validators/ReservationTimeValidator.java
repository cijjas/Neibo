package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.form.AmenityForm;
import ar.edu.itba.paw.webapp.form.ReservationForm;
import ar.edu.itba.paw.webapp.form.validation.constraints.ReservationTime;
import ar.edu.itba.paw.webapp.form.validation.constraints.TimeOrder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.sql.Time;
import java.time.LocalTime;

public class ReservationTimeValidator implements ConstraintValidator<ReservationTime, ReservationForm> {

    @Override
    public void initialize(ReservationTime constraint) {
    }

    @Override
    public boolean isValid(ReservationForm form, ConstraintValidatorContext context) {
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
