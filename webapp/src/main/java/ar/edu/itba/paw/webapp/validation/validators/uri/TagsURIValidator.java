package ar.edu.itba.paw.webapp.validation.validators.uri;

import ar.edu.itba.paw.interfaces.services.TagService;
import ar.edu.itba.paw.models.TwoId;
import ar.edu.itba.paw.webapp.auth.FormAccessControlHelper;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.uri.TagsURIConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractTwoId;

public class TagsURIValidator implements ConstraintValidator<TagsURIConstraint, List<String>> {

    @Autowired
    private FormAccessControlHelper formAccessControlHelper;

    @Autowired
    private TagService tagService;

    @Override
    public void initialize(TagsURIConstraint tagsURIConstraint) {
    }

    @Override
    public boolean isValid(List<String> tagURIs, ConstraintValidatorContext constraintValidatorContext) {
        if (tagURIs == null)
            return true;
        for (String urn : tagURIs) {
            if (!URIValidator.validateURI(urn, Endpoint.TAGS))
                return false;
            TwoId twoId = extractTwoId(urn);
            if (!tagService.findTag(twoId.getFirstId(), twoId.getSecondId()).isPresent())
                return false;
            if (!formAccessControlHelper.canReferenceNeighborhoodEntity(twoId.getFirstId())) {
                constraintValidatorContext.disableDefaultConstraintViolation();
                constraintValidatorContext.buildConstraintViolationWithTemplate(HttpStatus.FORBIDDEN.toString()).addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}