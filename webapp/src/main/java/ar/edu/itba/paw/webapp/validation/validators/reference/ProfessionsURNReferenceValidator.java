package ar.edu.itba.paw.webapp.validation.validators.reference;

import ar.edu.itba.paw.webapp.validation.constraints.reference.ProfessionsURNReferenceConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ProfessionsURNReferenceValidator implements ConstraintValidator<ProfessionsURNReferenceConstraint, List<String>> {
    @Override
    public void initialize(ProfessionsURNReferenceConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        if (value == null)
            return true;
        // URNValidator or ReferenceValidator
        return true;
    }
}
