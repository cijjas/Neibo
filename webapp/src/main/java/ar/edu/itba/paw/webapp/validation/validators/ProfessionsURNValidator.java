package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.webapp.validation.constraints.ProfessionsURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ProfessionsURNValidator implements ConstraintValidator<ProfessionsURNConstraint, String[]> {
    @Override
    public void initialize(ProfessionsURNConstraint professionsURNConstraint) {
    }

    @Override
    public boolean isValid(String[] professionsURN, ConstraintValidatorContext constraintValidatorContext) {
        if (professionsURN == null)
            return true;
        for (String urn : professionsURN)
            if (!URNValidator.validateURN(urn, "professions")) return false;
        return true;
    }
}