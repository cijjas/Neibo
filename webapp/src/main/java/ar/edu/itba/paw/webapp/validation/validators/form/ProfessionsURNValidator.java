package ar.edu.itba.paw.webapp.validation.validators.form;

import ar.edu.itba.paw.enums.Profession;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.ProfessionsURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;

public class ProfessionsURNValidator implements ConstraintValidator<ProfessionsURNConstraint, List<String>> {

    @Override
    public void initialize(ProfessionsURNConstraint professionsURNConstraint) {
    }

    @Override
    public boolean isValid(List<String> professionURNs, ConstraintValidatorContext constraintValidatorContext) {
        if (professionURNs == null)
            return true;
        for (String professionURN : professionURNs) {
            if (!URNValidator.validateURN(professionURN, "professions"))
                return false;
            try {
                Profession.fromId(extractFirstId(professionURN));
            } catch (NotFoundException e) {
                return false;
            }
        }
        return true;
    }
}