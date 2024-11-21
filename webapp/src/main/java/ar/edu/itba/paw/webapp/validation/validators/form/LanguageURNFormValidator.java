package ar.edu.itba.paw.webapp.validation.validators.form;

import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.LanguageURNFormConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LanguageURNFormValidator implements ConstraintValidator<LanguageURNFormConstraint, String> {

    @Override
    public void initialize(LanguageURNFormConstraint languageURNConstraint) {}

    @Override
    public boolean isValid(String languageURN, ConstraintValidatorContext constraintValidatorContext) {
        if (languageURN == null)
            return true;
        return URNValidator.validateURN(languageURN, "language");
    }
}