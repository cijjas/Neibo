package ar.edu.itba.paw.webapp.validation.validators.uri;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.uri.LanguageURIConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;

public class LanguageURIValidator implements ConstraintValidator<LanguageURIConstraint, String> {

    @Override
    public void initialize(LanguageURIConstraint languageURIConstraint) {
    }

    @Override
    public boolean isValid(String languageURI, ConstraintValidatorContext constraintValidatorContext) {
        if (languageURI == null)
            return true;
        if (!URIValidator.validateURI(languageURI, Endpoint.LANGUAGES))
            return false;
        try {
            Language.fromId(extractFirstId(languageURI));
        } catch (NotFoundException e) {
            return false;
        }
        return true;
    }
}