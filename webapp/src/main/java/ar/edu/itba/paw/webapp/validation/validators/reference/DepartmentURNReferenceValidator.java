package ar.edu.itba.paw.webapp.validation.validators.reference;

import ar.edu.itba.paw.webapp.validation.constraints.reference.DepartmentURNReferenceConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DepartmentURNReferenceValidator implements ConstraintValidator<DepartmentURNReferenceConstraint, String> {
    @Override
    public void initialize(DepartmentURNReferenceConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null)
            return false;
        // URNValidator or ReferenceValidator
        return true;
    }
}
