package ar.edu.itba.paw.webapp.validation.validators.urn;

import ar.edu.itba.paw.interfaces.services.TagService;
import ar.edu.itba.paw.models.TwoId;
import ar.edu.itba.paw.webapp.auth.FormAccessControlHelper;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.urn.TagsURNConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractTwoId;

public class TagsURNValidator implements ConstraintValidator<TagsURNConstraint, List<String>> {

    @Autowired
    private FormAccessControlHelper formAccessControlHelper;

    @Autowired
    private TagService tagService;

    @Override
    public void initialize(TagsURNConstraint tagsURNConstraint) {
    }

    @Override
    public boolean isValid(List<String> tagURNs, ConstraintValidatorContext constraintValidatorContext) {
        if (tagURNs == null)
            return true;
        for (String urn : tagURNs) {
            if (!URNValidator.validateURN(urn, Endpoint.TAGS))
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