package ar.edu.itba.paw.webapp.validation.validators.reference;

import ar.edu.itba.paw.interfaces.services.TagService;
import ar.edu.itba.paw.models.TwoId;
import ar.edu.itba.paw.webapp.validation.constraints.reference.TagsURNReferenceConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.List;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractTwoId;

public class TagsURNReferenceValidator implements ConstraintValidator<TagsURNReferenceConstraint, List<String>> {

    @Autowired
    private TagService tagService;

    @Override
    public void initialize(TagsURNReferenceConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(List<String> tagURNs, ConstraintValidatorContext context) {
        if (tagURNs == null)
            return true;
        for (String urn : tagURNs) {
            TwoId twoId = extractTwoId(urn);
            if (!tagService.findTag(twoId.getSecondId(), twoId.getFirstId()).isPresent())
                return false;
        }
        return true;
    }
}
