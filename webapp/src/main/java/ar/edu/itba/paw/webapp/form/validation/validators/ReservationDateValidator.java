package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.form.validation.constraints.ReservationDateConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReservationDateValidator implements ConstraintValidator<ReservationDateConstraint, String> {

    @Override
    public void initialize(ReservationDateConstraint constraint) {
    }

    @Override
    public boolean isValid(String date, ConstraintValidatorContext context) {
        if (date == null || date.trim().isEmpty()) {
            // Allow null or empty values, as @NotNull will handle it
            return true;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(true);
        try {
            Date parsedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Invalid Date Format. Please use YYYY-MM-DD.")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

}
