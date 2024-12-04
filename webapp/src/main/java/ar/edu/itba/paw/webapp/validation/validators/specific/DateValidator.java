package ar.edu.itba.paw.webapp.validation.validators.specific;

import ar.edu.itba.paw.webapp.validation.constraints.specific.DateConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateValidator implements ConstraintValidator<DateConstraint, String> {

    @Override
    public void initialize(DateConstraint constraint) {
    }

    @Override
    public boolean isValid(String date, ConstraintValidatorContext context) {
        if (date == null || date.trim().isEmpty())
            return true;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
           dateFormat.parse(date);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
}
