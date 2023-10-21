package ar.edu.itba.paw.webapp.form.validation.constraints;

import ar.edu.itba.paw.webapp.form.validation.validators.LanguageValidator;
import ar.edu.itba.paw.webapp.form.validation.validators.NeighborhoodSelectionValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Documented
@Constraint(validatedBy = LanguageValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface LanguageConstraint {
    String message() default "Invalid language";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
