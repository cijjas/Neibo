package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.form.validation.constraints.TagsConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Locale;

public class TagsValidator implements
        ConstraintValidator<TagsConstraint, String> {

    private static final String TAG_PATTERN = "^[A-Za-z0-9_]+$";
    private static final int MAX_TAG_LENGTH = 20;
    @Autowired
    private MessageSource messageSource;

    @Override
    public void initialize(TagsConstraint tagsConstraint) {

    }

    @Override
    public boolean isValid(String tag, ConstraintValidatorContext context) {
        if (tag == null) {
            return false; // Null values are invalid
        }


        if (!tag.matches(TAG_PATTERN)) {
            String tagError1 = messageSource.getMessage("TagError1", null, Locale.getDefault());
            String tagError2 = messageSource.getMessage("TagError2", null, Locale.getDefault());

            String errorMessage = tagError1 + tag + tagError2;
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            return false; // If tag is invalid, return false
        }
        if (tag.length() > MAX_TAG_LENGTH) {
            String tagError3 = messageSource.getMessage("TagError3", null, Locale.getDefault());

            String errorMessage = tag + tagError3;
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            return false;
        }

        // Check if the first letter is uppercase
        if (!Character.isUpperCase(tag.charAt(0))) {
            String tagError4 = messageSource.getMessage("TagError4", null, Locale.getDefault());

            String errorMessage = tagError4 + tag;
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            return false;
        }


        return true; // Tag is valid
    }
}
