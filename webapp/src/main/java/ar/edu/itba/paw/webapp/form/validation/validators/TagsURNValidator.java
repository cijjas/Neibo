package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.form.validation.constraints.TagsURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class TagsURNValidator implements ConstraintValidator<TagsURNConstraint, List<String>> {
    @Override
    public void initialize(TagsURNConstraint tagsURNConstraint) {}

    @Override
    public boolean isValid(List<String> tagURNs, ConstraintValidatorContext constraintValidatorContext) {
        System.out.println("Validating tags URNs: " + tagURNs);
        if(tagURNs == null || tagURNs.isEmpty())
            return true; //no tags is considered valid

        for (String urn : tagURNs)
            if (!URNValidator.validateURN(urn, "tags")) return false;
        return true;
    }
}