package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.webapp.validation.constraints.LanguageIdConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LanguageIdValidator implements ConstraintValidator<LanguageIdConstraint, Long> {
    @Override
    public void initialize(LanguageIdConstraint languageConstraint) {

    }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {
        return Language.nullableFromId(id) != null;
    }
}
