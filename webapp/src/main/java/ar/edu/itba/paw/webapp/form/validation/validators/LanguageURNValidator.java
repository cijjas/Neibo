package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.form.validation.constraints.AmenityURNConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.LanguageURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LanguageURNValidator implements ConstraintValidator<LanguageURNConstraint, String> {
    @Override
    public void initialize(LanguageURNConstraint languageURNConstraint) {}

    @Override
    public boolean isValid(String languageURN, ConstraintValidatorContext constraintValidatorContext) {
        if(languageURN==null)
            return true;
        return URNValidator.validateURN(languageURN, "language");
    }
}