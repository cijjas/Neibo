package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.interfaces.services.TagService;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.TagsURIConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class TagsURIValidator implements ConstraintValidator<TagsURIConstraint, List<String>> {

    @Autowired
    private TagService tagService;

    @Override
    public void initialize(TagsURIConstraint tagsURIConstraint) {
    }

    @Override
    public boolean isValid(List<String> tagURIs, ConstraintValidatorContext constraintValidatorContext) {
        if (tagURIs == null)
            return true;
        for (String urn : tagURIs)
            if (!URIValidator.validateOptionalURI(urn, Endpoint.TAGS))
                return false;
        return true;
    }
}