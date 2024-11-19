package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.interfaces.services.NeighborhoodService;
import ar.edu.itba.paw.webapp.validation.constraints.LanguageConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;


public class LanguageValidator implements ConstraintValidator<LanguageConstraint, String> {
    @Autowired
    NeighborhoodService neighborhoodService;

    @Override
    public void initialize(LanguageConstraint languageConstraint) {

    }

    @Override
    public boolean isValid(String language, ConstraintValidatorContext constraintValidatorContext) {
        if (language == null) {
            //returns true so the invalid language message isn't displayed, the null will be caught by another validation
            return true;
        }

        if (Objects.equals(language, "Spanish") || Objects.equals(language, "English")) {
            return true;
        }

        constraintValidatorContext.disableDefaultConstraintViolation();
        constraintValidatorContext.buildConstraintViolationWithTemplate("Invalid language")
                .addConstraintViolation();
        return false;
    }
}
