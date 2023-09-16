package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.form.validation.constraints.TagsConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TagsValidator implements
        ConstraintValidator<TagsConstraint, String> {

    private static final String TAG_PATTERN = "^[A-Za-z0-9_]+$";



    @Override
    public void initialize(TagsConstraint imageConstraint) {

    }

    @Override
    public boolean isValid(String tags, ConstraintValidatorContext context) {
        if (tags == null || tags.isEmpty()) {
            return true; // Null values are considered valid
        }

        String[] tagArray = tags.split(",");
        for (String tag : tagArray) {
            if (!tag.matches(TAG_PATTERN)) {
                String errorMessage = "Tag: <" + tag + "> is not valid. Tags should only contain letters, numbers, and underscores.";
                context.buildConstraintViolationWithTemplate(errorMessage)
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
                return false; // If any tag is invalid, return false
            }
        }

        return true; // All tags are valid
    }
}
