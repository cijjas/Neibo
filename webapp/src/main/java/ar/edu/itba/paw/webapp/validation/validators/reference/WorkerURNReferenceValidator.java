package ar.edu.itba.paw.webapp.validation.validators.reference;

import ar.edu.itba.paw.webapp.validation.constraints.reference.WorkerURNReferenceConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class WorkerURNReferenceValidator implements ConstraintValidator<WorkerURNReferenceConstraint, String> {
    @Override
    public void initialize(WorkerURNReferenceConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null)
            return true;
        // URNValidator or ReferenceValidator
        return true;
    }
}
