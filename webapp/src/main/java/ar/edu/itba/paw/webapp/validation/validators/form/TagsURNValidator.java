package ar.edu.itba.paw.webapp.validation.validators.form;

import ar.edu.itba.paw.interfaces.services.TagService;
import ar.edu.itba.paw.models.TwoId;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.TagsURNConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractTwoId;

public class TagsURNValidator implements ConstraintValidator<TagsURNConstraint, List<String>> {

    @Autowired
    private TagService tagService;

    @Override
    public void initialize(TagsURNConstraint tagsURNConstraint) {}

    @Override
    public boolean isValid(List<String> tagURNs, ConstraintValidatorContext constraintValidatorContext) {
        if (tagURNs == null)
            return true;
        for (String urn : tagURNs) {
            if (!URNValidator.validateURN(urn, "tags"))
                return false;
            TwoId twoId = extractTwoId(urn);
            if (!tagService.findTag(twoId.getSecondId(), twoId.getFirstId()).isPresent())
                return false;
        }
        return true;
    }
}