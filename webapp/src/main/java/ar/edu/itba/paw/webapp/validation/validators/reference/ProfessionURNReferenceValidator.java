package ar.edu.itba.paw.webapp.validation.validators.reference;

import ar.edu.itba.paw.enums.Profession;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.validation.constraints.reference.ProfessionURNReferenceConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractFirstId;

public class ProfessionURNReferenceValidator implements ConstraintValidator<ProfessionURNReferenceConstraint, String> {
    @Override
    public void initialize(ProfessionURNReferenceConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String professionURN, ConstraintValidatorContext context) {
        if (professionURN == null || professionURN.trim().isEmpty())
            return true;
        try {
            Profession.fromId(extractFirstId(professionURN));
        } catch (NotFoundException e){
            return false;
        }
        return true;
    }
}
