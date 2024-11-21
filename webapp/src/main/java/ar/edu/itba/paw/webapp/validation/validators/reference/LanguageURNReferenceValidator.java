package ar.edu.itba.paw.webapp.validation.validators.reference;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.validation.constraints.reference.LanguageURNReferenceConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractId;

public class LanguageURNReferenceValidator implements ConstraintValidator<LanguageURNReferenceConstraint, String> {

    @Override
    public void initialize(LanguageURNReferenceConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String languageURN, ConstraintValidatorContext context) {
        if (languageURN == null)
            return true;
        try {
            Language.fromId(extractId(languageURN));
        } catch (NotFoundException e){
            return false;
        }
        return true;
    }
}
