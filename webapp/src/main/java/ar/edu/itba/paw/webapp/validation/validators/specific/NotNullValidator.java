package ar.edu.itba.paw.webapp.validation.validators.specific;

import ar.edu.itba.paw.webapp.validation.constraints.specific.NotNullConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class NotNullValidator implements ConstraintValidator<NotNullConstraint, Object> {

    @Override
    public void initialize(NotNullConstraint notNullConstraint) {}

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext constraintValidatorContext) {
        return Objects.nonNull(obj);
    }
}
