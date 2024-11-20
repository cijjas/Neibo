package ar.edu.itba.paw.webapp.validation.validators.form;

import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.ProfessionsURNFormConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ProfessionsURNFormValidator implements ConstraintValidator<ProfessionsURNFormConstraint, String[]> {
    @Override
    public void initialize(ProfessionsURNFormConstraint professionsURNConstraint) {
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