package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.form.validation.constraints.NeighborhoodURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NeighborhoodURNValidator implements ConstraintValidator<NeighborhoodURNConstraint, String> {
    @Override
    public void initialize(NeighborhoodURNConstraint neighborhoodURNConstraint) {
    }

    @Override
    public boolean isValid(String neighborhoodURN, ConstraintValidatorContext constraintValidatorContext) {
        if (neighborhoodURN == null)
            return false;
        return URNValidator.validateURN(neighborhoodURN, "neighborhood");
    }
}