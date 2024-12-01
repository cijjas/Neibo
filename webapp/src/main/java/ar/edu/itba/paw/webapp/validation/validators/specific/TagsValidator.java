package ar.edu.itba.paw.webapp.validation.validators.specific;

import ar.edu.itba.paw.webapp.validation.constraints.specific.TagsConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Locale;

public class TagsValidator implements ConstraintValidator<TagsConstraint, String> {
    private static final String TAG_PATTERN = "^[A-Za-z0-9_]+$";
    private static final int MAX_TAG_LENGTH = 20;

    @Autowired
    private MessageSource messageSource;

    @Override
    public void initialize(TagsConstraint tagsConstraint) {
    }

    @Override
    public boolean isValid(String tag, ConstraintValidatorContext context) {
        if (tag == null)
            return false;

        if (!tag.matches(TAG_PATTERN)) {
            String errorMessage = "The tag '" + tag + "' is invalid. It must match the required pattern.";
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            return false; // If tag is invalid, return false
        }
        if (tag.length() > MAX_TAG_LENGTH) {
            String errorMessage = "The tag '" + tag + "' is too long. Maximum allowed length is " + MAX_TAG_LENGTH + " characters.";
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            return false;
        }

        // Check if the first letter is uppercase
        if (!Character.isUpperCase(tag.charAt(0))) {
            String errorMessage = "The tag '" + tag + "' must start with an uppercase letter.";
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            return false;
        }

        return true; // Tag is valid
    }

}
