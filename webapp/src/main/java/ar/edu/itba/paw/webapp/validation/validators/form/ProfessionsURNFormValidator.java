package ar.edu.itba.paw.webapp.validation.validators.form;

import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.ProfessionsURNFormConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ProfessionsURNFormValidator implements ConstraintValidator<ProfessionsURNFormConstraint, List<String>> {

    @Override
    public void initialize(ProfessionsURNFormConstraint professionsURNConstraint) {}

    @Override
    public boolean isValid(List<String> professionURNs, ConstraintValidatorContext constraintValidatorContext) {
        if (professionURNs == null)
            return true;
        for (String urn : professionURNs)
            if (!URNValidator.validateURN(urn, "professions"))
                return false;
        return true;
    }
}