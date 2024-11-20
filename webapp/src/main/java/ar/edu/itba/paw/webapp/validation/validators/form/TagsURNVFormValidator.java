package ar.edu.itba.paw.webapp.validation.validators.form;

import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.TagsURNFormConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class TagsURNVFormValidator implements ConstraintValidator<TagsURNFormConstraint, List<String>> {
    @Override
    public void initialize(TagsURNFormConstraint tagsURNConstraint) {
    }

    @Override
    public boolean isValid(List<String> tagURNs, ConstraintValidatorContext constraintValidatorContext) {
        if (tagURNs == null)
            return true;
        for (String urn : tagURNs)
            if (!URNValidator.validateURN(urn, "tags")) return false;
        return true;
    }
}