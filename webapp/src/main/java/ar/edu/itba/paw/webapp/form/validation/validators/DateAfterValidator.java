package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.form.validation.constraints.DateAfterConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.sql.Date;

public class DateAfterValidator implements
        ConstraintValidator<DateAfterConstraint, Date> {


    @Override
    public void initialize(DateAfterConstraint dateAfterConstraint) {

    }

    @Override
    public boolean isValid(Date date, ConstraintValidatorContext constraintValidatorContext) {
        return date.after(new Date(System.currentTimeMillis()));
    }
}
