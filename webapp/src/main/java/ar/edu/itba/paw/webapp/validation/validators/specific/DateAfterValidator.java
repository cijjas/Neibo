package ar.edu.itba.paw.webapp.validation.validators.specific;

import ar.edu.itba.paw.webapp.validation.constraints.specific.DateAfterConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Date;

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
