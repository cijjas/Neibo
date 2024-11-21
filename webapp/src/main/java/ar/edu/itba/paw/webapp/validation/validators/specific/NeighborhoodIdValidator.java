package ar.edu.itba.paw.webapp.validation.validators.specific;

import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.specific.NeighborhoodIdConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NeighborhoodIdValidator implements ConstraintValidator<NeighborhoodIdConstraint, Long> {
    @Override
    public void initialize(NeighborhoodIdConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(Long neighborhoodId, ConstraintValidatorContext context) {
        if (neighborhoodId == null)
            return false;
        // 0 is the Worker Neighborhood
        // -1 is the Banned Neighborhood
        // -2 is the SuperAdmin Neighborhood
        return neighborhoodId >= -2;
    }
}
