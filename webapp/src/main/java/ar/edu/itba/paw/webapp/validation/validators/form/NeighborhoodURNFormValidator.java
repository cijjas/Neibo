package ar.edu.itba.paw.webapp.validation.validators.form;

import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.NeighborhoodURNFormConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NeighborhoodURNFormValidator implements ConstraintValidator<NeighborhoodURNFormConstraint, String> {

    @Override
    public void initialize(NeighborhoodURNFormConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String neighborhoodURN, ConstraintValidatorContext context) {
        if (neighborhoodURN == null)
            return true;
        return URNValidator.validateURN(neighborhoodURN, "neighborhood");
    }
}
