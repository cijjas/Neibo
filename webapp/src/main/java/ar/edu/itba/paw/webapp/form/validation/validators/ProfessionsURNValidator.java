package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.form.validation.constraints.LanguageURNConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.ProfessionsURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ProfessionsURNValidator implements ConstraintValidator<ProfessionsURNConstraint, String[]> {
    @Override
    public void initialize(ProfessionsURNConstraint professionsURNConstraint) {}

    @Override
    public boolean isValid(String[] professionsURN, ConstraintValidatorContext constraintValidatorContext) {
        for (String urn : professionsURN)
            if (!URNValidator.validateURN(urn, "professions")) return false;
        return true;
    }
}