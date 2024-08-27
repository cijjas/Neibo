package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.enums.Profession;
import ar.edu.itba.paw.webapp.form.validation.constraints.ProfessionsConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class ProfessionsSelectorValidator implements ConstraintValidator<ProfessionsConstraint, Long[]> {

    @Override
    public void initialize(ProfessionsConstraint professionsConstraint) {

    }

    @Override
    public boolean isValid(Long[] professions, ConstraintValidatorContext constraintValidatorContext) {
        if (professions == null)
            //true so invalid message isnt displayed, null caught by another validation
            return true;
        if (professions.length == 0)
            return false;

        for (Long id : professions) {
            int found = 0;
            for (Profession profession : Profession.values()) {
                if (profession.getId() == id) {
                    found = 1;
                    break;
                }
            }
            if (found == 0) {
                constraintValidatorContext.disableDefaultConstraintViolation();
                constraintValidatorContext.buildConstraintViolationWithTemplate("Invalid profession")
                        .addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}
