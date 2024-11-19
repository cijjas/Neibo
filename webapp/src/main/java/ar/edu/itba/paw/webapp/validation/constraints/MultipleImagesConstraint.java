package ar.edu.itba.paw.webapp.validation.constraints;

import ar.edu.itba.paw.webapp.validation.validators.MultipleImagesValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Documented
@Constraint(validatedBy = MultipleImagesValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface MultipleImagesConstraint {
    String message() default "Invalid images detected";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
