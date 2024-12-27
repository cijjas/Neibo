package ar.edu.itba.paw.webapp.validation.validators.urn;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.urn.LanguageURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;

public class LanguageURNValidator implements ConstraintValidator<LanguageURNConstraint, String> {

    @Override
    public void initialize(LanguageURNConstraint languageURNConstraint) {
    }

    @Override
    public boolean isValid(String languageURN, ConstraintValidatorContext constraintValidatorContext) {
        if (languageURN == null)
            return true;
        if (!URNValidator.validateURN(languageURN, "language"))
            return false;
        try {
            Language.fromId(extractFirstId(languageURN));
        } catch (NotFoundException e) {
            return false;
        }
        return true;
    }
}