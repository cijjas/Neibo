package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.form.validation.constraints.DateAfterConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.TagsConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.sql.Date;
import java.util.Locale;

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
