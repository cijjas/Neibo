package ar.edu.itba.paw.webapp.validation.validators.reference;

import ar.edu.itba.paw.enums.Profession;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.validation.constraints.reference.ProfessionsURNReferenceConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractOptionalFirstId;

public class ProfessionsURNReferenceValidator implements ConstraintValidator<ProfessionsURNReferenceConstraint, List<String>> {

    @Override
    public void initialize(ProfessionsURNReferenceConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(List<String> professionURNs, ConstraintValidatorContext context) {
        if (professionURNs == null)
            return true;
        for (String professionURN : professionURNs) {
            Long professionId = extractOptionalFirstId(professionURN);
            if (professionId != null) {
                try {
                    Profession.fromId(professionId);
                } catch (NotFoundException e) {
                    return false;
                }
            }
        }
        return true;
    }
}
