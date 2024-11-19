package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.LanguageURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LanguageURNValidator implements ConstraintValidator<LanguageURNConstraint, String> {
    @Override
    public void initialize(LanguageURNConstraint languageURNConstraint) {
    }

    @Override
    public boolean isValid(String languageURN, ConstraintValidatorContext constraintValidatorContext) {
        return URNValidator.validateURN(languageURN, "language");
    }
}