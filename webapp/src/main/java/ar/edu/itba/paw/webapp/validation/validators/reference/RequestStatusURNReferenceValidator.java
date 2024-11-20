package ar.edu.itba.paw.webapp.validation.validators.reference;

import ar.edu.itba.paw.webapp.validation.constraints.reference.RequestStatusURNReferenceConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RequestStatusURNReferenceValidator implements ConstraintValidator<RequestStatusURNReferenceConstraint, String> {
    @Override
    public void initialize(RequestStatusURNReferenceConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null)
            return true;
        // URNValidator or ReferenceValidator
        return true;
    }
}
