package ar.edu.itba.paw.webapp.validation.validators.specific;

import ar.edu.itba.paw.webapp.validation.constraints.specific.GenericIdConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class GenericIdValidator implements ConstraintValidator<GenericIdConstraint, Long> {

    @Override
    public void initialize(GenericIdConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(Long genericId, ConstraintValidatorContext context) {
        if (genericId == null)
            return false;
        // 0 is the Worker Neighborhood
        // -1 is the Banned Neighborhood
        // -2 is the SuperAdmin Neighborhood
        return genericId >= 0;
    }
}
